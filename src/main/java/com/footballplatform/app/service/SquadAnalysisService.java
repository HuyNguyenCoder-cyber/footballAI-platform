package com.footballplatform.app.service;

import com.footballplatform.app.dto.SquadAnalysisDTO;
import java.util.List;
import java.util.Optional;

public interface SquadAnalysisService {

    List<SquadAnalysisDTO> findAll();

    Optional<SquadAnalysisDTO> findById(Long id);

    Optional<SquadAnalysisDTO> findByMatchId(Long matchId);

    boolean isMatchAssignedToAnotherSquadAnalysis(Long matchId, Long currentSquadAnalysisId);

    SquadAnalysisDTO create(SquadAnalysisDTO dto);

    SquadAnalysisDTO update(SquadAnalysisDTO dto);

    void delete(Long id);
}
