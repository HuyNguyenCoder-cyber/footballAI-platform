package com.footballplatform.app.repository;

import com.footballplatform.app.entity.Match;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MatchRepository extends JpaRepository<Match, Long> {
}
