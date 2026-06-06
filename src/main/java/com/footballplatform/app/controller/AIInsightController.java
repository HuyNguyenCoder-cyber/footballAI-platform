package com.footballplatform.app.controller;

import com.footballplatform.app.dto.AIInsightDTO;
import com.footballplatform.app.dto.MatchDTO;
import com.footballplatform.app.entity.MatchStatus;
import com.footballplatform.app.service.AIInsightService;
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
@RequestMapping("/admin/ai-insights")
public class AIInsightController {

    private static final DateTimeFormatter MATCH_TIME_FORMATTER = DateTimeFormatter.ofPattern("dd MMM yyyy HH:mm");

    private final AIInsightService aiInsightService;
    private final MatchService matchService;

    public AIInsightController(AIInsightService aiInsightService, MatchService matchService) {
        this.aiInsightService = aiInsightService;
        this.matchService = matchService;
    }

    @GetMapping
    public String list(Model model) {
        model.addAttribute("aiInsights", aiInsightService.findAll());
        return "admin/ai-insights/list";
    }

    @GetMapping("/new")
    public String createForm(Model model) {
        addFormAttributes(model, true);
        model.addAttribute("aiInsight", new AIInsightDTO());
        return "admin/ai-insights/form";
    }

    @PostMapping("/create")
    public String create(@Valid @ModelAttribute("aiInsight") AIInsightDTO aiInsight,
                         BindingResult bindingResult,
                         Model model,
                         RedirectAttributes redirectAttributes) {
        validateBusiness(aiInsight, bindingResult);
        if (bindingResult.hasErrors()) {
            addFormAttributes(model, true);
            return "admin/ai-insights/form";
        }

        try {
            aiInsightService.create(aiInsight);
            redirectAttributes.addFlashAttribute("successMessage", "AI Insight created successfully.");
            return "redirect:/admin/ai-insights";
        } catch (RuntimeException ex) {
            System.out.println("Runtime error: " + ex.getMessage());
            addFormAttributes(model, true);
            model.addAttribute("errorMessage", ex.getMessage());
            return "admin/ai-insights/form";
        }
    }

    @GetMapping("/{id}/edit")
    public String editForm(@PathVariable("id") Long id, Model model, RedirectAttributes redirectAttributes) {
        try {
            addFormAttributes(model, false);
            model.addAttribute("aiInsight", aiInsightService.findById(id)
                    .orElseThrow(() -> new RuntimeException("AIInsight not found with id: " + id)));
            return "admin/ai-insights/form";
        } catch (RuntimeException ex) {
            System.out.println("Runtime error: " + ex.getMessage());
            redirectAttributes.addFlashAttribute("errorMessage", ex.getMessage());
            return "redirect:/admin/ai-insights";
        }
    }

    @PostMapping("/{id}/update")
    public String update(@PathVariable("id") Long id,
                         @Valid @ModelAttribute("aiInsight") AIInsightDTO aiInsight,
                         BindingResult bindingResult,
                         Model model,
                         RedirectAttributes redirectAttributes) {
        aiInsight.setId(id);
        validateBusiness(aiInsight, bindingResult);
        if (bindingResult.hasErrors()) {
            addFormAttributes(model, false);
            populateMatchDisplay(aiInsight);
            return "admin/ai-insights/form";
        }

        try {
            aiInsightService.update(aiInsight);
            redirectAttributes.addFlashAttribute("successMessage", "AI Insight updated successfully.");
            return "redirect:/admin/ai-insights";
        } catch (RuntimeException ex) {
            System.out.println("Runtime error: " + ex.getMessage());
            addFormAttributes(model, false);
            populateMatchDisplay(aiInsight);
            model.addAttribute("errorMessage", ex.getMessage());
            return "admin/ai-insights/form";
        }
    }

    @PostMapping("/{id}/delete")
    public String delete(@PathVariable("id") Long id, RedirectAttributes redirectAttributes) {
        try {
            aiInsightService.delete(id);
            redirectAttributes.addFlashAttribute("successMessage", "AI Insight deleted successfully.");
        } catch (RuntimeException ex) {
            System.out.println("Runtime error: " + ex.getMessage());
            redirectAttributes.addFlashAttribute("errorMessage", ex.getMessage());
        }
        return "redirect:/admin/ai-insights";
    }

    private void addFormAttributes(Model model, boolean createMode) {
        if (createMode) {
            model.addAttribute("matches", matchService.findByStatuses(List.of(MatchStatus.UPCOMING)));
        }
    }

    private void validateBusiness(AIInsightDTO aiInsight, BindingResult bindingResult) {
        if (aiInsight.getMatchId() == null || bindingResult.hasFieldErrors("matchId")) {
            return;
        }

        if (aiInsightService.isMatchAssignedToAnotherAIInsight(aiInsight.getMatchId(), aiInsight.getId())) {
            bindingResult.rejectValue("matchId", "matchId.duplicate", "Trận đấu này đã có AI Insight.");
        }
    }

    private void populateMatchDisplay(AIInsightDTO aiInsight) {
        if (aiInsight.getMatchId() == null) {
            return;
        }

        try {
            MatchDTO match = matchService.findById(aiInsight.getMatchId());
            aiInsight.setMatchLabel(match.getTeamA() + " vs " + match.getTeamB() + " | "
                    + match.getMatchTime().format(MATCH_TIME_FORMATTER));
            aiInsight.setMatchStatus(match.getStatus() != null ? match.getStatus().getDisplayName() : "");
        } catch (RuntimeException ex) {
            System.out.println("Runtime error: " + ex.getMessage());
        }
    }
}
