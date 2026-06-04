package com.footballplatform.app.repository;

import com.footballplatform.app.entity.BetRecommendation;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BetRecommendationRepository extends JpaRepository<BetRecommendation, Long> {

    List<BetRecommendation> findByMatch_IdOrderByDisplayOrderAsc(Long matchId);
}
