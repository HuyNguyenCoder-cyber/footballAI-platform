package com.footballplatform.app.service.impl;

import com.footballplatform.app.dto.HeadToHeadDTO;
import com.footballplatform.app.entity.HeadToHead;
import com.footballplatform.app.entity.Match;
import com.footballplatform.app.repository.HeadToHeadRepository;
import com.footballplatform.app.repository.MatchRepository;
import com.footballplatform.app.service.CacheInvalidationService;
import com.footballplatform.app.service.HeadToHeadService;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class HeadToHeadServiceImpl implements HeadToHeadService {

    private static final DateTimeFormatter MATCH_TIME_FORMATTER = DateTimeFormatter.ofPattern("dd MMM yyyy HH:mm");

    private final HeadToHeadRepository headToHeadRepository;
    private final MatchRepository matchRepository;
    private final CacheInvalidationService cacheInvalidationService;

    public HeadToHeadServiceImpl(HeadToHeadRepository headToHeadRepository,
                                 MatchRepository matchRepository,
                                 CacheInvalidationService cacheInvalidationService) {
        this.headToHeadRepository = headToHeadRepository;
        this.matchRepository = matchRepository;
        this.cacheInvalidationService = cacheInvalidationService;
    }

    @Override
    @Transactional(readOnly = true)
    public List<HeadToHeadDTO> findAll() {
        return headToHeadRepository.findAll().stream().map(this::toDto).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<HeadToHeadDTO> findById(Long id) {
        return headToHeadRepository.findById(id).map(this::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<HeadToHeadDTO> findByMatchId(Long matchId) {
        return headToHeadRepository.findByMatch_Id(matchId).map(this::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean isMatchAssignedToAnotherHeadToHead(Long matchId, Long currentHeadToHeadId) {
        return headToHeadRepository.findByMatch_Id(matchId)
                .map(existing -> currentHeadToHeadId == null || !existing.getId().equals(currentHeadToHeadId))
                .orElse(false);
    }

    @Override
    public HeadToHeadDTO create(HeadToHeadDTO dto) {
        if (isMatchAssignedToAnotherHeadToHead(dto.getMatchId(), null)) {
            throw new RuntimeException("Trận đấu này đã có Head To Head.");
        }

        HeadToHead entity = new HeadToHead();
        applyDto(entity, dto);
        HeadToHeadDTO saved = toDto(headToHeadRepository.save(entity));
        cacheInvalidationService.evictMatchAnalysisCache(saved.getMatchId());
        return saved;
    }

    @Override
    public HeadToHeadDTO update(HeadToHeadDTO dto) {
        HeadToHead entity = headToHeadRepository.findById(dto.getId())
                .orElseThrow(() -> new RuntimeException("HeadToHead not found with id: " + dto.getId()));

        if (isMatchAssignedToAnotherHeadToHead(dto.getMatchId(), dto.getId())) {
            throw new RuntimeException("Trận đấu này đã có Head To Head.");
        }

        applyDto(entity, dto);
        HeadToHeadDTO saved = toDto(headToHeadRepository.save(entity));
        cacheInvalidationService.evictMatchAnalysisCache(saved.getMatchId());
        return saved;
    }

    @Override
    public void delete(Long id) {
        HeadToHead entity = headToHeadRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("HeadToHead not found with id: " + id));
        Long matchId = entity.getMatch() != null ? entity.getMatch().getId() : null;
        headToHeadRepository.delete(entity);
        cacheInvalidationService.evictMatchAnalysisCache(matchId);
    }

    private void applyDto(HeadToHead entity, HeadToHeadDTO dto) {
        Match match = matchRepository.findById(dto.getMatchId())
                .orElseThrow(() -> new RuntimeException("Match not found with id: " + dto.getMatchId()));

        entity.setMatch(match);
        entity.setH2hMatchesText(dto.getH2hMatchesText().trim());
        entity.setTotalMatches(dto.getTotalMatches());
        entity.setTeamAWins(dto.getTeamAWins());
        entity.setDraws(dto.getDraws());
        entity.setTeamBWins(dto.getTeamBWins());
        entity.setTeamAGoals(dto.getTeamAGoals());
        entity.setTeamBGoals(dto.getTeamBGoals());
        entity.setTotalGoals(dto.getTotalGoals());
        entity.setAverageGoalsPerMatch(dto.getAverageGoalsPerMatch());
        entity.setTeamACleanSheets(dto.getTeamACleanSheets());
        entity.setTeamBCleanSheets(dto.getTeamBCleanSheets());
        entity.setAnalysis(dto.getAnalysis().trim());
    }

    private HeadToHeadDTO toDto(HeadToHead entity) {
        return HeadToHeadDTO.builder()
                .id(entity.getId())
                .matchId(entity.getMatch() != null ? entity.getMatch().getId() : null)
                .h2hMatchesText(entity.getH2hMatchesText())
                .totalMatches(entity.getTotalMatches())
                .teamAWins(entity.getTeamAWins())
                .draws(entity.getDraws())
                .teamBWins(entity.getTeamBWins())
                .teamAGoals(entity.getTeamAGoals())
                .teamBGoals(entity.getTeamBGoals())
                .totalGoals(entity.getTotalGoals())
                .averageGoalsPerMatch(entity.getAverageGoalsPerMatch())
                .teamACleanSheets(entity.getTeamACleanSheets())
                .teamBCleanSheets(entity.getTeamBCleanSheets())
                .analysis(entity.getAnalysis())
                .matchLabel(buildMatchLabel(entity.getMatch()))
                .matchStatus(entity.getMatch() != null && entity.getMatch().getStatus() != null
                        ? entity.getMatch().getStatus().getDisplayName()
                        : "")
                .build();
    }

    private String buildMatchLabel(Match match) {
        if (match == null) {
            return "";
        }

        String teamA = match.getTeamA() == null ? "" : match.getTeamA().trim();
        String teamB = match.getTeamB() == null ? "" : match.getTeamB().trim();
        String matchTime = match.getMatchTime() != null ? match.getMatchTime().format(MATCH_TIME_FORMATTER) : "";
        return teamA + " vs " + teamB + (matchTime.isEmpty() ? "" : " | " + matchTime);
    }
}
