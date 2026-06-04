package com.footballplatform.app.controller;

import com.footballplatform.app.dto.MatchDTO;
import com.footballplatform.app.entity.MatchStatus;
import com.footballplatform.app.repository.CompetitionRepository;
import com.footballplatform.app.service.MatchService;
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
@RequestMapping("/admin/matches")
public class MatchController {

    private final MatchService matchService;
    private final CompetitionRepository competitionRepository;

    public MatchController(MatchService matchService, CompetitionRepository competitionRepository) {
        this.matchService = matchService;
        this.competitionRepository = competitionRepository;
    }

    @GetMapping
    public String list(Model model) {
        model.addAttribute("matches", matchService.findAll());
        return "match/list";
    }

    @GetMapping("/new")
    public String createForm(Model model) {
        addFormAttributes(model);
        model.addAttribute("match", new MatchDTO());
        return "match/form";
    }

    @PostMapping("/create")
    public String createMatch(@Valid @ModelAttribute("match") MatchDTO match,
                              BindingResult bindingResult,
                              Model model,
                              RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            addFormAttributes(model);
            return "match/form";
        }

        try {
            matchService.create(match);
            redirectAttributes.addFlashAttribute("successMessage", "Match created successfully.");
            return "redirect:/admin/matches";
        } catch (RuntimeException ex) {
            System.out.println("Runtime error: " + ex.getMessage());
            addFormAttributes(model);
            model.addAttribute("errorMessage", ex.getMessage());
            return "match/form";
        }
    }

    @GetMapping("/{id}/edit")
    public String editForm(@PathVariable("id") Long id, Model model, RedirectAttributes redirectAttributes) {
        try {
            addFormAttributes(model);
            model.addAttribute("match", matchService.findById(id));
            return "match/form";
        } catch (RuntimeException ex) {
            System.out.println("Runtime error: " + ex.getMessage());
            redirectAttributes.addFlashAttribute("errorMessage", ex.getMessage());
            return "redirect:/admin/matches";
        }
    }

    @PostMapping("/{id}/update")
    public String updateMatch(@PathVariable("id") Long id,
                              @Valid @ModelAttribute("match") MatchDTO match,
                              BindingResult bindingResult,
                              Model model,
                              RedirectAttributes redirectAttributes) {
        match.setId(id);

        if (bindingResult.hasErrors()) {
            addFormAttributes(model);
            return "match/form";
        }

        try {
            matchService.update(match);
            redirectAttributes.addFlashAttribute("successMessage", "Match updated successfully.");
            return "redirect:/admin/matches";
        } catch (RuntimeException ex) {
            System.out.println("Runtime error: " + ex.getMessage());
            addFormAttributes(model);
            model.addAttribute("errorMessage", ex.getMessage());
            return "match/form";
        }
    }

    @PostMapping("/{id}/delete")
    public String deleteMatch(@PathVariable("id") Long id, RedirectAttributes redirectAttributes) {
        try {
            matchService.delete(id);
            redirectAttributes.addFlashAttribute("successMessage", "Match deleted successfully.");
        } catch (RuntimeException ex) {
            System.out.println("Runtime error: " + ex.getMessage());
            redirectAttributes.addFlashAttribute("errorMessage", ex.getMessage());
        }
        return "redirect:/admin/matches";
    }

    private void addFormAttributes(Model model) {
        model.addAttribute("competitions", competitionRepository.findAll());
        model.addAttribute("statuses", MatchStatus.values());
    }
}
