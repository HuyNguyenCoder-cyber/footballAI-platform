package com.footballplatform.app.service.impl;

import com.footballplatform.app.dto.KeyPlayerDTO;
import com.footballplatform.app.entity.KeyPlayer;
import com.footballplatform.app.entity.Match;
import com.footballplatform.app.repository.KeyPlayerRepository;
import com.footballplatform.app.repository.MatchRepository;
import com.footballplatform.app.service.CacheInvalidationService;
import com.footballplatform.app.service.KeyPlayerService;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class KeyPlayerServiceImpl implements KeyPlayerService {

    private static final DateTimeFormatter MATCH_TIME_FORMATTER = DateTimeFormatter.ofPattern("dd MMM yyyy HH:mm");

    private final KeyPlayerRepository keyPlayerRepository;
    private final MatchRepository matchRepository;
    private final CacheInvalidationService cacheInvalidationService;

    public KeyPlayerServiceImpl(KeyPlayerRepository keyPlayerRepository,
                                MatchRepository matchRepository,
                                CacheInvalidationService cacheInvalidationService) {
        this.keyPlayerRepository = keyPlayerRepository;
        this.matchRepository = matchRepository;
        this.cacheInvalidationService = cacheInvalidationService;
    }

    @Override
    @Transactional(readOnly = true)
    public List<KeyPlayerDTO> findAll() {
        return keyPlayerRepository.findAll().stream()
                .sorted(Comparator.comparing((KeyPlayer item) -> item.getMatch() != null ? item.getMatch().getId() : Long.MAX_VALUE)
                        .thenComparing(KeyPlayer::getDisplayOrder, Comparator.nullsLast(Integer::compareTo)))
                .map(this::toDto)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<KeyPlayerDTO> findById(Long id) {
        return keyPlayerRepository.findById(id).map(this::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public List<KeyPlayerDTO> findByMatchId(Long matchId) {
        return keyPlayerRepository.findByMatch_IdOrderByDisplayOrderAsc(matchId).stream()
                .map(this::toDto)
                .toList();
    }

    @Override
    public KeyPlayerDTO create(KeyPlayerDTO dto) {
        KeyPlayer entity = new KeyPlayer();
        applyDto(entity, dto);
        KeyPlayerDTO saved = toDto(keyPlayerRepository.save(entity));
        cacheInvalidationService.evictMatchAnalysisCache(saved.getMatchId());
        return saved;
    }

    @Override
    public KeyPlayerDTO update(KeyPlayerDTO dto) {
        KeyPlayer entity = keyPlayerRepository.findById(dto.getId())
                .orElseThrow(() -> new RuntimeException("KeyPlayer not found with id: " + dto.getId()));
        applyDto(entity, dto);
        KeyPlayerDTO saved = toDto(keyPlayerRepository.save(entity));
        cacheInvalidationService.evictMatchAnalysisCache(saved.getMatchId());
        return saved;
    }

    @Override
    public void delete(Long id) {
        KeyPlayer entity = keyPlayerRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("KeyPlayer not found with id: " + id));
        Long matchId = entity.getMatch() != null ? entity.getMatch().getId() : null;
        keyPlayerRepository.delete(entity);
        cacheInvalidationService.evictMatchAnalysisCache(matchId);
    }

    private void applyDto(KeyPlayer entity, KeyPlayerDTO dto) {
        Match match = matchRepository.findById(dto.getMatchId())
                .orElseThrow(() -> new RuntimeException("Match not found with id: " + dto.getMatchId()));

        entity.setMatch(match);
        entity.setPlayerName(dto.getPlayerName().trim());
        entity.setTeamName(dto.getTeamName().trim());
        entity.setShortDescription(dto.getShortDescription().trim());
        entity.setImageUrl(dto.getImageUrl() == null ? null : dto.getImageUrl().trim());
        entity.setDisplayOrder(dto.getDisplayOrder());
    }

    private KeyPlayerDTO toDto(KeyPlayer entity) {
        return KeyPlayerDTO.builder()
                .id(entity.getId())
                .matchId(entity.getMatch() != null ? entity.getMatch().getId() : null)
                .playerName(entity.getPlayerName())
                .teamName(entity.getTeamName())
                .shortDescription(entity.getShortDescription())
                .imageUrl(entity.getImageUrl())
                .displayOrder(entity.getDisplayOrder())
                .avatarInitials(buildAvatarInitials(entity.getPlayerName()))
                .matchLabel(buildMatchLabel(entity.getMatch()))
                .matchStatus(entity.getMatch() != null && entity.getMatch().getStatus() != null
                        ? entity.getMatch().getStatus().getDisplayName()
                        : "")
                .build();
    }

    private String buildAvatarInitials(String playerName) {
        if (playerName == null) {
            return "";
        }

        String trimmedName = playerName.trim();
        if (trimmedName.isEmpty()) {
            return "";
        }

        String initialsSource = trimmedName.length() >= 2 ? trimmedName.substring(0, 2) : trimmedName;
        return initialsSource.toUpperCase();
    }

    private String buildMatchLabel(Match match) {
        if (match == null) {
            return "";
        }

        String teamA = match.getTeamA() == null ? "" : match.getTeamA().trim();
        String teamB = match.getTeamB() == null ? "" : match.getTeamB().trim();
        String matchTime = match.getMatchTime() != null ? match.getMatchTime().format(MATCH_TIME_FORMATTER) : "";
        return teamA + " vs " + teamB + (matchTime.isEmpty() ? "" : " | " + matchTime);
    }
}
