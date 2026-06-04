package com.footballplatform.app.repository;

import com.footballplatform.app.entity.AttackAnalysis;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AttackAnalysisRepository extends JpaRepository<AttackAnalysis, Long> {

    Optional<AttackAnalysis> findByMatch_Id(Long matchId);
}
