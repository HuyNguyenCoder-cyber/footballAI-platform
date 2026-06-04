package com.footballplatform.app.service;

import com.footballplatform.app.dto.MatchDTO;
import com.footballplatform.app.entity.MatchStatus;
import java.util.List;

public interface MatchService {

    List<MatchDTO> findAll();

    List<MatchDTO> findByStatuses(List<MatchStatus> statuses);

    MatchDTO findById(Long id);

    MatchDTO create(MatchDTO dto);

    MatchDTO update(MatchDTO dto);

    void delete(Long id);
}
