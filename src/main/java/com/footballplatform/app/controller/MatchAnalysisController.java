package com.footballplatform.app.controller;

import com.footballplatform.app.dto.MatchDTO;
import com.footballplatform.app.entity.TeamSide;
import com.footballplatform.app.service.AttackAnalysisService;
import com.footballplatform.app.service.AIInsightService;
import com.footballplatform.app.service.BetRecommendationService;
import com.footballplatform.app.service.DefenseAnalysisService;
import com.footballplatform.app.service.HeadToHeadService;
import com.footballplatform.app.service.KeyPlayerService;
import com.footballplatform.app.service.MatchPredictionService;
import com.footballplatform.app.service.MatchService;
import com.footballplatform.app.service.PredictionModelService;
import com.footballplatform.app.service.SquadAnalysisService;
import com.footballplatform.app.service.TeamRecentFormService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class MatchAnalysisController {

    private final MatchService matchService;
    private final MatchPredictionService matchPredictionService;
    private final AIInsightService aiInsightService;
    private final AttackAnalysisService attackAnalysisService;
    private final DefenseAnalysisService defenseAnalysisService;
    private final BetRecommendationService betRecommendationService;
    private final HeadToHeadService headToHeadService;
    private final PredictionModelService predictionModelService;
    private final KeyPlayerService keyPlayerService;
    private final SquadAnalysisService squadAnalysisService;
    private final TeamRecentFormService teamRecentFormService;

    public MatchAnalysisController(MatchService matchService,
                                   MatchPredictionService matchPredictionService,
                                   AIInsightService aiInsightService,
                                   AttackAnalysisService attackAnalysisService,
                                   DefenseAnalysisService defenseAnalysisService,
                                   BetRecommendationService betRecommendationService,
                                   HeadToHeadService headToHeadService,
                                   PredictionModelService predictionModelService,
                                   KeyPlayerService keyPlayerService,
                                   SquadAnalysisService squadAnalysisService,
                                   TeamRecentFormService teamRecentFormService) {
        this.matchService = matchService;
        this.matchPredictionService = matchPredictionService;
        this.aiInsightService = aiInsightService;
        this.attackAnalysisService = attackAnalysisService;
        this.defenseAnalysisService = defenseAnalysisService;
        this.betRecommendationService = betRecommendationService;
        this.headToHeadService = headToHeadService;
        this.predictionModelService = predictionModelService;
        this.keyPlayerService = keyPlayerService;
        this.squadAnalysisService = squadAnalysisService;
        this.teamRecentFormService = teamRecentFormService;
    }

    @GetMapping("/matches/{id}/analysis")
    public String analysis(@PathVariable("id") Long id, Model model, RedirectAttributes redirectAttributes) {
        try {
            MatchDTO match = matchService.findById(id);
            model.addAttribute("match", match);
            model.addAttribute("prediction", matchPredictionService.findByMatchId(id).orElse(null));
            model.addAttribute("aiInsight", aiInsightService.findByMatchId(id).orElse(null));
            model.addAttribute("attackAnalysis", attackAnalysisService.findByMatchId(id).orElse(null));
            model.addAttribute("defenseAnalysis", defenseAnalysisService.findByMatchId(id).orElse(null));
            model.addAttribute("betRecommendations", betRecommendationService.findByMatchId(id));
            model.addAttribute("headToHead", headToHeadService.findByMatchId(id).orElse(null));
            model.addAttribute("predictionModel", predictionModelService.findByMatchId(id).orElse(null));
            model.addAttribute("keyPlayers", keyPlayerService.findByMatchId(id));
            model.addAttribute("squadAnalysis", squadAnalysisService.findByMatchId(id).orElse(null));
            model.addAttribute("homeTeamForm", teamRecentFormService.findByMatchIdAndTeamSide(id, TeamSide.HOME).orElse(null));
            model.addAttribute("awayTeamForm", teamRecentFormService.findByMatchIdAndTeamSide(id, TeamSide.AWAY).orElse(null));
            return "match/match-analysis-detail";
        } catch (RuntimeException ex) {
            System.out.println("Runtime error: " + ex.getMessage());
            redirectAttributes.addFlashAttribute("errorMessage", ex.getMessage());
            return "redirect:/home";
        }
    }
}
