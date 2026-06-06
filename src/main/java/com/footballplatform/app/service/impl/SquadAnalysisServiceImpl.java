package com.footballplatform.app.service.impl;

import com.footballplatform.app.dto.SquadAnalysisDTO;
import com.footballplatform.app.entity.Match;
import com.footballplatform.app.entity.SquadAnalysis;
import com.footballplatform.app.repository.MatchRepository;
import com.footballplatform.app.repository.SquadAnalysisRepository;
import com.footballplatform.app.service.SquadAnalysisService;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class SquadAnalysisServiceImpl implements SquadAnalysisService {

    private static final DateTimeFormatter MATCH_TIME_FORMATTER = DateTimeFormatter.ofPattern("dd MMM yyyy HH:mm");

    private final SquadAnalysisRepository squadAnalysisRepository;
    private final MatchRepository matchRepository;

    public SquadAnalysisServiceImpl(SquadAnalysisRepository squadAnalysisRepository, MatchRepository matchRepository) {
        this.squadAnalysisRepository = squadAnalysisRepository;
        this.matchRepository = matchRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public List<SquadAnalysisDTO> findAll() {
        return squadAnalysisRepository.findAll().stream().map(this::toDto).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<SquadAnalysisDTO> findById(Long id) {
        return squadAnalysisRepository.findById(id).map(this::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<SquadAnalysisDTO> findByMatchId(Long matchId) {
        return squadAnalysisRepository.findByMatch_Id(matchId).map(this::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean isMatchAssignedToAnotherSquadAnalysis(Long matchId, Long currentSquadAnalysisId) {
        return squadAnalysisRepository.findByMatch_Id(matchId)
                .map(existing -> currentSquadAnalysisId == null || !existing.getId().equals(currentSquadAnalysisId))
                .orElse(false);
    }

    @Override
    public SquadAnalysisDTO create(SquadAnalysisDTO dto) {
        if (isMatchAssignedToAnotherSquadAnalysis(dto.getMatchId(), null)) {
            throw new RuntimeException("Trận đấu này đã có Squad Analysis.");
        }

        SquadAnalysis entity = new SquadAnalysis();
        applyDto(entity, dto);
        return toDto(squadAnalysisRepository.save(entity));
    }

    @Override
    public SquadAnalysisDTO update(SquadAnalysisDTO dto) {
        SquadAnalysis entity = squadAnalysisRepository.findById(dto.getId())
                .orElseThrow(() -> new RuntimeException("SquadAnalysis not found with id: " + dto.getId()));

        if (isMatchAssignedToAnotherSquadAnalysis(dto.getMatchId(), dto.getId())) {
            throw new RuntimeException("Trận đấu này đã có Squad Analysis.");
        }

        applyDto(entity, dto);
        return toDto(squadAnalysisRepository.save(entity));
    }

    @Override
    public void delete(Long id) {
        if (!squadAnalysisRepository.existsById(id)) {
            throw new RuntimeException("SquadAnalysis not found with id: " + id);
        }
        squadAnalysisRepository.deleteById(id);
    }

    private void applyDto(SquadAnalysis entity, SquadAnalysisDTO dto) {
        Match match = matchRepository.findById(dto.getMatchId())
                .orElseThrow(() -> new RuntimeException("Match not found with id: " + dto.getMatchId()));

        entity.setMatch(match);
        entity.setTeamAContent(dto.getTeamAContent().trim());
        entity.setTeamBContent(dto.getTeamBContent().trim());
    }

    private SquadAnalysisDTO toDto(SquadAnalysis entity) {
        return SquadAnalysisDTO.builder()
                .id(entity.getId())
                .matchId(entity.getMatch() != null ? entity.getMatch().getId() : null)
                .teamAContent(entity.getTeamAContent())
                .teamBContent(entity.getTeamBContent())
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
