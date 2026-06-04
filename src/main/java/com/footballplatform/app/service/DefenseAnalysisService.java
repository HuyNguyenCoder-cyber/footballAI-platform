package com.footballplatform.app.service;

import com.footballplatform.app.dto.DefenseAnalysisDTO;
import java.util.List;
import java.util.Optional;

public interface DefenseAnalysisService {

    List<DefenseAnalysisDTO> findAll();

    Optional<DefenseAnalysisDTO> findById(Long id);

    Optional<DefenseAnalysisDTO> findByMatchId(Long matchId);

    boolean isMatchAssignedToAnotherDefenseAnalysis(Long matchId, Long currentDefenseAnalysisId);

    DefenseAnalysisDTO create(DefenseAnalysisDTO dto);

    DefenseAnalysisDTO update(DefenseAnalysisDTO dto);

    void delete(Long id);
}
