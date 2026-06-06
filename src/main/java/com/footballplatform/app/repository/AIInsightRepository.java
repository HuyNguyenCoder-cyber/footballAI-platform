package com.footballplatform.app.repository;

import com.footballplatform.app.entity.AIInsight;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AIInsightRepository extends JpaRepository<AIInsight, Long> {

    Optional<AIInsight> findByMatch_Id(Long matchId);
}
