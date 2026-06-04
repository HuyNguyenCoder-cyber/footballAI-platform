package com.footballplatform.app.controller;

import com.footballplatform.app.dto.UserDTO;
import com.footballplatform.app.entity.UserRole;
import com.footballplatform.app.service.UserService;
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
@RequestMapping("/admin/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    public String list(Model model) {
        model.addAttribute("users", userService.findAll());
        return "admin/users/list";
    }

    @GetMapping("/new")
    public String createForm(Model model) {
        addFormAttributes(model);
        model.addAttribute("user", UserDTO.builder().enabled(true).role(UserRole.ADMIN).build());
        return "admin/users/form";
    }

    @PostMapping("/create")
    public String create(@Valid @ModelAttribute("user") UserDTO user,
                         BindingResult bindingResult,
                         Model model,
                         RedirectAttributes redirectAttributes) {
        validateUser(user, bindingResult, true);
        if (bindingResult.hasErrors()) {
            addFormAttributes(model);
            return "admin/users/form";
        }

        try {
            userService.create(user);
            redirectAttributes.addFlashAttribute("successMessage", "User created successfully.");
            return "redirect:/admin/users";
        } catch (RuntimeException ex) {
            System.out.println("Runtime error: " + ex.getMessage());
            addFormAttributes(model);
            model.addAttribute("errorMessage", ex.getMessage());
            return "admin/users/form";
        }
    }

    @GetMapping("/{id}/edit")
    public String editForm(@PathVariable("id") Long id, Model model, RedirectAttributes redirectAttributes) {
        try {
            addFormAttributes(model);
            model.addAttribute("user", userService.findById(id));
            return "admin/users/form";
        } catch (RuntimeException ex) {
            System.out.println("Runtime error: " + ex.getMessage());
            redirectAttributes.addFlashAttribute("errorMessage", ex.getMessage());
            return "redirect:/admin/users";
        }
    }

    @PostMapping("/{id}/update")
    public String update(@PathVariable("id") Long id,
                         @Valid @ModelAttribute("user") UserDTO user,
                         BindingResult bindingResult,
                         Model model,
                         RedirectAttributes redirectAttributes) {
        user.setId(id);
        validateUser(user, bindingResult, false);
        if (bindingResult.hasErrors()) {
            addFormAttributes(model);
            return "admin/users/form";
        }

        try {
            userService.update(user);
            redirectAttributes.addFlashAttribute("successMessage", "User updated successfully.");
            return "redirect:/admin/users";
        } catch (RuntimeException ex) {
            System.out.println("Runtime error: " + ex.getMessage());
            addFormAttributes(model);
            model.addAttribute("errorMessage", ex.getMessage());
            return "admin/users/form";
        }
    }

    @PostMapping("/{id}/delete")
    public String delete(@PathVariable("id") Long id, RedirectAttributes redirectAttributes) {
        try {
            userService.delete(id);
            redirectAttributes.addFlashAttribute("successMessage", "User deleted successfully.");
        } catch (RuntimeException ex) {
            System.out.println("Runtime error: " + ex.getMessage());
            redirectAttributes.addFlashAttribute("errorMessage", ex.getMessage());
        }
        return "redirect:/admin/users";
    }

    private void addFormAttributes(Model model) {
        model.addAttribute("roles", UserRole.values());
    }

    private void validateUser(UserDTO user, BindingResult bindingResult, boolean createMode) {
        if (user.getUsername() != null && !user.getUsername().trim().isEmpty()) {
            boolean duplicate = createMode
                    ? userService.existsByUsername(user.getUsername().trim())
                    : userService.existsByUsernameAndIdNot(user.getUsername().trim(), user.getId());
            if (duplicate) {
                bindingResult.rejectValue("username", "username.duplicate", "Username already exists.");
            }
        }

        if (createMode && (user.getPassword() == null || user.getPassword().trim().isEmpty())) {
            bindingResult.rejectValue("password", "password.required", "Password is required.");
        }
    }
}
