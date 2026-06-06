package com.footballplatform.app.service.impl;

import com.footballplatform.app.dto.AttackAnalysisDTO;
import com.footballplatform.app.entity.AttackAnalysis;
import com.footballplatform.app.entity.Match;
import com.footballplatform.app.repository.AttackAnalysisRepository;
import com.footballplatform.app.repository.MatchRepository;
import com.footballplatform.app.service.AttackAnalysisService;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class AttackAnalysisServiceImpl implements AttackAnalysisService {

    private static final DateTimeFormatter MATCH_TIME_FORMATTER = DateTimeFormatter.ofPattern("dd MMM yyyy HH:mm");

    private final AttackAnalysisRepository attackAnalysisRepository;
    private final MatchRepository matchRepository;

    public AttackAnalysisServiceImpl(AttackAnalysisRepository attackAnalysisRepository,
                                     MatchRepository matchRepository) {
        this.attackAnalysisRepository = attackAnalysisRepository;
        this.matchRepository = matchRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public List<AttackAnalysisDTO> findAll() {
        return attackAnalysisRepository.findAll().stream().map(this::toDto).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<AttackAnalysisDTO> findById(Long id) {
        return attackAnalysisRepository.findById(id).map(this::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<AttackAnalysisDTO> findByMatchId(Long matchId) {
        return attackAnalysisRepository.findByMatch_Id(matchId).map(this::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean isMatchAssignedToAnotherAttackAnalysis(Long matchId, Long currentAttackAnalysisId) {
        return attackAnalysisRepository.findByMatch_Id(matchId)
                .map(existing -> currentAttackAnalysisId == null || !existing.getId().equals(currentAttackAnalysisId))
                .orElse(false);
    }

    @Override
    public AttackAnalysisDTO create(AttackAnalysisDTO dto) {
        if (isMatchAssignedToAnotherAttackAnalysis(dto.getMatchId(), null)) {
            throw new RuntimeException("Trận đấu này đã có Attack Analysis.");
        }

        AttackAnalysis entity = new AttackAnalysis();
        applyDto(entity, dto);
        return toDto(attackAnalysisRepository.save(entity));
    }

    @Override
    public AttackAnalysisDTO update(AttackAnalysisDTO dto) {
        AttackAnalysis entity = attackAnalysisRepository.findById(dto.getId())
                .orElseThrow(() -> new RuntimeException("AttackAnalysis not found with id: " + dto.getId()));

        if (isMatchAssignedToAnotherAttackAnalysis(dto.getMatchId(), dto.getId())) {
            throw new RuntimeException("Trận đấu này đã có Attack Analysis.");
        }

        applyDto(entity, dto);
        return toDto(attackAnalysisRepository.save(entity));
    }

    @Override
    public void delete(Long id) {
        if (!attackAnalysisRepository.existsById(id)) {
            throw new RuntimeException("AttackAnalysis not found with id: " + id);
        }
        attackAnalysisRepository.deleteById(id);
    }

    private void applyDto(AttackAnalysis entity, AttackAnalysisDTO dto) {
        Match match = matchRepository.findById(dto.getMatchId())
                .orElseThrow(() -> new RuntimeException("Match not found with id: " + dto.getMatchId()));

        entity.setMatch(match);
        entity.setTeamAGoals(dto.getTeamAGoals());
        entity.setTeamAGoalsPerMatch(dto.getTeamAGoalsPerMatch());
        entity.setTeamAScoringRate(dto.getTeamAScoringRate());
        entity.setTeamAXgPerMatch(dto.getTeamAXgPerMatch());
        entity.setTeamABigChances(dto.getTeamABigChances());
        entity.setTeamAShotsPerMatch(dto.getTeamAShotsPerMatch());
        entity.setTeamAConversionRate(dto.getTeamAConversionRate());
        entity.setTeamAAttackIndex(dto.getTeamAAttackIndex());
        entity.setTeamBGoals(dto.getTeamBGoals());
        entity.setTeamBGoalsPerMatch(dto.getTeamBGoalsPerMatch());
        entity.setTeamBScoringRate(dto.getTeamBScoringRate());
        entity.setTeamBXgPerMatch(dto.getTeamBXgPerMatch());
        entity.setTeamBBigChances(dto.getTeamBBigChances());
        entity.setTeamBShotsPerMatch(dto.getTeamBShotsPerMatch());
        entity.setTeamBConversionRate(dto.getTeamBConversionRate());
        entity.setTeamBAttackIndex(dto.getTeamBAttackIndex());
        entity.setAnalysis(dto.getAnalysis().trim());
    }

    private AttackAnalysisDTO toDto(AttackAnalysis entity) {
        return AttackAnalysisDTO.builder()
                .id(entity.getId())
                .matchId(entity.getMatch() != null ? entity.getMatch().getId() : null)
                .teamAGoals(entity.getTeamAGoals())
                .teamAGoalsPerMatch(entity.getTeamAGoalsPerMatch())
                .teamAScoringRate(entity.getTeamAScoringRate())
                .teamAXgPerMatch(entity.getTeamAXgPerMatch())
                .teamABigChances(entity.getTeamABigChances())
                .teamAShotsPerMatch(entity.getTeamAShotsPerMatch())
                .teamAConversionRate(entity.getTeamAConversionRate())
                .teamAAttackIndex(entity.getTeamAAttackIndex())
                .teamBGoals(entity.getTeamBGoals())
                .teamBGoalsPerMatch(entity.getTeamBGoalsPerMatch())
                .teamBScoringRate(entity.getTeamBScoringRate())
                .teamBXgPerMatch(entity.getTeamBXgPerMatch())
                .teamBBigChances(entity.getTeamBBigChances())
                .teamBShotsPerMatch(entity.getTeamBShotsPerMatch())
                .teamBConversionRate(entity.getTeamBConversionRate())
                .teamBAttackIndex(entity.getTeamBAttackIndex())
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
