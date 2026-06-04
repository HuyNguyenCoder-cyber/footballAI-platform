package com.footballplatform.app.repository;

import com.footballplatform.app.entity.TeamRecentForm;
import com.footballplatform.app.entity.TeamSide;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TeamRecentFormRepository extends JpaRepository<TeamRecentForm, Long> {

    Optional<TeamRecentForm> findByMatch_IdAndTeamSide(Long matchId, TeamSide teamSide);

    List<TeamRecentForm> findByMatch_IdOrderByTeamSideAsc(Long matchId);
}
