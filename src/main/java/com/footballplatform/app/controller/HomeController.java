package com.footballplatform.app.controller;

import com.footballplatform.app.service.CompetitionService;
import com.footballplatform.app.service.MatchService;
import org.springframework.ui.Model;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {

    private final MatchService matchService;
    private final CompetitionService competitionService;

    public HomeController(MatchService matchService, CompetitionService competitionService) {
        this.matchService = matchService;
        this.competitionService = competitionService;
    }

    @GetMapping({"/", "/home"})
    public String home(Model model) {
        model.addAttribute("matches", matchService.findAll());
        model.addAttribute("competitions", competitionService.findAll());
        return "home";
    }
}
