package com.footballplatform.app.controller;

import com.footballplatform.app.dto.MatchDTO;
import com.footballplatform.app.dto.PredictionModelDTO;
import com.footballplatform.app.entity.MatchStatus;
import com.footballplatform.app.service.MatchService;
import com.footballplatform.app.service.PredictionModelService;
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
@RequestMapping("/admin/prediction-models")
public class PredictionModelController {

    private static final DateTimeFormatter MATCH_TIME_FORMATTER = DateTimeFormatter.ofPattern("dd MMM yyyy HH:mm");

    private final PredictionModelService predictionModelService;
    private final MatchService matchService;

    public PredictionModelController(PredictionModelService predictionModelService, MatchService matchService) {
        this.predictionModelService = predictionModelService;
        this.matchService = matchService;
    }

    @GetMapping
    public String list(Model model) {
        model.addAttribute("predictionModels", predictionModelService.findAll());
        return "admin/prediction-models/list";
    }

    @GetMapping("/new")
    public String createForm(Model model) {
        addFormAttributes(model);
        model.addAttribute("predictionModel", new PredictionModelDTO());
        return "admin/prediction-models/form";
    }

    @PostMapping("/create")
    public String create(@Valid @ModelAttribute("predictionModel") PredictionModelDTO predictionModel,
                         BindingResult bindingResult,
                         Model model,
                         RedirectAttributes redirectAttributes) {
        validatePredictionModelBusiness(predictionModel, bindingResult);
        if (bindingResult.hasErrors()) {
            addFormAttributes(model);
            return "admin/prediction-models/form";
        }

        try {
            predictionModelService.create(predictionModel);
            redirectAttributes.addFlashAttribute("successMessage", "Prediction model created successfully.");
            return "redirect:/admin/prediction-models";
        } catch (RuntimeException ex) {
            System.out.println("Runtime error: " + ex.getMessage());
            addFormAttributes(model);
            model.addAttribute("errorMessage", ex.getMessage());
            return "admin/prediction-models/form";
        }
    }

    @GetMapping("/{id}/edit")
    public String editForm(@PathVariable("id") Long id, Model model, RedirectAttributes redirectAttributes) {
        try {
            addFormAttributes(model);
            model.addAttribute("predictionModel", predictionModelService.findById(id)
                    .orElseThrow(() -> new RuntimeException("PredictionModel not found with id: " + id)));
            return "admin/prediction-models/form";
        } catch (RuntimeException ex) {
            System.out.println("Runtime error: " + ex.getMessage());
            redirectAttributes.addFlashAttribute("errorMessage", ex.getMessage());
            return "redirect:/admin/prediction-models";
        }
    }

    @PostMapping("/{id}/update")
    public String update(@PathVariable("id") Long id,
                         @Valid @ModelAttribute("predictionModel") PredictionModelDTO predictionModel,
                         BindingResult bindingResult,
                         Model model,
                         RedirectAttributes redirectAttributes) {
        predictionModel.setId(id);
        validatePredictionModelBusiness(predictionModel, bindingResult);
        if (bindingResult.hasErrors()) {
            addFormAttributes(model);
            populateMatchDisplay(predictionModel);
            return "admin/prediction-models/form";
        }

        try {
            predictionModelService.update(predictionModel);
            redirectAttributes.addFlashAttribute("successMessage", "Prediction model updated successfully.");
            return "redirect:/admin/prediction-models";
        } catch (RuntimeException ex) {
            System.out.println("Runtime error: " + ex.getMessage());
            addFormAttributes(model);
            populateMatchDisplay(predictionModel);
            model.addAttribute("errorMessage", ex.getMessage());
            return "admin/prediction-models/form";
        }
    }

    @PostMapping("/{id}/delete")
    public String delete(@PathVariable("id") Long id, RedirectAttributes redirectAttributes) {
        try {
            predictionModelService.delete(id);
            redirectAttributes.addFlashAttribute("successMessage", "Prediction model deleted successfully.");
        } catch (RuntimeException ex) {
            System.out.println("Runtime error: " + ex.getMessage());
            redirectAttributes.addFlashAttribute("errorMessage", ex.getMessage());
        }
        return "redirect:/admin/prediction-models";
    }

    private void addFormAttributes(Model model) {
        model.addAttribute("matches", matchService.findByStatuses(List.of(MatchStatus.UPCOMING, MatchStatus.LIVE)));
    }

    private void validatePredictionModelBusiness(PredictionModelDTO predictionModel, BindingResult bindingResult) {
        validateProbabilitySum(predictionModel, bindingResult);
        validatePredictionModelMatchUniqueness(predictionModel, bindingResult);
    }

    private void validateProbabilitySum(PredictionModelDTO predictionModel, BindingResult bindingResult) {
        if (predictionModel.getTeamAWinProbability() == null
                || predictionModel.getDrawProbability() == null
                || predictionModel.getTeamBWinProbability() == null) {
            return;
        }

        int total = predictionModel.getTeamAWinProbability()
                + predictionModel.getDrawProbability()
                + predictionModel.getTeamBWinProbability();

        if (total != 100) {
            bindingResult.rejectValue(
                    "teamBWinProbability",
                    "probabilitySum",
                    "Tổng xác xuất thắng thua phải bằng 100"
            );
        }
    }

    private void validatePredictionModelMatchUniqueness(PredictionModelDTO predictionModel, BindingResult bindingResult) {
        if (predictionModel.getMatchId() == null || bindingResult.hasFieldErrors("matchId")) {
            return;
        }

        if (predictionModelService.isMatchAssignedToAnotherPredictionModel(predictionModel.getMatchId(), predictionModel.getId())) {
            bindingResult.rejectValue("matchId", "matchId.duplicate", "Tráº­n Ä‘áº¥u nÃ y Ä‘Ã£ cÃ³ Prediction Model.");
        }
    }

    private void populateMatchDisplay(PredictionModelDTO predictionModel) {
        if (predictionModel.getMatchId() == null) {
            return;
        }

        try {
            MatchDTO match = matchService.findById(predictionModel.getMatchId());
            predictionModel.setMatchLabel(match.getTeamA() + " vs " + match.getTeamB() + " | " + match.getMatchTime().format(MATCH_TIME_FORMATTER));
            predictionModel.setMatchStatus(match.getStatus() != null ? match.getStatus().name() : "");
        } catch (RuntimeException ex) {
            System.out.println("Runtime error: " + ex.getMessage());
        }
    }
}
