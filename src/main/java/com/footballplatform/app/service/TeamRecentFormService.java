package com.footballplatform.app.service;

import com.footballplatform.app.dto.TeamRecentFormDTO;
import com.footballplatform.app.entity.TeamSide;
import java.util.List;
import java.util.Optional;

public interface TeamRecentFormService {

    List<TeamRecentFormDTO> findAll();

    Optional<TeamRecentFormDTO> findById(Long id);

    Optional<TeamRecentFormDTO> findByMatchIdAndTeamSide(Long matchId, TeamSide teamSide);

    List<TeamRecentFormDTO> findByMatchId(Long matchId);

    boolean existsByMatchIdAndTeamSideForAnotherRecord(Long matchId, TeamSide teamSide, Long currentId);

    TeamRecentFormDTO create(TeamRecentFormDTO dto);

    TeamRecentFormDTO update(TeamRecentFormDTO dto);

    void delete(Long id);
}
