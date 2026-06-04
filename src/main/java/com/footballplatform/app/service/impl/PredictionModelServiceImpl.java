package com.footballplatform.app.service.impl;

import com.footballplatform.app.dto.PredictionModelDTO;
import com.footballplatform.app.entity.Match;
import com.footballplatform.app.entity.PredictionModel;
import com.footballplatform.app.repository.MatchRepository;
import com.footballplatform.app.repository.PredictionModelRepository;
import com.footballplatform.app.service.PredictionModelService;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class PredictionModelServiceImpl implements PredictionModelService {

    private static final DateTimeFormatter MATCH_TIME_FORMATTER = DateTimeFormatter.ofPattern("dd MMM yyyy HH:mm");

    private final PredictionModelRepository predictionModelRepository;
    private final MatchRepository matchRepository;

    public PredictionModelServiceImpl(PredictionModelRepository predictionModelRepository,
                                      MatchRepository matchRepository) {
        this.predictionModelRepository = predictionModelRepository;
        this.matchRepository = matchRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public List<PredictionModelDTO> findAll() {
        return predictionModelRepository.findAll().stream().map(this::toDto).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<PredictionModelDTO> findById(Long id) {
        return predictionModelRepository.findById(id).map(this::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<PredictionModelDTO> findByMatchId(Long matchId) {
        return predictionModelRepository.findByMatch_Id(matchId).map(this::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean isMatchAssignedToAnotherPredictionModel(Long matchId, Long currentPredictionModelId) {
        return predictionModelRepository.findByMatch_Id(matchId)
                .map(existing -> currentPredictionModelId == null || !existing.getId().equals(currentPredictionModelId))
                .orElse(false);
    }

    @Override
    public PredictionModelDTO create(PredictionModelDTO dto) {
        validateProbabilityTotal(dto);
        if (isMatchAssignedToAnotherPredictionModel(dto.getMatchId(), null)) {
            throw new RuntimeException("Tráº­n Ä‘áº¥u nÃ y Ä‘Ã£ cÃ³ Prediction Model.");
        }

        PredictionModel entity = new PredictionModel();
        applyDto(entity, dto);
        return toDto(predictionModelRepository.save(entity));
    }

    @Override
    public PredictionModelDTO update(PredictionModelDTO dto) {
        validateProbabilityTotal(dto);
        PredictionModel entity = predictionModelRepository.findById(dto.getId())
                .orElseThrow(() -> new RuntimeException("PredictionModel not found with id: " + dto.getId()));
        if (isMatchAssignedToAnotherPredictionModel(dto.getMatchId(), dto.getId())) {
            throw new RuntimeException("Tráº­n Ä‘áº¥u nÃ y Ä‘Ã£ cÃ³ Prediction Model.");
        }

        applyDto(entity, dto);
        return toDto(predictionModelRepository.save(entity));
    }

    @Override
    public void delete(Long id) {
        if (!predictionModelRepository.existsById(id)) {
            throw new RuntimeException("PredictionModel not found with id: " + id);
        }
        predictionModelRepository.deleteById(id);
    }

    private void applyDto(PredictionModel entity, PredictionModelDTO dto) {
        Match match = matchRepository.findById(dto.getMatchId())
                .orElseThrow(() -> new RuntimeException("Match not found with id: " + dto.getMatchId()));

        entity.setMatch(match);
        entity.setTeamAWinProbability(dto.getTeamAWinProbability());
        entity.setDrawProbability(dto.getDrawProbability());
        entity.setTeamBWinProbability(dto.getTeamBWinProbability());
        entity.setDataConfidence(dto.getDataConfidence());
    }

    private PredictionModelDTO toDto(PredictionModel entity) {
        return PredictionModelDTO.builder()
                .id(entity.getId())
                .matchId(entity.getMatch() != null ? entity.getMatch().getId() : null)
                .teamAWinProbability(entity.getTeamAWinProbability())
                .drawProbability(entity.getDrawProbability())
                .teamBWinProbability(entity.getTeamBWinProbability())
                .dataConfidence(entity.getDataConfidence())
                .matchLabel(buildMatchLabel(entity.getMatch()))
                .matchStatus(entity.getMatch() != null && entity.getMatch().getStatus() != null
                        ? entity.getMatch().getStatus().name()
                        : "")
                .build();
    }

    private void validateProbabilityTotal(PredictionModelDTO dto) {
        int total = dto.getTeamAWinProbability() + dto.getDrawProbability() + dto.getTeamBWinProbability();
        if (total != 100) {
            throw new RuntimeException("Tá»•ng xÃ¡c suáº¥t tháº¯ng, hÃ²a vÃ  thua pháº£i báº±ng 100%");
        }
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
