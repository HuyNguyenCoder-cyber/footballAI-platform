package com.footballplatform.app.service;

import com.footballplatform.app.dto.MatchDTO;
import java.util.List;

public interface MatchService {

    List<MatchDTO> findAll();

    MatchDTO findById(Long id);

    MatchDTO create(MatchDTO dto);

    MatchDTO update(MatchDTO dto);

    void delete(Long id);
}
