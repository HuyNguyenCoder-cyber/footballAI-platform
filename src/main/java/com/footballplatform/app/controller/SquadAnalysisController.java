package com.footballplatform.app.controller;

import com.footballplatform.app.dto.MatchDTO;
import com.footballplatform.app.dto.SquadAnalysisDTO;
import com.footballplatform.app.entity.MatchStatus;
import com.footballplatform.app.service.MatchService;
import com.footballplatform.app.service.SquadAnalysisService;
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
@RequestMapping("/admin/squad-analyses")
public class SquadAnalysisController {

    private static final DateTimeFormatter MATCH_TIME_FORMATTER = DateTimeFormatter.ofPattern("dd MMM yyyy HH:mm");

    private final SquadAnalysisService squadAnalysisService;
    private final MatchService matchService;

    public SquadAnalysisController(SquadAnalysisService squadAnalysisService, MatchService matchService) {
        this.squadAnalysisService = squadAnalysisService;
        this.matchService = matchService;
    }

    @GetMapping
    public String list(Model model) {
        model.addAttribute("squadAnalyses", squadAnalysisService.findAll());
        return "admin/squad-analyses/list";
    }

    @GetMapping("/new")
    public String createForm(Model model) {
        addFormAttributes(model, true);
        model.addAttribute("squadAnalysis", new SquadAnalysisDTO());
        return "admin/squad-analyses/form";
    }

    @PostMapping("/create")
    public String create(@Valid @ModelAttribute("squadAnalysis") SquadAnalysisDTO squadAnalysis,
                         BindingResult bindingResult,
                         Model model,
                         RedirectAttributes redirectAttributes) {
        validateBusiness(squadAnalysis, bindingResult);
        if (bindingResult.hasErrors()) {
            addFormAttributes(model, true);
            return "admin/squad-analyses/form";
        }

        try {
            squadAnalysisService.create(squadAnalysis);
            redirectAttributes.addFlashAttribute("successMessage", "Squad Analysis created successfully.");
            return "redirect:/admin/squad-analyses";
        } catch (RuntimeException ex) {
            System.out.println("Runtime error: " + ex.getMessage());
            addFormAttributes(model, true);
            model.addAttribute("errorMessage", ex.getMessage());
            return "admin/squad-analyses/form";
        }
    }

    @GetMapping("/{id}/edit")
    public String editForm(@PathVariable("id") Long id, Model model, RedirectAttributes redirectAttributes) {
        try {
            addFormAttributes(model, false);
            model.addAttribute("squadAnalysis", squadAnalysisService.findById(id)
                    .orElseThrow(() -> new RuntimeException("SquadAnalysis not found with id: " + id)));
            return "admin/squad-analyses/form";
        } catch (RuntimeException ex) {
            System.out.println("Runtime error: " + ex.getMessage());
            redirectAttributes.addFlashAttribute("errorMessage", ex.getMessage());
            return "redirect:/admin/squad-analyses";
        }
    }

    @PostMapping("/{id}/update")
    public String update(@PathVariable("id") Long id,
                         @Valid @ModelAttribute("squadAnalysis") SquadAnalysisDTO squadAnalysis,
                         BindingResult bindingResult,
                         Model model,
                         RedirectAttributes redirectAttributes) {
        squadAnalysis.setId(id);
        validateBusiness(squadAnalysis, bindingResult);
        if (bindingResult.hasErrors()) {
            addFormAttributes(model, false);
            populateMatchDisplay(squadAnalysis);
            return "admin/squad-analyses/form";
        }

        try {
            squadAnalysisService.update(squadAnalysis);
            redirectAttributes.addFlashAttribute("successMessage", "Squad Analysis updated successfully.");
            return "redirect:/admin/squad-analyses";
        } catch (RuntimeException ex) {
            System.out.println("Runtime error: " + ex.getMessage());
            addFormAttributes(model, false);
            populateMatchDisplay(squadAnalysis);
            model.addAttribute("errorMessage", ex.getMessage());
            return "admin/squad-analyses/form";
        }
    }

    @PostMapping("/{id}/delete")
    public String delete(@PathVariable("id") Long id, RedirectAttributes redirectAttributes) {
        try {
            squadAnalysisService.delete(id);
            redirectAttributes.addFlashAttribute("successMessage", "Squad Analysis deleted successfully.");
        } catch (RuntimeException ex) {
            System.out.println("Runtime error: " + ex.getMessage());
            redirectAttributes.addFlashAttribute("errorMessage", ex.getMessage());
        }
        return "redirect:/admin/squad-analyses";
    }

    private void addFormAttributes(Model model, boolean createMode) {
        if (createMode) {
            model.addAttribute("matches", matchService.findByStatuses(List.of(MatchStatus.UPCOMING)));
        }
    }

    private void validateBusiness(SquadAnalysisDTO squadAnalysis, BindingResult bindingResult) {
        if (squadAnalysis.getMatchId() == null || bindingResult.hasFieldErrors("matchId")) {
            return;
        }

        if (squadAnalysisService.isMatchAssignedToAnotherSquadAnalysis(squadAnalysis.getMatchId(), squadAnalysis.getId())) {
            bindingResult.rejectValue("matchId", "matchId.duplicate", "Trận đấu này đã có Squad Analysis.");
        }
    }

    private void populateMatchDisplay(SquadAnalysisDTO squadAnalysis) {
        if (squadAnalysis.getMatchId() == null) {
            return;
        }

        try {
            MatchDTO match = matchService.findById(squadAnalysis.getMatchId());
            squadAnalysis.setMatchLabel(match.getTeamA() + " vs " + match.getTeamB() + " | " + match.getMatchTime().format(MATCH_TIME_FORMATTER));
            squadAnalysis.setMatchStatus(match.getStatus() != null ? match.getStatus().getDisplayName() : "");
        } catch (RuntimeException ex) {
            System.out.println("Runtime error: " + ex.getMessage());
        }
    }
}
