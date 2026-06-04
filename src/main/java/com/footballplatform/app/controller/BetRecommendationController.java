package com.footballplatform.app.controller;

import com.footballplatform.app.dto.BetRecommendationDTO;
import com.footballplatform.app.dto.MatchDTO;
import com.footballplatform.app.entity.BetType;
import com.footballplatform.app.entity.MatchStatus;
import com.footballplatform.app.service.BetRecommendationService;
import com.footballplatform.app.service.MatchService;
import jakarta.validation.Valid;
import java.time.format.DateTimeFormatter;
import java.util.List;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/admin/bet-recommendations")
public class BetRecommendationController {

    private static final DateTimeFormatter MATCH_TIME_FORMATTER = DateTimeFormatter.ofPattern("dd MMM yyyy HH:mm");

    private final BetRecommendationService betRecommendationService;
    private final MatchService matchService;

    public BetRecommendationController(BetRecommendationService betRecommendationService, MatchService matchService) {
        this.betRecommendationService = betRecommendationService;
        this.matchService = matchService;
    }

    @GetMapping
    public String list(Model model) {
        model.addAttribute("recommendations", betRecommendationService.findAll());
        return "admin/bet-recommendations/list";
    }

    @GetMapping("/new")
    public String createForm(Model model) {
        addFormAttributes(model);
        model.addAttribute("recommendation", new BetRecommendationDTO());
        return "admin/bet-recommendations/form";
    }

    @PostMapping("/create")
    public String create(@Valid @ModelAttribute("recommendation") BetRecommendationDTO recommendation,
                         BindingResult bindingResult,
                         Model model,
                         RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            addFormAttributes(model);
            return "admin/bet-recommendations/form";
        }
        try {
            betRecommendationService.create(recommendation);
            redirectAttributes.addFlashAttribute("successMessage", "Recommendation created successfully.");
            return "redirect:/admin/bet-recommendations";
        } catch (RuntimeException ex) {
            System.out.println("Runtime error: " + ex.getMessage());
            addFormAttributes(model);
            model.addAttribute("errorMessage", ex.getMessage());
            return "admin/bet-recommendations/form";
        }
    }

    @GetMapping("/{id}/edit")
    public String editForm(@PathVariable("id") Long id, Model model, RedirectAttributes redirectAttributes) {
        try {
            addFormAttributes(model);
            model.addAttribute("recommendation", betRecommendationService.findById(id)
                    .orElseThrow(() -> new RuntimeException("BetRecommendation not found with id: " + id)));
            return "admin/bet-recommendations/form";
        } catch (RuntimeException ex) {
            System.out.println("Runtime error: " + ex.getMessage());
            redirectAttributes.addFlashAttribute("errorMessage", ex.getMessage());
            return "redirect:/admin/bet-recommendations";
        }
    }

    @PostMapping("/{id}/update")
    public String update(@PathVariable("id") Long id,
                         @Valid @ModelAttribute("recommendation") BetRecommendationDTO recommendation,
                         BindingResult bindingResult,
                         Model model,
                         RedirectAttributes redirectAttributes) {
        recommendation.setId(id);
        if (bindingResult.hasErrors()) {
            addFormAttributes(model);
            populateMatchDisplay(recommendation);
            return "admin/bet-recommendations/form";
        }
        try {
            betRecommendationService.update(recommendation);
            redirectAttributes.addFlashAttribute("successMessage", "Recommendation updated successfully.");
            return "redirect:/admin/bet-recommendations";
        } catch (RuntimeException ex) {
            System.out.println("Runtime error: " + ex.getMessage());
            addFormAttributes(model);
            populateMatchDisplay(recommendation);
            model.addAttribute("errorMessage", ex.getMessage());
            return "admin/bet-recommendations/form";
        }
    }

    @PostMapping("/{id}/delete")
    public String delete(@PathVariable("id") Long id, RedirectAttributes redirectAttributes) {
        try {
            betRecommendationService.delete(id);
            redirectAttributes.addFlashAttribute("successMessage", "Recommendation deleted successfully.");
        } catch (RuntimeException ex) {
            System.out.println("Runtime error: " + ex.getMessage());
            redirectAttributes.addFlashAttribute("errorMessage", ex.getMessage());
        }
        return "redirect:/admin/bet-recommendations";
    }

    private void addFormAttributes(Model model) {
        model.addAttribute("matches", matchService.findByStatuses(List.of(MatchStatus.UPCOMING, MatchStatus.LIVE)));
        model.addAttribute("betTypes", BetType.values());
    }

    private void populateMatchDisplay(BetRecommendationDTO recommendation) {
        if (recommendation.getMatchId() == null) {
            return;
        }

        try {
            MatchDTO match = matchService.findById(recommendation.getMatchId());
            recommendation.setMatchLabel(match.getTeamA() + " vs " + match.getTeamB() + " | " + match.getMatchTime().format(MATCH_TIME_FORMATTER));
            recommendation.setMatchStatus(match.getStatus() != null ? match.getStatus().name() : "");
        } catch (RuntimeException ex) {
            System.out.println("Runtime error: " + ex.getMessage());
        }
    }
}
