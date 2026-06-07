package com.footballplatform.app.controller;

import com.footballplatform.app.dto.MatchAnalysisPageDataDTO;
import com.footballplatform.app.entity.TeamSide;
import com.footballplatform.app.service.MatchAnalysisPageService;
import com.footballplatform.app.service.SeoService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class MatchAnalysisController {

    private final MatchAnalysisPageService matchAnalysisPageService;
    private final SeoService seoService;

    public MatchAnalysisController(MatchAnalysisPageService matchAnalysisPageService, SeoService seoService) {
        this.matchAnalysisPageService = matchAnalysisPageService;
        this.seoService = seoService;
    }

    @GetMapping("/matches/{id}/analysis")
    public String analysis(@PathVariable("id") Long id, Model model, RedirectAttributes redirectAttributes) {
        try {
            MatchAnalysisPageDataDTO pageData = matchAnalysisPageService.getMatchAnalysisPageData(id);
            model.addAttribute("match", pageData.getMatch());
            model.addAttribute("seo", seoService.buildMatchAnalysisSeo(pageData.getMatch()));
            model.addAttribute("prediction", pageData.getPrediction());
            model.addAttribute("aiInsight", pageData.getAiInsight());
            model.addAttribute("attackAnalysis", pageData.getAttackAnalysis());
            model.addAttribute("defenseAnalysis", pageData.getDefenseAnalysis());
            model.addAttribute("betRecommendations", pageData.getBetRecommendations());
            model.addAttribute("headToHead", pageData.getHeadToHead());
            model.addAttribute("predictionModel", pageData.getPredictionModel());
            model.addAttribute("keyPlayers", pageData.getKeyPlayers());
            model.addAttribute("squadAnalysis", pageData.getSquadAnalysis());
            model.addAttribute("homeTeamForm", pageData.getTeamForm(TeamSide.HOME));
            model.addAttribute("awayTeamForm", pageData.getTeamForm(TeamSide.AWAY));
            return "match/match-analysis-detail";
        } catch (RuntimeException ex) {
            System.out.println("Runtime error: " + ex.getMessage());
            redirectAttributes.addFlashAttribute("errorMessage", ex.getMessage());
            return "redirect:/home";
        }
    }
}
