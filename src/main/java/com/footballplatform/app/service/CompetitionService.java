package com.footballplatform.app.service;

import com.footballplatform.app.dto.CompetitionDTO;
import java.util.List;

public interface CompetitionService {

    List<CompetitionDTO> findAll();

    CompetitionDTO findById(Long id);

    CompetitionDTO create(CompetitionDTO dto);

    CompetitionDTO update(CompetitionDTO dto);

    void delete(Long id);
}
