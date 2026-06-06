package com.footballplatform.app.service;

import com.footballplatform.app.dto.AIInsightDTO;
import java.util.List;
import java.util.Optional;

public interface AIInsightService {

    List<AIInsightDTO> findAll();

    Optional<AIInsightDTO> findById(Long id);

    Optional<AIInsightDTO> findByMatchId(Long matchId);

    boolean isMatchAssignedToAnotherAIInsight(Long matchId, Long currentAIInsightId);

    AIInsightDTO create(AIInsightDTO dto);

    AIInsightDTO update(AIInsightDTO dto);

    void delete(Long id);
}
