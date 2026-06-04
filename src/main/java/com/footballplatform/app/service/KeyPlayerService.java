package com.footballplatform.app.service;

import com.footballplatform.app.dto.KeyPlayerDTO;
import java.util.List;
import java.util.Optional;

public interface KeyPlayerService {

    List<KeyPlayerDTO> findAll();

    Optional<KeyPlayerDTO> findById(Long id);

    List<KeyPlayerDTO> findByMatchId(Long matchId);

    KeyPlayerDTO create(KeyPlayerDTO dto);

    KeyPlayerDTO update(KeyPlayerDTO dto);

    void delete(Long id);
}
