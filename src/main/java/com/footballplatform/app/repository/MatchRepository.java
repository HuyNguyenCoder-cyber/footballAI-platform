package com.footballplatform.app.repository;

import com.footballplatform.app.entity.Match;
import com.footballplatform.app.entity.MatchStatus;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MatchRepository extends JpaRepository<Match, Long> {

    List<Match> findByStatusInOrderByMatchTimeAsc(List<MatchStatus> statuses);
}
