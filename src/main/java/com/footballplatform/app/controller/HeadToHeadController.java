package com.footballplatform.app.controller;

import com.footballplatform.app.dto.HeadToHeadDTO;
import com.footballplatform.app.dto.MatchDTO;
import com.footballplatform.app.entity.MatchStatus;
import com.footballplatform.app.service.HeadToHeadService;
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
@RequestMapping("/admin/head-to-heads")
public class HeadToHeadController {

    private static final DateTimeFormatter MATCH_TIME_FORMATTER = DateTimeFormatter.ofPattern("dd MMM yyyy HH:mm");

    private final HeadToHeadService headToHeadService;
    private final MatchService matchService;

    public HeadToHeadController(HeadToHeadService headToHeadService, MatchService matchService) {
        this.headToHeadService = headToHeadService;
        this.matchService = matchService;
    }

    @GetMapping
    public String list(Model model) {
        model.addAttribute("headToHeads", headToHeadService.findAll());
        return "admin/head-to-heads/list";
    }

    @GetMapping("/new")
    public String createForm(Model model) {
        addFormAttributes(model, true);
        model.addAttribute("headToHead", new HeadToHeadDTO());
        return "admin/head-to-heads/form";
    }

    @PostMapping("/create")
    public String create(@Valid @ModelAttribute("headToHead") HeadToHeadDTO headToHead,
                         BindingResult bindingResult,
                         Model model,
                         RedirectAttributes redirectAttributes) {
        validateBusiness(headToHead, bindingResult);
        if (bindingResult.hasErrors()) {
            addFormAttributes(model, true);
            return "admin/head-to-heads/form";
        }

        try {
            headToHeadService.create(headToHead);
            redirectAttributes.addFlashAttribute("successMessage", "Head To Head created successfully.");
            return "redirect:/admin/head-to-heads";
        } catch (RuntimeException ex) {
            System.out.println("Runtime error: " + ex.getMessage());
            addFormAttributes(model, true);
            model.addAttribute("errorMessage", ex.getMessage());
            return "admin/head-to-heads/form";
        }
    }

    @GetMapping("/{id}/edit")
    public String editForm(@PathVariable("id") Long id, Model model, RedirectAttributes redirectAttributes) {
        try {
            addFormAttributes(model, false);
            model.addAttribute("headToHead", headToHeadService.findById(id)
                    .orElseThrow(() -> new RuntimeException("HeadToHead not found with id: " + id)));
            return "admin/head-to-heads/form";
        } catch (RuntimeException ex) {
            System.out.println("Runtime error: " + ex.getMessage());
            redirectAttributes.addFlashAttribute("errorMessage", ex.getMessage());
            return "redirect:/admin/head-to-heads";
        }
    }

    @PostMapping("/{id}/update")
    public String update(@PathVariable("id") Long id,
                         @Valid @ModelAttribute("headToHead") HeadToHeadDTO headToHead,
                         BindingResult bindingResult,
                         Model model,
                         RedirectAttributes redirectAttributes) {
        headToHead.setId(id);
        validateBusiness(headToHead, bindingResult);
        if (bindingResult.hasErrors()) {
            addFormAttributes(model, false);
            populateMatchDisplay(headToHead);
            return "admin/head-to-heads/form";
        }

        try {
            headToHeadService.update(headToHead);
            redirectAttributes.addFlashAttribute("successMessage", "Head To Head updated successfully.");
            return "redirect:/admin/head-to-heads";
        } catch (RuntimeException ex) {
            System.out.println("Runtime error: " + ex.getMessage());
            addFormAttributes(model, false);
            populateMatchDisplay(headToHead);
            model.addAttribute("errorMessage", ex.getMessage());
            return "admin/head-to-heads/form";
        }
    }

    @PostMapping("/{id}/delete")
    public String delete(@PathVariable("id") Long id, RedirectAttributes redirectAttributes) {
        try {
            headToHeadService.delete(id);
            redirectAttributes.addFlashAttribute("successMessage", "Head To Head deleted successfully.");
        } catch (RuntimeException ex) {
            System.out.println("Runtime error: " + ex.getMessage());
            redirectAttributes.addFlashAttribute("errorMessage", ex.getMessage());
        }
        return "redirect:/admin/head-to-heads";
    }

    private void addFormAttributes(Model model, boolean createMode) {
        if (createMode) {
            model.addAttribute("matches", matchService.findByStatuses(List.of(MatchStatus.UPCOMING)));
        }
    }

    private void validateBusiness(HeadToHeadDTO headToHead, BindingResult bindingResult) {
        if (headToHead.getMatchId() == null || bindingResult.hasFieldErrors("matchId")) {
            return;
        }

        if (headToHeadService.isMatchAssignedToAnotherHeadToHead(headToHead.getMatchId(), headToHead.getId())) {
            bindingResult.rejectValue("matchId", "matchId.duplicate", "Trận đấu này đã có Head To Head.");
        }
    }

    private void populateMatchDisplay(HeadToHeadDTO headToHead) {
        if (headToHead.getMatchId() == null) {
            return;
        }

        try {
            MatchDTO match = matchService.findById(headToHead.getMatchId());
            headToHead.setMatchLabel(match.getTeamA() + " vs " + match.getTeamB() + " | " + match.getMatchTime().format(MATCH_TIME_FORMATTER));
            headToHead.setMatchStatus(match.getStatus() != null ? match.getStatus().getDisplayName() : "");
        } catch (RuntimeException ex) {
            System.out.println("Runtime error: " + ex.getMessage());
        }
    }
}
