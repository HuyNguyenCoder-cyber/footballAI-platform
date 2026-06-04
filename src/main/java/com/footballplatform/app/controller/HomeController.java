package com.footballplatform.app.controller;

import com.footballplatform.app.dto.MatchDTO;
import com.footballplatform.app.service.CompetitionService;
import com.footballplatform.app.service.MatchService;
import com.footballplatform.app.entity.MatchStatus;
import java.util.List;
import org.springframework.ui.Model;
import org.springframework.stereotype.Controller;
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
                       Model model) {
        List<MatchDTO> matches = matchService.findAll();

        if (competitionId != null) {
            matches = matches.stream()
                    .filter(match -> competitionId.equals(match.getCompetitionId()))
                    .toList();
        }

        if (status != null) {
            matches = matches.stream()
                    .filter(match -> status.equals(match.getStatus()))
                    .toList();
        }

        model.addAttribute("matches", matches);
        model.addAttribute("competitions", competitionService.findAll());
        model.addAttribute("statuses", MatchStatus.values());
        model.addAttribute("selectedCompetitionId", competitionId);
        model.addAttribute("selectedStatus", status);
        return "home";
    }
}
