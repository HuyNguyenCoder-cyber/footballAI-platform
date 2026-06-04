package com.footballplatform.app.service;

import com.footballplatform.app.dto.BetRecommendationDTO;
import java.util.List;
import java.util.Optional;

public interface BetRecommendationService {

    List<BetRecommendationDTO> findAll();

    Optional<BetRecommendationDTO> findById(Long id);

    List<BetRecommendationDTO> findByMatchId(Long matchId);

    BetRecommendationDTO create(BetRecommendationDTO dto);

    BetRecommendationDTO update(BetRecommendationDTO dto);

    void delete(Long id);
}
