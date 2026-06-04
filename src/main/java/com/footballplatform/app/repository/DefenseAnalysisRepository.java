package com.footballplatform.app.repository;

import com.footballplatform.app.entity.DefenseAnalysis;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DefenseAnalysisRepository extends JpaRepository<DefenseAnalysis, Long> {

    Optional<DefenseAnalysis> findByMatch_Id(Long matchId);
}
