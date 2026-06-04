package com.footballplatform.app.service.impl;

import com.footballplatform.app.dto.MatchPredictionDTO;
import com.footballplatform.app.entity.Match;
import com.footballplatform.app.entity.MatchPrediction;
import com.footballplatform.app.repository.MatchPredictionRepository;
import com.footballplatform.app.repository.MatchRepository;
import com.footballplatform.app.service.MatchPredictionService;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class MatchPredictionServiceImpl implements MatchPredictionService {

    private static final DateTimeFormatter MATCH_TIME_FORMATTER = DateTimeFormatter.ofPattern("dd MMM yyyy HH:mm");

    private final MatchPredictionRepository matchPredictionRepository;
    private final MatchRepository matchRepository;

    public MatchPredictionServiceImpl(MatchPredictionRepository matchPredictionRepository,
                                      MatchRepository matchRepository) {
        this.matchPredictionRepository = matchPredictionRepository;
        this.matchRepository = matchRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public List<MatchPredictionDTO> findAll() {
        return matchPredictionRepository.findAll().stream().map(this::toDto).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<MatchPredictionDTO> findById(Long id) {
        return matchPredictionRepository.findById(id).map(this::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<MatchPredictionDTO> findByMatchId(Long matchId) {
        return matchPredictionRepository.findByMatch_Id(matchId).map(this::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean isMatchAssignedToAnotherPrediction(Long matchId, Long currentPredictionId) {
        return matchPredictionRepository.findByMatch_Id(matchId)
                .map(existing -> currentPredictionId == null || !existing.getId().equals(currentPredictionId))
                .orElse(false);
    }

    @Override
    public MatchPredictionDTO create(MatchPredictionDTO dto) {
        if (isMatchAssignedToAnotherPrediction(dto.getMatchId(), null)) {
            throw new RuntimeException("Trận đấu này đã có AI Prediction.");
        }
        MatchPrediction entity = new MatchPrediction();
        applyDto(entity, dto);
        return toDto(matchPredictionRepository.save(entity));
    }

    @Override
    public MatchPredictionDTO update(MatchPredictionDTO dto) {
        MatchPrediction entity = matchPredictionRepository.findById(dto.getId())
                .orElseThrow(() -> new RuntimeException("MatchPrediction not found with id: " + dto.getId()));
        if (isMatchAssignedToAnotherPrediction(dto.getMatchId(), dto.getId())) {
            throw new RuntimeException("Trận đấu này đã có AI Prediction.");
        }
        applyDto(entity, dto);
        return toDto(matchPredictionRepository.save(entity));
    }

    @Override
    public void delete(Long id) {
        if (!matchPredictionRepository.existsById(id)) {
            throw new RuntimeException("MatchPrediction not found with id: " + id);
        }
        matchPredictionRepository.deleteById(id);
    }

    private void applyDto(MatchPrediction entity, MatchPredictionDTO dto) {
        Match match = matchRepository.findById(dto.getMatchId())
                .orElseThrow(() -> new RuntimeException("Match not found with id: " + dto.getMatchId()));
        entity.setMatch(match);
        entity.setScorePrediction(dto.getScorePrediction().trim());
        entity.setConfidence(dto.getConfidence());
    }

    private MatchPredictionDTO toDto(MatchPrediction entity) {
        return MatchPredictionDTO.builder()
                .id(entity.getId())
                .matchId(entity.getMatch() != null ? entity.getMatch().getId() : null)
                .scorePrediction(entity.getScorePrediction())
                .confidence(entity.getConfidence())
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
