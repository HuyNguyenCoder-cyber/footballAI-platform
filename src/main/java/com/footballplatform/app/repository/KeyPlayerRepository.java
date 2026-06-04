package com.footballplatform.app.repository;

import com.footballplatform.app.entity.KeyPlayer;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface KeyPlayerRepository extends JpaRepository<KeyPlayer, Long> {

    List<KeyPlayer> findByMatch_IdOrderByDisplayOrderAsc(Long matchId);
}
