package com.footballplatform.app.controller;

import com.footballplatform.app.dto.KeyPlayerDTO;
import com.footballplatform.app.dto.MatchDTO;
import com.footballplatform.app.service.KeyPlayerService;
import com.footballplatform.app.service.MatchService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class KeyPlayerViewController {

    private final KeyPlayerService keyPlayerService;
    private final MatchService matchService;

    public KeyPlayerViewController(KeyPlayerService keyPlayerService, MatchService matchService) {
        this.keyPlayerService = keyPlayerService;
        this.matchService = matchService;
    }

    @GetMapping("/key-players/{id}")
    public String detail(@PathVariable("id") Long id, Model model, RedirectAttributes redirectAttributes) {
        try {
            KeyPlayerDTO keyPlayer = keyPlayerService.findById(id)
                    .orElseThrow(() -> new RuntimeException("KeyPlayer not found with id: " + id));
            MatchDTO match = matchService.findById(keyPlayer.getMatchId());

            model.addAttribute("keyPlayer", keyPlayer);
            model.addAttribute("match", match);
            return "key-player/detail";
        } catch (RuntimeException ex) {
            System.out.println("Runtime error: " + ex.getMessage());
            redirectAttributes.addFlashAttribute("errorMessage", ex.getMessage());
            return "redirect:/";
        }
    }
}
