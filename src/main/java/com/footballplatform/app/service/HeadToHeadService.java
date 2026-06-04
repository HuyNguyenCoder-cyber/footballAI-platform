package com.footballplatform.app.service;

import com.footballplatform.app.dto.HeadToHeadDTO;
import java.util.List;
import java.util.Optional;

public interface HeadToHeadService {

    List<HeadToHeadDTO> findAll();

    Optional<HeadToHeadDTO> findById(Long id);

    Optional<HeadToHeadDTO> findByMatchId(Long matchId);

    boolean isMatchAssignedToAnotherHeadToHead(Long matchId, Long currentHeadToHeadId);

    HeadToHeadDTO create(HeadToHeadDTO dto);

    HeadToHeadDTO update(HeadToHeadDTO dto);

    void delete(Long id);
}
