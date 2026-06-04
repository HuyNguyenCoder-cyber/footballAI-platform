package com.footballplatform.app.repository;

import com.footballplatform.app.entity.Competition;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CompetitionRepository extends JpaRepository<Competition, Long> {

    boolean existsByNameIgnoreCase(String name);

    java.util.Optional<Competition> findByNameIgnoreCase(String name);
}
