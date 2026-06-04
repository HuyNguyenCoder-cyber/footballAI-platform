package com.footballplatform.app.service;

import com.footballplatform.app.dto.PredictionModelDTO;
import java.util.List;
import java.util.Optional;

public interface PredictionModelService {

    List<PredictionModelDTO> findAll();

    Optional<PredictionModelDTO> findById(Long id);

    Optional<PredictionModelDTO> findByMatchId(Long matchId);

    boolean isMatchAssignedToAnotherPredictionModel(Long matchId, Long currentPredictionModelId);

    PredictionModelDTO create(PredictionModelDTO dto);

    PredictionModelDTO update(PredictionModelDTO dto);

    void delete(Long id);
}
