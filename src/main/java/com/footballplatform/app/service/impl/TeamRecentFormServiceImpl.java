package com.footballplatform.app.service.impl;

import com.footballplatform.app.dto.TeamRecentFormDTO;
import com.footballplatform.app.entity.Match;
import com.footballplatform.app.entity.TeamRecentForm;
import com.footballplatform.app.entity.TeamSide;
import com.footballplatform.app.repository.MatchRepository;
import com.footballplatform.app.repository.TeamRecentFormRepository;
import com.footballplatform.app.service.TeamRecentFormService;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class TeamRecentFormServiceImpl implements TeamRecentFormService {

    private static final DateTimeFormatter MATCH_TIME_FORMATTER = DateTimeFormatter.ofPattern("dd MMM yyyy HH:mm");

    private final TeamRecentFormRepository teamRecentFormRepository;
    private final MatchRepository matchRepository;

    public TeamRecentFormServiceImpl(TeamRecentFormRepository teamRecentFormRepository,
                                     MatchRepository matchRepository) {
        this.teamRecentFormRepository = teamRecentFormRepository;
        this.matchRepository = matchRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public List<TeamRecentFormDTO> findAll() {
        return teamRecentFormRepository.findAll().stream()
                .sorted(Comparator.comparing((TeamRecentForm item) -> item.getMatch() != null ? item.getMatch().getId() : Long.MAX_VALUE)
                        .thenComparing(TeamRecentForm::getTeamSide))
                .map(this::toDto)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<TeamRecentFormDTO> findById(Long id) {
        return teamRecentFormRepository.findById(id).map(this::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<TeamRecentFormDTO> findByMatchIdAndTeamSide(Long matchId, TeamSide teamSide) {
        return teamRecentFormRepository.findByMatch_IdAndTeamSide(matchId, teamSide).map(this::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public List<TeamRecentFormDTO> findByMatchId(Long matchId) {
        return teamRecentFormRepository.findByMatch_IdOrderByTeamSideAsc(matchId).stream()
                .map(this::toDto)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existsByMatchIdAndTeamSideForAnotherRecord(Long matchId, TeamSide teamSide, Long currentId) {
        return teamRecentFormRepository.findByMatch_IdAndTeamSide(matchId, teamSide)
                .map(existing -> currentId == null || !existing.getId().equals(currentId))
                .orElse(false);
    }

    @Override
    public TeamRecentFormDTO create(TeamRecentFormDTO dto) {
        if (existsByMatchIdAndTeamSideForAnotherRecord(dto.getMatchId(), dto.getTeamSide(), null)) {
            throw new RuntimeException(buildDuplicateMessage(dto.getTeamSide()));
        }

        TeamRecentForm entity = new TeamRecentForm();
        applyDto(entity, dto);
        return toDto(teamRecentFormRepository.save(entity));
    }

    @Override
    public TeamRecentFormDTO update(TeamRecentFormDTO dto) {
        TeamRecentForm entity = teamRecentFormRepository.findById(dto.getId())
                .orElseThrow(() -> new RuntimeException("TeamRecentForm not found with id: " + dto.getId()));

        if (existsByMatchIdAndTeamSideForAnotherRecord(dto.getMatchId(), dto.getTeamSide(), dto.getId())) {
            throw new RuntimeException(buildDuplicateMessage(dto.getTeamSide()));
        }

        applyDto(entity, dto);
        return toDto(teamRecentFormRepository.save(entity));
    }

    @Override
    public void delete(Long id) {
        if (!teamRecentFormRepository.existsById(id)) {
            throw new RuntimeException("TeamRecentForm not found with id: " + id);
        }
        teamRecentFormRepository.deleteById(id);
    }

    private void applyDto(TeamRecentForm entity, TeamRecentFormDTO dto) {
        Match match = matchRepository.findById(dto.getMatchId())
                .orElseThrow(() -> new RuntimeException("Match not found with id: " + dto.getMatchId()));

        entity.setMatch(match);
        entity.setTeamSide(dto.getTeamSide());
        entity.setRecentMatchesText(dto.getRecentMatchesText().trim());
        entity.setWins(dto.getWins());
        entity.setDraws(dto.getDraws());
        entity.setLosses(dto.getLosses());
        entity.setGoalsScored(dto.getGoalsScored());
        entity.setGoalsConceded(dto.getGoalsConceded());
        entity.setCleanSheets(dto.getCleanSheets());
        entity.setWinRate(dto.getWinRate());
        entity.setCleanSheetRate(dto.getCleanSheetRate());
        entity.setScoringRate(dto.getScoringRate());
        entity.setConcedingRate(dto.getConcedingRate());
    }

    private TeamRecentFormDTO toDto(TeamRecentForm entity) {
        return TeamRecentFormDTO.builder()
                .id(entity.getId())
                .matchId(entity.getMatch() != null ? entity.getMatch().getId() : null)
                .teamSide(entity.getTeamSide())
                .recentMatchesText(entity.getRecentMatchesText())
                .wins(entity.getWins())
                .draws(entity.getDraws())
                .losses(entity.getLosses())
                .goalsScored(entity.getGoalsScored())
                .goalsConceded(entity.getGoalsConceded())
                .cleanSheets(entity.getCleanSheets())
                .winRate(entity.getWinRate())
                .cleanSheetRate(entity.getCleanSheetRate())
                .scoringRate(entity.getScoringRate())
                .concedingRate(entity.getConcedingRate())
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

    private String buildDuplicateMessage(TeamSide teamSide) {
        return teamSide == TeamSide.HOME
                ? "Trận đấu này đã có HOME form."
                : "Trận đấu này đã có AWAY form.";
    }
}
