package com.footballplatform.app.controller;

import com.footballplatform.app.dto.CompetitionDTO;
import com.footballplatform.app.service.CompetitionService;
import jakarta.validation.Valid;
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
@RequestMapping("/admin/competitions")
public class CompetitionController {

    private final CompetitionService competitionService;

    public CompetitionController(CompetitionService competitionService) {
        this.competitionService = competitionService;
    }

    @GetMapping
    public String list(Model model) {
        model.addAttribute("competitions", competitionService.findAll());
        return "competition/list";
    }

    @GetMapping("/new")
    public String createForm(Model model) {
        model.addAttribute("competition", new CompetitionDTO());
        return "competition/form";
    }

    @PostMapping("/create")
    public String createCompetition(@Valid @ModelAttribute("competition") CompetitionDTO competition,
                                    BindingResult bindingResult,
                                    Model model,
                                    RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            return "competition/form";
        }

        try {
            competitionService.create(competition);
            redirectAttributes.addFlashAttribute("successMessage", "Competition created successfully.");
            return "redirect:/admin/competitions";
        } catch (RuntimeException ex) {
            System.out.println("Runtime error: " + ex.getMessage());
            model.addAttribute("errorMessage", ex.getMessage());
            return "competition/form";
        }
    }

    @GetMapping("/{id}/edit")
    public String editForm(@PathVariable("id") Long id, Model model, RedirectAttributes redirectAttributes) {
        try {
            model.addAttribute("competition", competitionService.findById(id));
            return "competition/form";
        } catch (RuntimeException ex) {
            System.out.println("Runtime error: " + ex.getMessage());
            redirectAttributes.addFlashAttribute("errorMessage", ex.getMessage());
            return "redirect:/admin/competitions";
        }
    }

    @PostMapping("/{id}/update")
    public String updateCompetition(@PathVariable("id") Long id,
                                    @Valid @ModelAttribute("competition") CompetitionDTO competition,
                                    BindingResult bindingResult,
                                    Model model,
                                    RedirectAttributes redirectAttributes) {
        competition.setId(id);

        if (bindingResult.hasErrors()) {
            return "competition/form";
        }

        try {
            competitionService.update(competition);
            redirectAttributes.addFlashAttribute("successMessage", "Competition updated successfully.");
            return "redirect:/admin/competitions";
        } catch (RuntimeException ex) {
            System.out.println("Runtime error: " + ex.getMessage());
            model.addAttribute("errorMessage", ex.getMessage());
            return "competition/form";
        }
    }

    @PostMapping("/{id}/delete")
    public String deleteCompetition(@PathVariable("id") Long id, RedirectAttributes redirectAttributes) {
        try {
            competitionService.delete(id);
            redirectAttributes.addFlashAttribute("successMessage", "Competition deleted successfully.");
        } catch (RuntimeException ex) {
            System.out.println("Runtime error: " + ex.getMessage());
            redirectAttributes.addFlashAttribute("errorMessage", ex.getMessage());
        }
        return "redirect:/admin/competitions";
    }
}
