package com.footballplatform.app.controller;

import com.footballplatform.app.dto.MatchDTO;
import com.footballplatform.app.dto.CompetitionDTO;
import com.footballplatform.app.entity.MatchStatus;
import com.footballplatform.app.service.CompetitionService;
import com.footballplatform.app.service.MatchService;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class HomeController {

    private final MatchService matchService;
    private final CompetitionService competitionService;

    public HomeController(MatchService matchService, CompetitionService competitionService) {
        this.matchService = matchService;
        this.competitionService = competitionService;
    }

    @GetMapping({"/", "/home"})
    public String home(@RequestParam(required = false) Long competitionId,
                       @RequestParam(required = false) MatchStatus status,
                       @RequestParam(required = false) String timeFilter,
                       @RequestParam(required = false) String keyword,
                       Model model) {
        List<CompetitionDTO> competitions = competitionService.findAll();
        List<MatchDTO> matches = matchService.findAll();

        if (shouldApplyDefaultStatusFilter(status, timeFilter)) {
            matches = filterByDefaultStatuses(matches);
        }

        matches = filterByCompetition(matches, competitionId);
        matches = filterByStatus(matches, status);
        matches = filterByTime(matches, timeFilter);
        matches = filterByKeyword(matches, keyword);

        model.addAttribute("matches", matches);
        model.addAttribute("competitions", competitions);
        model.addAttribute("statuses", MatchStatus.values());
        model.addAttribute("selectedCompetitionId", competitionId);
        model.addAttribute("selectedCompetitionName", findCompetitionName(competitions, competitionId));
        model.addAttribute("selectedStatus", status);
        model.addAttribute("selectedTimeFilter", normalizeTimeFilter(timeFilter));
        model.addAttribute("selectedKeyword", keyword == null ? "" : keyword.trim());
        return "home";
    }

    private String findCompetitionName(List<CompetitionDTO> competitions, Long competitionId) {
        if (competitionId == null) {
            return null;
        }

        return competitions.stream()
                .filter(competition -> competitionId.equals(competition.getId()))
                .map(CompetitionDTO::getName)
                .findFirst()
                .orElse(null);
    }

    private List<MatchDTO> filterByCompetition(List<MatchDTO> matches, Long competitionId) {
        if (competitionId == null) {
            return matches;
        }

        return matches.stream()
                .filter(match -> competitionId.equals(match.getCompetitionId()))
                .toList();
    }

    private List<MatchDTO> filterByStatus(List<MatchDTO> matches, MatchStatus status) {
        if (status == null) {
            return matches;
        }

        return matches.stream()
                .filter(match -> status.equals(match.getStatus()))
                .toList();
    }

    private List<MatchDTO> filterByDefaultStatuses(List<MatchDTO> matches) {
        return matches.stream()
                .filter(match -> match.getStatus() == MatchStatus.LIVE || match.getStatus() == MatchStatus.UPCOMING)
                .toList();
    }

    private List<MatchDTO> filterByTime(List<MatchDTO> matches, String timeFilter) {
        String normalized = normalizeTimeFilter(timeFilter);
        if (normalized == null) {
            return matches;
        }

        LocalDateTime now = LocalDateTime.now();
        LocalDate today = now.toLocalDate();
        LocalDateTime todayStart = today.atStartOfDay();
        LocalDateTime tomorrowStart = today.plusDays(1).atStartOfDay();
        LocalDateTime next7DaysEnd = today.plusDays(7).atTime(23, 59, 59);
        LocalDateTime next30DaysEnd = today.plusDays(30).atTime(23, 59, 59);

        return switch (normalized) {
            case "today" -> matches.stream()
                    .filter(match -> match.getMatchTime() != null)
                    .filter(match -> !match.getMatchTime().isBefore(todayStart) && match.getMatchTime().isBefore(tomorrowStart))
                    .toList();
            case "next7days" -> matches.stream()
                    .filter(match -> match.getMatchTime() != null)
                    .filter(match -> !match.getMatchTime().isBefore(todayStart) && !match.getMatchTime().isAfter(next7DaysEnd))
                    .toList();
            case "next30days" -> matches.stream()
                    .filter(match -> match.getMatchTime() != null)
                    .filter(match -> !match.getMatchTime().isBefore(todayStart) && !match.getMatchTime().isAfter(next30DaysEnd))
                    .toList();
            case "past" -> matches.stream()
                    .filter(match -> match.getMatchTime() != null)
                    .filter(match -> match.getMatchTime().isBefore(now))
                    .toList();
            default -> matches;
        };
    }

    private List<MatchDTO> filterByKeyword(List<MatchDTO> matches, String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) {
            return matches;
        }

        String normalizedKeyword = keyword.trim().toLowerCase();
        return matches.stream()
                .filter(match -> containsIgnoreCase(match.getTeamA(), normalizedKeyword)
                        || containsIgnoreCase(match.getTeamB(), normalizedKeyword))
                .toList();
    }

    private boolean containsIgnoreCase(String value, String keyword) {
        return value != null && value.toLowerCase().contains(keyword);
    }

    private String normalizeTimeFilter(String timeFilter) {
        if (timeFilter == null || timeFilter.isBlank() || "all".equalsIgnoreCase(timeFilter)) {
            return null;
        }

        return switch (timeFilter.trim().toLowerCase()) {
            case "today" -> "today";
            case "next7days" -> "next7days";
            case "next30days" -> "next30days";
            case "past" -> "past";
            default -> null;
        };
    }

    private boolean shouldApplyDefaultStatusFilter(MatchStatus status, String timeFilter) {
        return status == null && normalizeTimeFilter(timeFilter) == null;
    }
}
