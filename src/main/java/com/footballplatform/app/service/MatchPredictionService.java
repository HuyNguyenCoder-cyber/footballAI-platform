package com.footballplatform.app.service;

import com.footballplatform.app.dto.MatchPredictionDTO;
import java.util.List;
import java.util.Optional;

public interface MatchPredictionService {

    List<MatchPredictionDTO> findAll();

    Optional<MatchPredictionDTO> findById(Long id);

    Optional<MatchPredictionDTO> findByMatchId(Long matchId);

    boolean isMatchAssignedToAnotherPrediction(Long matchId, Long currentPredictionId);

    MatchPredictionDTO create(MatchPredictionDTO dto);

    MatchPredictionDTO update(MatchPredictionDTO dto);

    void delete(Long id);
}
