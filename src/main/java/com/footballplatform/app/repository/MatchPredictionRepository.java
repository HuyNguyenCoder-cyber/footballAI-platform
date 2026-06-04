package com.footballplatform.app.repository;

import com.footballplatform.app.entity.MatchPrediction;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MatchPredictionRepository extends JpaRepository<MatchPrediction, Long> {

    Optional<MatchPrediction> findByMatch_Id(Long matchId);
}
