package com.footballplatform.app.controller;

import com.footballplatform.app.dto.MatchDTO;
import com.footballplatform.app.dto.TeamRecentFormDTO;
import com.footballplatform.app.entity.MatchStatus;
import com.footballplatform.app.entity.TeamSide;
import com.footballplatform.app.service.MatchService;
import com.footballplatform.app.service.TeamRecentFormService;
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
@RequestMapping("/admin/team-recent-forms")
public class TeamRecentFormController {

    private static final DateTimeFormatter MATCH_TIME_FORMATTER = DateTimeFormatter.ofPattern("dd MMM yyyy HH:mm");

    private final TeamRecentFormService teamRecentFormService;
    private final MatchService matchService;

    public TeamRecentFormController(TeamRecentFormService teamRecentFormService, MatchService matchService) {
        this.teamRecentFormService = teamRecentFormService;
        this.matchService = matchService;
    }

    @GetMapping
    public String list(Model model) {
        model.addAttribute("teamRecentForms", teamRecentFormService.findAll());
        return "admin/team-recent-forms/list";
    }

    @GetMapping("/new")
    public String createForm(Model model) {
        addFormAttributes(model, true);
        model.addAttribute("teamRecentForm", new TeamRecentFormDTO());
        return "admin/team-recent-forms/form";
    }

    @PostMapping("/create")
    public String create(@Valid @ModelAttribute("teamRecentForm") TeamRecentFormDTO teamRecentForm,
                         BindingResult bindingResult,
                         Model model,
                         RedirectAttributes redirectAttributes) {
        validateBusiness(teamRecentForm, bindingResult);
        if (bindingResult.hasErrors()) {
            addFormAttributes(model, true);
            return "admin/team-recent-forms/form";
        }

        try {
            teamRecentFormService.create(teamRecentForm);
            redirectAttributes.addFlashAttribute("successMessage", "Team recent form created successfully.");
            return "redirect:/admin/team-recent-forms";
        } catch (RuntimeException ex) {
            System.out.println("Runtime error: " + ex.getMessage());
            addFormAttributes(model, true);
            model.addAttribute("errorMessage", ex.getMessage());
            return "admin/team-recent-forms/form";
        }
    }

    @GetMapping("/{id}/edit")
    public String editForm(@PathVariable("id") Long id, Model model, RedirectAttributes redirectAttributes) {
        try {
            addFormAttributes(model, false);
            model.addAttribute("teamRecentForm", teamRecentFormService.findById(id)
                    .orElseThrow(() -> new RuntimeException("TeamRecentForm not found with id: " + id)));
            return "admin/team-recent-forms/form";
        } catch (RuntimeException ex) {
            System.out.println("Runtime error: " + ex.getMessage());
            redirectAttributes.addFlashAttribute("errorMessage", ex.getMessage());
            return "redirect:/admin/team-recent-forms";
        }
    }

    @PostMapping("/{id}/update")
    public String update(@PathVariable("id") Long id,
                         @Valid @ModelAttribute("teamRecentForm") TeamRecentFormDTO teamRecentForm,
                         BindingResult bindingResult,
                         Model model,
                         RedirectAttributes redirectAttributes) {
        teamRecentForm.setId(id);
        validateBusiness(teamRecentForm, bindingResult);
        if (bindingResult.hasErrors()) {
            addFormAttributes(model, false);
            populateMatchDisplay(teamRecentForm);
            return "admin/team-recent-forms/form";
        }

        try {
            teamRecentFormService.update(teamRecentForm);
            redirectAttributes.addFlashAttribute("successMessage", "Team recent form updated successfully.");
            return "redirect:/admin/team-recent-forms";
        } catch (RuntimeException ex) {
            System.out.println("Runtime error: " + ex.getMessage());
            addFormAttributes(model, false);
            populateMatchDisplay(teamRecentForm);
            model.addAttribute("errorMessage", ex.getMessage());
            return "admin/team-recent-forms/form";
        }
    }

    @PostMapping("/{id}/delete")
    public String delete(@PathVariable("id") Long id, RedirectAttributes redirectAttributes) {
        try {
            teamRecentFormService.delete(id);
            redirectAttributes.addFlashAttribute("successMessage", "Team recent form deleted successfully.");
        } catch (RuntimeException ex) {
            System.out.println("Runtime error: " + ex.getMessage());
            redirectAttributes.addFlashAttribute("errorMessage", ex.getMessage());
        }
        return "redirect:/admin/team-recent-forms";
    }

    private void addFormAttributes(Model model, boolean createMode) {
        model.addAttribute("teamSides", TeamSide.values());
        if (createMode) {
            model.addAttribute("matches", matchService.findByStatuses(List.of(MatchStatus.UPCOMING)));
        }
    }

    private void validateBusiness(TeamRecentFormDTO teamRecentForm, BindingResult bindingResult) {
        if (teamRecentForm.getMatchId() == null
                || teamRecentForm.getTeamSide() == null
                || bindingResult.hasFieldErrors("matchId")
                || bindingResult.hasFieldErrors("teamSide")) {
            return;
        }

        if (teamRecentFormService.existsByMatchIdAndTeamSideForAnotherRecord(
                teamRecentForm.getMatchId(),
                teamRecentForm.getTeamSide(),
                teamRecentForm.getId())) {
            bindingResult.rejectValue(
                    "teamSide",
                    "teamSide.duplicate",
                    teamRecentForm.getTeamSide() == TeamSide.HOME
                            ? "Trận đấu này đã có HOME form."
                            : "Trận đấu này đã có AWAY form."
            );
        }
    }

    private void populateMatchDisplay(TeamRecentFormDTO teamRecentForm) {
        if (teamRecentForm.getMatchId() == null) {
            return;
        }

        try {
            MatchDTO match = matchService.findById(teamRecentForm.getMatchId());
            teamRecentForm.setMatchLabel(match.getTeamA() + " vs " + match.getTeamB() + " | " + match.getMatchTime().format(MATCH_TIME_FORMATTER));
            teamRecentForm.setMatchStatus(match.getStatus() != null ? match.getStatus().getDisplayName() : "");
        } catch (RuntimeException ex) {
            System.out.println("Runtime error: " + ex.getMessage());
        }
    }
}
