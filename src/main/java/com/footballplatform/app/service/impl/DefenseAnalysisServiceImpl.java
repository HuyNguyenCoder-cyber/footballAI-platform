package com.footballplatform.app.service.impl;

import com.footballplatform.app.dto.DefenseAnalysisDTO;
import com.footballplatform.app.entity.DefenseAnalysis;
import com.footballplatform.app.entity.Match;
import com.footballplatform.app.repository.DefenseAnalysisRepository;
import com.footballplatform.app.repository.MatchRepository;
import com.footballplatform.app.service.DefenseAnalysisService;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class DefenseAnalysisServiceImpl implements DefenseAnalysisService {

    private static final DateTimeFormatter MATCH_TIME_FORMATTER = DateTimeFormatter.ofPattern("dd MMM yyyy HH:mm");

    private final DefenseAnalysisRepository defenseAnalysisRepository;
    private final MatchRepository matchRepository;

    public DefenseAnalysisServiceImpl(DefenseAnalysisRepository defenseAnalysisRepository,
                                      MatchRepository matchRepository) {
        this.defenseAnalysisRepository = defenseAnalysisRepository;
        this.matchRepository = matchRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public List<DefenseAnalysisDTO> findAll() {
        return defenseAnalysisRepository.findAll().stream().map(this::toDto).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<DefenseAnalysisDTO> findById(Long id) {
        return defenseAnalysisRepository.findById(id).map(this::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<DefenseAnalysisDTO> findByMatchId(Long matchId) {
        return defenseAnalysisRepository.findByMatch_Id(matchId).map(this::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean isMatchAssignedToAnotherDefenseAnalysis(Long matchId, Long currentDefenseAnalysisId) {
        return defenseAnalysisRepository.findByMatch_Id(matchId)
                .map(existing -> currentDefenseAnalysisId == null || !existing.getId().equals(currentDefenseAnalysisId))
                .orElse(false);
    }

    @Override
    public DefenseAnalysisDTO create(DefenseAnalysisDTO dto) {
        if (isMatchAssignedToAnotherDefenseAnalysis(dto.getMatchId(), null)) {
            throw new RuntimeException("Trận đấu này đã có Defense Analysis.");
        }

        DefenseAnalysis entity = new DefenseAnalysis();
        applyDto(entity, dto);
        return toDto(defenseAnalysisRepository.save(entity));
    }

    @Override
    public DefenseAnalysisDTO update(DefenseAnalysisDTO dto) {
        DefenseAnalysis entity = defenseAnalysisRepository.findById(dto.getId())
                .orElseThrow(() -> new RuntimeException("DefenseAnalysis not found with id: " + dto.getId()));

        if (isMatchAssignedToAnotherDefenseAnalysis(dto.getMatchId(), dto.getId())) {
            throw new RuntimeException("Trận đấu này đã có Defense Analysis.");
        }

        applyDto(entity, dto);
        return toDto(defenseAnalysisRepository.save(entity));
    }

    @Override
    public void delete(Long id) {
        if (!defenseAnalysisRepository.existsById(id)) {
            throw new RuntimeException("DefenseAnalysis not found with id: " + id);
        }
        defenseAnalysisRepository.deleteById(id);
    }

    private void applyDto(DefenseAnalysis entity, DefenseAnalysisDTO dto) {
        Match match = matchRepository.findById(dto.getMatchId())
                .orElseThrow(() -> new RuntimeException("Match not found with id: " + dto.getMatchId()));

        entity.setMatch(match);
        entity.setTeamAGoalsConceded(dto.getTeamAGoalsConceded());
        entity.setTeamAGoalsConcededPerMatch(dto.getTeamAGoalsConcededPerMatch());
        entity.setTeamACleanSheets(dto.getTeamACleanSheets());
        entity.setTeamACleanSheetRate(dto.getTeamACleanSheetRate());
        entity.setTeamAConcedingRate(dto.getTeamAConcedingRate());
        entity.setTeamAXgaPerMatch(dto.getTeamAXgaPerMatch());
        entity.setTeamAShotsConcededPerMatch(dto.getTeamAShotsConcededPerMatch());
        entity.setTeamADefenceIndex(dto.getTeamADefenceIndex());
        entity.setTeamBGoalsConceded(dto.getTeamBGoalsConceded());
        entity.setTeamBGoalsConcededPerMatch(dto.getTeamBGoalsConcededPerMatch());
        entity.setTeamBCleanSheets(dto.getTeamBCleanSheets());
        entity.setTeamBCleanSheetRate(dto.getTeamBCleanSheetRate());
        entity.setTeamBConcedingRate(dto.getTeamBConcedingRate());
        entity.setTeamBXgaPerMatch(dto.getTeamBXgaPerMatch());
        entity.setTeamBShotsConcededPerMatch(dto.getTeamBShotsConcededPerMatch());
        entity.setTeamBDefenceIndex(dto.getTeamBDefenceIndex());
        entity.setAnalysis(dto.getAnalysis().trim());
    }

    private DefenseAnalysisDTO toDto(DefenseAnalysis entity) {
        return DefenseAnalysisDTO.builder()
                .id(entity.getId())
                .matchId(entity.getMatch() != null ? entity.getMatch().getId() : null)
                .teamAGoalsConceded(entity.getTeamAGoalsConceded())
                .teamAGoalsConcededPerMatch(entity.getTeamAGoalsConcededPerMatch())
                .teamACleanSheets(entity.getTeamACleanSheets())
                .teamACleanSheetRate(entity.getTeamACleanSheetRate())
                .teamAConcedingRate(entity.getTeamAConcedingRate())
                .teamAXgaPerMatch(entity.getTeamAXgaPerMatch())
                .teamAShotsConcededPerMatch(entity.getTeamAShotsConcededPerMatch())
                .teamADefenceIndex(entity.getTeamADefenceIndex())
                .teamBGoalsConceded(entity.getTeamBGoalsConceded())
                .teamBGoalsConcededPerMatch(entity.getTeamBGoalsConcededPerMatch())
                .teamBCleanSheets(entity.getTeamBCleanSheets())
                .teamBCleanSheetRate(entity.getTeamBCleanSheetRate())
                .teamBConcedingRate(entity.getTeamBConcedingRate())
                .teamBXgaPerMatch(entity.getTeamBXgaPerMatch())
                .teamBShotsConcededPerMatch(entity.getTeamBShotsConcededPerMatch())
                .teamBDefenceIndex(entity.getTeamBDefenceIndex())
                .analysis(entity.getAnalysis())
                .matchLabel(buildMatchLabel(entity.getMatch()))
                .matchStatus(entity.getMatch() != null && entity.getMatch().getStatus() != null
                        ? entity.getMatch().getStatus().name()
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
