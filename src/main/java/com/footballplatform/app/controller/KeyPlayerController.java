package com.footballplatform.app.controller;

import com.footballplatform.app.dto.KeyPlayerDTO;
import com.footballplatform.app.dto.MatchDTO;
import com.footballplatform.app.entity.MatchStatus;
import com.footballplatform.app.service.KeyPlayerService;
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
@RequestMapping("/admin/key-players")
public class KeyPlayerController {

    private static final DateTimeFormatter MATCH_TIME_FORMATTER = DateTimeFormatter.ofPattern("dd MMM yyyy HH:mm");

    private final KeyPlayerService keyPlayerService;
    private final MatchService matchService;

    public KeyPlayerController(KeyPlayerService keyPlayerService, MatchService matchService) {
        this.keyPlayerService = keyPlayerService;
        this.matchService = matchService;
    }

    @GetMapping
    public String list(Model model) {
        model.addAttribute("keyPlayers", keyPlayerService.findAll());
        return "admin/key-players/list";
    }

    @GetMapping("/new")
    public String createForm(Model model) {
        addFormAttributes(model);
        model.addAttribute("keyPlayer", new KeyPlayerDTO());
        return "admin/key-players/form";
    }

    @PostMapping("/create")
    public String create(@Valid @ModelAttribute("keyPlayer") KeyPlayerDTO keyPlayer,
                         BindingResult bindingResult,
                         Model model,
                         RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            addFormAttributes(model);
            return "admin/key-players/form";
        }

        try {
            keyPlayerService.create(keyPlayer);
            redirectAttributes.addFlashAttribute("successMessage", "Key player created successfully.");
            return "redirect:/admin/key-players";
        } catch (RuntimeException ex) {
            System.out.println("Runtime error: " + ex.getMessage());
            addFormAttributes(model);
            model.addAttribute("errorMessage", ex.getMessage());
            return "admin/key-players/form";
        }
    }

    @GetMapping("/{id}/edit")
    public String editForm(@PathVariable("id") Long id, Model model, RedirectAttributes redirectAttributes) {
        try {
            addFormAttributes(model);
            model.addAttribute("keyPlayer", keyPlayerService.findById(id)
                    .orElseThrow(() -> new RuntimeException("KeyPlayer not found with id: " + id)));
            return "admin/key-players/form";
        } catch (RuntimeException ex) {
            System.out.println("Runtime error: " + ex.getMessage());
            redirectAttributes.addFlashAttribute("errorMessage", ex.getMessage());
            return "redirect:/admin/key-players";
        }
    }

    @PostMapping("/{id}/update")
    public String update(@PathVariable("id") Long id,
                         @Valid @ModelAttribute("keyPlayer") KeyPlayerDTO keyPlayer,
                         BindingResult bindingResult,
                         Model model,
                         RedirectAttributes redirectAttributes) {
        keyPlayer.setId(id);

        if (bindingResult.hasErrors()) {
            addFormAttributes(model);
            populateMatchDisplay(keyPlayer);
            return "admin/key-players/form";
        }

        try {
            keyPlayerService.update(keyPlayer);
            redirectAttributes.addFlashAttribute("successMessage", "Key player updated successfully.");
            return "redirect:/admin/key-players";
        } catch (RuntimeException ex) {
            System.out.println("Runtime error: " + ex.getMessage());
            addFormAttributes(model);
            populateMatchDisplay(keyPlayer);
            model.addAttribute("errorMessage", ex.getMessage());
            return "admin/key-players/form";
        }
    }

    @PostMapping("/{id}/delete")
    public String delete(@PathVariable("id") Long id, RedirectAttributes redirectAttributes) {
        try {
            keyPlayerService.delete(id);
            redirectAttributes.addFlashAttribute("successMessage", "Key player deleted successfully.");
        } catch (RuntimeException ex) {
            System.out.println("Runtime error: " + ex.getMessage());
            redirectAttributes.addFlashAttribute("errorMessage", ex.getMessage());
        }
        return "redirect:/admin/key-players";
    }

    private void addFormAttributes(Model model) {
        model.addAttribute("matches", matchService.findByStatuses(List.of(MatchStatus.UPCOMING, MatchStatus.LIVE)));
    }

    private void populateMatchDisplay(KeyPlayerDTO keyPlayer) {
        if (keyPlayer.getMatchId() == null) {
            return;
        }

        try {
            MatchDTO match = matchService.findById(keyPlayer.getMatchId());
            keyPlayer.setMatchLabel(match.getTeamA() + " vs " + match.getTeamB() + " | " + match.getMatchTime().format(MATCH_TIME_FORMATTER));
            keyPlayer.setMatchStatus(match.getStatus() != null ? match.getStatus().getDisplayName() : "");
        } catch (RuntimeException ex) {
            System.out.println("Runtime error: " + ex.getMessage());
        }
    }
}
