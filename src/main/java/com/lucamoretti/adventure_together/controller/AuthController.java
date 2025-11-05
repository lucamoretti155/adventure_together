package com.lucamoretti.adventure_together.controller;

import com.lucamoretti.adventure_together.dto.user.TravelerDTO;
import com.lucamoretti.adventure_together.service.user.UserService;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class AuthController {

    private UserService userService;

    @PostMapping("/register")
    public String registerTraveler(@RequestParam String password, @Valid @ModelAttribute TravelerDTO dto, BindingResult result) {
        if (result.hasErrors()) {
            return "auth/register"; // mostra errori nel form
        }
        userService.registerTraveler(dto, password);
        return "redirect:/auth/login?registered";
    }
}
