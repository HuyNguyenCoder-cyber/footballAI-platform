package com.footballplatform.app.service.impl;

import com.footballplatform.app.dto.BetRecommendationDTO;
import com.footballplatform.app.entity.BetRecommendation;
import com.footballplatform.app.entity.Match;
import com.footballplatform.app.repository.BetRecommendationRepository;
import com.footballplatform.app.repository.MatchRepository;
import com.footballplatform.app.service.BetRecommendationService;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class BetRecommendationServiceImpl implements BetRecommendationService {

    private static final DateTimeFormatter MATCH_TIME_FORMATTER = DateTimeFormatter.ofPattern("dd MMM yyyy HH:mm");

    private final BetRecommendationRepository betRecommendationRepository;
    private final MatchRepository matchRepository;

    public BetRecommendationServiceImpl(BetRecommendationRepository betRecommendationRepository,
                                        MatchRepository matchRepository) {
        this.betRecommendationRepository = betRecommendationRepository;
        this.matchRepository = matchRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public List<BetRecommendationDTO> findAll() {
        return betRecommendationRepository.findAll().stream().map(this::toDto).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<BetRecommendationDTO> findById(Long id) {
        return betRecommendationRepository.findById(id).map(this::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public List<BetRecommendationDTO> findByMatchId(Long matchId) {
        return betRecommendationRepository.findByMatch_IdOrderByDisplayOrderAsc(matchId)
                .stream()
                .map(this::toDto)
                .toList();
    }

    @Override
    public BetRecommendationDTO create(BetRecommendationDTO dto) {
        BetRecommendation entity = new BetRecommendation();
        applyDto(entity, dto);
        return toDto(betRecommendationRepository.save(entity));
    }

    @Override
    public BetRecommendationDTO update(BetRecommendationDTO dto) {
        BetRecommendation entity = betRecommendationRepository.findById(dto.getId())
                .orElseThrow(() -> new RuntimeException("BetRecommendation not found with id: " + dto.getId()));
        applyDto(entity, dto);
        return toDto(betRecommendationRepository.save(entity));
    }

    @Override
    public void delete(Long id) {
        if (!betRecommendationRepository.existsById(id)) {
            throw new RuntimeException("BetRecommendation not found with id: " + id);
        }
        betRecommendationRepository.deleteById(id);
    }

    private void applyDto(BetRecommendation entity, BetRecommendationDTO dto) {
        Match match = matchRepository.findById(dto.getMatchId())
                .orElseThrow(() -> new RuntimeException("Match not found with id: " + dto.getMatchId()));
        entity.setMatch(match);
        entity.setBetType(dto.getBetType());
        entity.setTitle(dto.getTitle().trim());
        entity.setRecommendation(dto.getRecommendation().trim());
        entity.setDisplayOrder(dto.getDisplayOrder());
    }

    private BetRecommendationDTO toDto(BetRecommendation entity) {
        return BetRecommendationDTO.builder()
                .id(entity.getId())
                .matchId(entity.getMatch() != null ? entity.getMatch().getId() : null)
                .betType(entity.getBetType())
                .title(entity.getTitle())
                .recommendation(entity.getRecommendation())
                .displayOrder(entity.getDisplayOrder())
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
