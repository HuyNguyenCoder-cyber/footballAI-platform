package com.footballplatform.app.repository;

import com.footballplatform.app.entity.SquadAnalysis;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SquadAnalysisRepository extends JpaRepository<SquadAnalysis, Long> {

    Optional<SquadAnalysis> findByMatch_Id(Long matchId);
}
