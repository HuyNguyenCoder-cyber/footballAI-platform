package com.footballplatform.app.repository;

import com.footballplatform.app.entity.HeadToHead;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface HeadToHeadRepository extends JpaRepository<HeadToHead, Long> {

    Optional<HeadToHead> findByMatch_Id(Long matchId);
}
