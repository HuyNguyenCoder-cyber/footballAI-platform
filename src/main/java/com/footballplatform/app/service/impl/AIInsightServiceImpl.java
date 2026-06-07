package com.footballplatform.app.service.impl;

import com.footballplatform.app.dto.AIInsightDTO;
import com.footballplatform.app.entity.AIInsight;
import com.footballplatform.app.entity.Match;
import com.footballplatform.app.repository.AIInsightRepository;
import com.footballplatform.app.repository.MatchRepository;
import com.footballplatform.app.service.CacheInvalidationService;
import com.footballplatform.app.service.AIInsightService;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class AIInsightServiceImpl implements AIInsightService {

    private static final DateTimeFormatter MATCH_TIME_FORMATTER = DateTimeFormatter.ofPattern("dd MMM yyyy HH:mm");

    private final AIInsightRepository aiInsightRepository;
    private final MatchRepository matchRepository;
    private final CacheInvalidationService cacheInvalidationService;

    public AIInsightServiceImpl(AIInsightRepository aiInsightRepository,
                                MatchRepository matchRepository,
                                CacheInvalidationService cacheInvalidationService) {
        this.aiInsightRepository = aiInsightRepository;
        this.matchRepository = matchRepository;
        this.cacheInvalidationService = cacheInvalidationService;
    }

    @Override
    @Transactional(readOnly = true)
    public List<AIInsightDTO> findAll() {
        return aiInsightRepository.findAll().stream().map(this::toDto).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<AIInsightDTO> findById(Long id) {
        return aiInsightRepository.findById(id).map(this::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<AIInsightDTO> findByMatchId(Long matchId) {
        return aiInsightRepository.findByMatch_Id(matchId).map(this::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean isMatchAssignedToAnotherAIInsight(Long matchId, Long currentAIInsightId) {
        return aiInsightRepository.findByMatch_Id(matchId)
                .map(existing -> currentAIInsightId == null || !existing.getId().equals(currentAIInsightId))
                .orElse(false);
    }

    @Override
    public AIInsightDTO create(AIInsightDTO dto) {
        if (isMatchAssignedToAnotherAIInsight(dto.getMatchId(), null)) {
            throw new RuntimeException("Trận đấu này đã có AI Insight.");
        }

        AIInsight entity = new AIInsight();
        applyDto(entity, dto);
        AIInsightDTO saved = toDto(aiInsightRepository.save(entity));
        cacheInvalidationService.evictMatchAnalysisCache(saved.getMatchId());
        return saved;
    }

    @Override
    public AIInsightDTO update(AIInsightDTO dto) {
        AIInsight entity = aiInsightRepository.findById(dto.getId())
                .orElseThrow(() -> new RuntimeException("AIInsight not found with id: " + dto.getId()));

        if (isMatchAssignedToAnotherAIInsight(dto.getMatchId(), dto.getId())) {
            throw new RuntimeException("Trận đấu này đã có AI Insight.");
        }

        applyDto(entity, dto);
        AIInsightDTO saved = toDto(aiInsightRepository.save(entity));
        cacheInvalidationService.evictMatchAnalysisCache(saved.getMatchId());
        return saved;
    }

    @Override
    public void delete(Long id) {
        AIInsight entity = aiInsightRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("AIInsight not found with id: " + id));
        Long matchId = entity.getMatch() != null ? entity.getMatch().getId() : null;
        aiInsightRepository.delete(entity);
        cacheInvalidationService.evictMatchAnalysisCache(matchId);
    }

    private void applyDto(AIInsight entity, AIInsightDTO dto) {
        Match match = matchRepository.findById(dto.getMatchId())
                .orElseThrow(() -> new RuntimeException("Match not found with id: " + dto.getMatchId()));

        entity.setMatch(match);
        entity.setContent(dto.getContent().trim());
    }

    private AIInsightDTO toDto(AIInsight entity) {
        return AIInsightDTO.builder()
                .id(entity.getId())
                .matchId(entity.getMatch() != null ? entity.getMatch().getId() : null)
                .content(entity.getContent())
                .matchLabel(buildMatchLabel(entity.getMatch()))
                .matchStatus(entity.getMatch() != null && entity.getMatch().getStatus() != null
                        ? entity.getMatch().getStatus().getDisplayName()
                        : "")
                .build();
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
