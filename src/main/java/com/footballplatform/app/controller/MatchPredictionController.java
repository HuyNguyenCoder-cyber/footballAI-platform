package com.footballplatform.app.controller;

import com.footballplatform.app.dto.MatchDTO;
import com.footballplatform.app.dto.MatchPredictionDTO;
import com.footballplatform.app.entity.MatchStatus;
import com.footballplatform.app.service.MatchPredictionService;
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
@RequestMapping("/admin/match-predictions")
public class MatchPredictionController {

    private static final DateTimeFormatter MATCH_TIME_FORMATTER = DateTimeFormatter.ofPattern("dd MMM yyyy HH:mm");

    private final MatchPredictionService matchPredictionService;
    private final MatchService matchService;

    public MatchPredictionController(MatchPredictionService matchPredictionService, MatchService matchService) {
        this.matchPredictionService = matchPredictionService;
        this.matchService = matchService;
    }

    @GetMapping
    public String list(Model model) {
        model.addAttribute("predictions", matchPredictionService.findAll());
        return "admin/match-predictions/list";
    }

    @GetMapping("/new")
    public String createForm(Model model) {
        addFormAttributes(model);
        model.addAttribute("prediction", new MatchPredictionDTO());
        return "admin/match-predictions/form";
    }

    @PostMapping("/create")
    public String create(@Valid @ModelAttribute("prediction") MatchPredictionDTO prediction,
                         BindingResult bindingResult,
                         Model model,
                         RedirectAttributes redirectAttributes) {
        validateMatchPredictionBusiness(prediction, bindingResult);
        if (bindingResult.hasErrors()) {
            addFormAttributes(model);
            return "admin/match-predictions/form";
        }
        try {
            matchPredictionService.create(prediction);
            redirectAttributes.addFlashAttribute("successMessage", "Prediction created successfully.");
            return "redirect:/admin/match-predictions";
        } catch (RuntimeException ex) {
            System.out.println("Runtime error: " + ex.getMessage());
            addFormAttributes(model);
            model.addAttribute("errorMessage", ex.getMessage());
            return "admin/match-predictions/form";
        }
    }

    @GetMapping("/{id}/edit")
    public String editForm(@PathVariable("id") Long id, Model model, RedirectAttributes redirectAttributes) {
        try {
            addFormAttributes(model);
            model.addAttribute("prediction", matchPredictionService.findById(id)
                    .orElseThrow(() -> new RuntimeException("MatchPrediction not found with id: " + id)));
            return "admin/match-predictions/form";
        } catch (RuntimeException ex) {
            System.out.println("Runtime error: " + ex.getMessage());
            redirectAttributes.addFlashAttribute("errorMessage", ex.getMessage());
            return "redirect:/admin/match-predictions";
        }
    }

    @PostMapping("/{id}/update")
    public String update(@PathVariable("id") Long id,
                         @Valid @ModelAttribute("prediction") MatchPredictionDTO prediction,
                         BindingResult bindingResult,
                         Model model,
                         RedirectAttributes redirectAttributes) {
        prediction.setId(id);
        validateMatchPredictionBusiness(prediction, bindingResult);
        if (bindingResult.hasErrors()) {
            addFormAttributes(model);
            populateMatchDisplay(prediction);
            return "admin/match-predictions/form";
        }
        try {
            matchPredictionService.update(prediction);
            redirectAttributes.addFlashAttribute("successMessage", "Prediction updated successfully.");
            return "redirect:/admin/match-predictions";
        } catch (RuntimeException ex) {
            System.out.println("Runtime error: " + ex.getMessage());
            addFormAttributes(model);
            populateMatchDisplay(prediction);
            model.addAttribute("errorMessage", ex.getMessage());
            return "admin/match-predictions/form";
        }
    }

    @PostMapping("/{id}/delete")
    public String delete(@PathVariable("id") Long id, RedirectAttributes redirectAttributes) {
        try {
            matchPredictionService.delete(id);
            redirectAttributes.addFlashAttribute("successMessage", "Prediction deleted successfully.");
        } catch (RuntimeException ex) {
            System.out.println("Runtime error: " + ex.getMessage());
            redirectAttributes.addFlashAttribute("errorMessage", ex.getMessage());
        }
        return "redirect:/admin/match-predictions";
    }

    private void addFormAttributes(Model model) {
        model.addAttribute("matches", matchService.findByStatuses(List.of(MatchStatus.UPCOMING, MatchStatus.LIVE)));
    }

    private void validateMatchPredictionBusiness(MatchPredictionDTO prediction, BindingResult bindingResult) {
        if (prediction.getMatchId() == null || bindingResult.hasFieldErrors("matchId")) {
            return;
        }

        if (matchPredictionService.isMatchAssignedToAnotherPrediction(prediction.getMatchId(), prediction.getId())) {
            bindingResult.rejectValue("matchId", "matchId.duplicate", "Tráº­n Ä‘áº¥u nÃ y Ä‘Ã£ cÃ³ AI Prediction.");
        }
    }

    private void populateMatchDisplay(MatchPredictionDTO prediction) {
        if (prediction.getMatchId() == null) {
            return;
        }

        try {
            MatchDTO match = matchService.findById(prediction.getMatchId());
            prediction.setMatchLabel(match.getTeamA() + " vs " + match.getTeamB() + " | " + match.getMatchTime().format(MATCH_TIME_FORMATTER));
            prediction.setMatchStatus(match.getStatus() != null ? match.getStatus().getDisplayName() : "");
        } catch (RuntimeException ex) {
            System.out.println("Runtime error: " + ex.getMessage());
        }
    }
}
