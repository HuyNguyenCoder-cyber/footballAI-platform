package com.footballplatform.app.controller;

import com.footballplatform.app.dto.DefenseAnalysisDTO;
import com.footballplatform.app.dto.MatchDTO;
import com.footballplatform.app.entity.MatchStatus;
import com.footballplatform.app.service.DefenseAnalysisService;
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
@RequestMapping("/admin/defense-analyses")
public class DefenseAnalysisController {

    private static final DateTimeFormatter MATCH_TIME_FORMATTER = DateTimeFormatter.ofPattern("dd MMM yyyy HH:mm");

    private final DefenseAnalysisService defenseAnalysisService;
    private final MatchService matchService;

    public DefenseAnalysisController(DefenseAnalysisService defenseAnalysisService, MatchService matchService) {
        this.defenseAnalysisService = defenseAnalysisService;
        this.matchService = matchService;
    }

    @GetMapping
    public String list(Model model) {
        model.addAttribute("defenseAnalyses", defenseAnalysisService.findAll());
        return "admin/defense-analyses/list";
    }

    @GetMapping("/new")
    public String createForm(Model model) {
        addFormAttributes(model, true);
        model.addAttribute("defenseAnalysis", new DefenseAnalysisDTO());
        return "admin/defense-analyses/form";
    }

    @PostMapping("/create")
    public String create(@Valid @ModelAttribute("defenseAnalysis") DefenseAnalysisDTO defenseAnalysis,
                         BindingResult bindingResult,
                         Model model,
                         RedirectAttributes redirectAttributes) {
        validateBusiness(defenseAnalysis, bindingResult);
        if (bindingResult.hasErrors()) {
            addFormAttributes(model, true);
            return "admin/defense-analyses/form";
        }

        try {
            defenseAnalysisService.create(defenseAnalysis);
            redirectAttributes.addFlashAttribute("successMessage", "Defense Analysis created successfully.");
            return "redirect:/admin/defense-analyses";
        } catch (RuntimeException ex) {
            System.out.println("Runtime error: " + ex.getMessage());
            addFormAttributes(model, true);
            model.addAttribute("errorMessage", ex.getMessage());
            return "admin/defense-analyses/form";
        }
    }

    @GetMapping("/{id}/edit")
    public String editForm(@PathVariable("id") Long id, Model model, RedirectAttributes redirectAttributes) {
        try {
            addFormAttributes(model, false);
            model.addAttribute("defenseAnalysis", defenseAnalysisService.findById(id)
                    .orElseThrow(() -> new RuntimeException("DefenseAnalysis not found with id: " + id)));
            return "admin/defense-analyses/form";
        } catch (RuntimeException ex) {
            System.out.println("Runtime error: " + ex.getMessage());
            redirectAttributes.addFlashAttribute("errorMessage", ex.getMessage());
            return "redirect:/admin/defense-analyses";
        }
    }

    @PostMapping("/{id}/update")
    public String update(@PathVariable("id") Long id,
                         @Valid @ModelAttribute("defenseAnalysis") DefenseAnalysisDTO defenseAnalysis,
                         BindingResult bindingResult,
                         Model model,
                         RedirectAttributes redirectAttributes) {
        defenseAnalysis.setId(id);
        validateBusiness(defenseAnalysis, bindingResult);
        if (bindingResult.hasErrors()) {
            addFormAttributes(model, false);
            populateMatchDisplay(defenseAnalysis);
            return "admin/defense-analyses/form";
        }

        try {
            defenseAnalysisService.update(defenseAnalysis);
            redirectAttributes.addFlashAttribute("successMessage", "Defense Analysis updated successfully.");
            return "redirect:/admin/defense-analyses";
        } catch (RuntimeException ex) {
            System.out.println("Runtime error: " + ex.getMessage());
            addFormAttributes(model, false);
            populateMatchDisplay(defenseAnalysis);
            model.addAttribute("errorMessage", ex.getMessage());
            return "admin/defense-analyses/form";
        }
    }

    @PostMapping("/{id}/delete")
    public String delete(@PathVariable("id") Long id, RedirectAttributes redirectAttributes) {
        try {
            defenseAnalysisService.delete(id);
            redirectAttributes.addFlashAttribute("successMessage", "Defense Analysis deleted successfully.");
        } catch (RuntimeException ex) {
            System.out.println("Runtime error: " + ex.getMessage());
            redirectAttributes.addFlashAttribute("errorMessage", ex.getMessage());
        }
        return "redirect:/admin/defense-analyses";
    }

    private void addFormAttributes(Model model, boolean createMode) {
        if (createMode) {
            model.addAttribute("matches", matchService.findByStatuses(List.of(MatchStatus.UPCOMING)));
        }
    }

    private void validateBusiness(DefenseAnalysisDTO defenseAnalysis, BindingResult bindingResult) {
        if (defenseAnalysis.getMatchId() == null || bindingResult.hasFieldErrors("matchId")) {
            return;
        }

        if (defenseAnalysisService.isMatchAssignedToAnotherDefenseAnalysis(defenseAnalysis.getMatchId(), defenseAnalysis.getId())) {
            bindingResult.rejectValue("matchId", "matchId.duplicate", "Trận đấu này đã có Defense Analysis.");
        }
    }

    private void populateMatchDisplay(DefenseAnalysisDTO defenseAnalysis) {
        if (defenseAnalysis.getMatchId() == null) {
            return;
        }

        try {
            MatchDTO match = matchService.findById(defenseAnalysis.getMatchId());
            defenseAnalysis.setMatchLabel(match.getTeamA() + " vs " + match.getTeamB() + " | " + match.getMatchTime().format(MATCH_TIME_FORMATTER));
            defenseAnalysis.setMatchStatus(match.getStatus() != null ? match.getStatus().getDisplayName() : "");
        } catch (RuntimeException ex) {
            System.out.println("Runtime error: " + ex.getMessage());
        }
    }
}
