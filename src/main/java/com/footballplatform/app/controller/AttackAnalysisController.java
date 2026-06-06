package com.footballplatform.app.controller;

import com.footballplatform.app.dto.AttackAnalysisDTO;
import com.footballplatform.app.dto.MatchDTO;
import com.footballplatform.app.entity.MatchStatus;
import com.footballplatform.app.service.AttackAnalysisService;
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
@RequestMapping("/admin/attack-analyses")
public class AttackAnalysisController {

    private static final DateTimeFormatter MATCH_TIME_FORMATTER = DateTimeFormatter.ofPattern("dd MMM yyyy HH:mm");

    private final AttackAnalysisService attackAnalysisService;
    private final MatchService matchService;

    public AttackAnalysisController(AttackAnalysisService attackAnalysisService, MatchService matchService) {
        this.attackAnalysisService = attackAnalysisService;
        this.matchService = matchService;
    }

    @GetMapping
    public String list(Model model) {
        model.addAttribute("attackAnalyses", attackAnalysisService.findAll());
        return "admin/attack-analyses/list";
    }

    @GetMapping("/new")
    public String createForm(Model model) {
        addFormAttributes(model, true);
        model.addAttribute("attackAnalysis", new AttackAnalysisDTO());
        return "admin/attack-analyses/form";
    }

    @PostMapping("/create")
    public String create(@Valid @ModelAttribute("attackAnalysis") AttackAnalysisDTO attackAnalysis,
                         BindingResult bindingResult,
                         Model model,
                         RedirectAttributes redirectAttributes) {
        validateBusiness(attackAnalysis, bindingResult);
        if (bindingResult.hasErrors()) {
            addFormAttributes(model, true);
            return "admin/attack-analyses/form";
        }

        try {
            attackAnalysisService.create(attackAnalysis);
            redirectAttributes.addFlashAttribute("successMessage", "Attack Analysis created successfully.");
            return "redirect:/admin/attack-analyses";
        } catch (RuntimeException ex) {
            System.out.println("Runtime error: " + ex.getMessage());
            addFormAttributes(model, true);
            model.addAttribute("errorMessage", ex.getMessage());
            return "admin/attack-analyses/form";
        }
    }

    @GetMapping("/{id}/edit")
    public String editForm(@PathVariable("id") Long id, Model model, RedirectAttributes redirectAttributes) {
        try {
            addFormAttributes(model, false);
            model.addAttribute("attackAnalysis", attackAnalysisService.findById(id)
                    .orElseThrow(() -> new RuntimeException("AttackAnalysis not found with id: " + id)));
            return "admin/attack-analyses/form";
        } catch (RuntimeException ex) {
            System.out.println("Runtime error: " + ex.getMessage());
            redirectAttributes.addFlashAttribute("errorMessage", ex.getMessage());
            return "redirect:/admin/attack-analyses";
        }
    }

    @PostMapping("/{id}/update")
    public String update(@PathVariable("id") Long id,
                         @Valid @ModelAttribute("attackAnalysis") AttackAnalysisDTO attackAnalysis,
                         BindingResult bindingResult,
                         Model model,
                         RedirectAttributes redirectAttributes) {
        attackAnalysis.setId(id);
        validateBusiness(attackAnalysis, bindingResult);
        if (bindingResult.hasErrors()) {
            addFormAttributes(model, false);
            populateMatchDisplay(attackAnalysis);
            return "admin/attack-analyses/form";
        }

        try {
            attackAnalysisService.update(attackAnalysis);
            redirectAttributes.addFlashAttribute("successMessage", "Attack Analysis updated successfully.");
            return "redirect:/admin/attack-analyses";
        } catch (RuntimeException ex) {
            System.out.println("Runtime error: " + ex.getMessage());
            addFormAttributes(model, false);
            populateMatchDisplay(attackAnalysis);
            model.addAttribute("errorMessage", ex.getMessage());
            return "admin/attack-analyses/form";
        }
    }

    @PostMapping("/{id}/delete")
    public String delete(@PathVariable("id") Long id, RedirectAttributes redirectAttributes) {
        try {
            attackAnalysisService.delete(id);
            redirectAttributes.addFlashAttribute("successMessage", "Attack Analysis deleted successfully.");
        } catch (RuntimeException ex) {
            System.out.println("Runtime error: " + ex.getMessage());
            redirectAttributes.addFlashAttribute("errorMessage", ex.getMessage());
        }
        return "redirect:/admin/attack-analyses";
    }

    private void addFormAttributes(Model model, boolean createMode) {
        if (createMode) {
            model.addAttribute("matches", matchService.findByStatuses(List.of(MatchStatus.UPCOMING)));
        }
    }

    private void validateBusiness(AttackAnalysisDTO attackAnalysis, BindingResult bindingResult) {
        if (attackAnalysis.getMatchId() == null || bindingResult.hasFieldErrors("matchId")) {
            return;
        }

        if (attackAnalysisService.isMatchAssignedToAnotherAttackAnalysis(attackAnalysis.getMatchId(), attackAnalysis.getId())) {
            bindingResult.rejectValue("matchId", "matchId.duplicate", "Trận đấu này đã có Attack Analysis.");
        }
    }

    private void populateMatchDisplay(AttackAnalysisDTO attackAnalysis) {
        if (attackAnalysis.getMatchId() == null) {
            return;
        }

        try {
            MatchDTO match = matchService.findById(attackAnalysis.getMatchId());
            attackAnalysis.setMatchLabel(match.getTeamA() + " vs " + match.getTeamB() + " | " + match.getMatchTime().format(MATCH_TIME_FORMATTER));
            attackAnalysis.setMatchStatus(match.getStatus() != null ? match.getStatus().getDisplayName() : "");
        } catch (RuntimeException ex) {
            System.out.println("Runtime error: " + ex.getMessage());
        }
    }
}
