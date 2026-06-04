package com.footballplatform.app.service;

import com.footballplatform.app.dto.AttackAnalysisDTO;
import java.util.List;
import java.util.Optional;

public interface AttackAnalysisService {

    List<AttackAnalysisDTO> findAll();

    Optional<AttackAnalysisDTO> findById(Long id);

    Optional<AttackAnalysisDTO> findByMatchId(Long matchId);

    boolean isMatchAssignedToAnotherAttackAnalysis(Long matchId, Long currentAttackAnalysisId);

    AttackAnalysisDTO create(AttackAnalysisDTO dto);

    AttackAnalysisDTO update(AttackAnalysisDTO dto);

    void delete(Long id);
}
