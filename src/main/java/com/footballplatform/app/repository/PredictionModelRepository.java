package com.footballplatform.app.repository;

import com.footballplatform.app.entity.PredictionModel;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PredictionModelRepository extends JpaRepository<PredictionModel, Long> {

    Optional<PredictionModel> findByMatch_Id(Long matchId);
}
