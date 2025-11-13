package com.lucamoretti.adventure_together.controller.auth;

import com.lucamoretti.adventure_together.dto.user.TravelerDTO;
import com.lucamoretti.adventure_together.service.user.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

/*
    Controller per la gestione dell'autenticazione di tutti gli utenti e registrazione degli utenti Traveler
*/

@Controller
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private UserService userService;

    // LOGIN PAGE
    // la gestione effettiva del login viene fatta da Spring Security
    // nel form HTML l'endpoint POST è /auth/authenticate (vedi SecurityConfig)
    @GetMapping("/login")
    public String showLoginPage(@RequestParam(value = "error", required = false) String error,
                                @RequestParam(value = "logout", required = false) String logout,
                                @RequestParam(value = "registered", required = false) String registered,
                                Model model) {
        if (error != null) model.addAttribute("error", "Credenziali non valide");
        if (logout != null) model.addAttribute("logout", "Logout effettuato con successo");
        if (registered != null) model.addAttribute("registered", "Registrazione completata, effettua il login");
        return "auth/login";
    }

    // REGISTRAZIONE

    // Mostra la pagina di registrazione per i viaggiatori.
    @GetMapping("/register")
    public String showRegisterForm(Model model) {
        if (!model.containsAttribute("travelerDTO")) { // Per mantenere i dati in caso di errori di validazione
            model.addAttribute("travelerDTO", new TravelerDTO());
        }
        return "auth/register";
    }
    // Gestisce la registrazione di un nuovo viaggiatore.
    @PostMapping("/register")
    public String registerTraveler(@RequestParam String password,
                                   @Valid @ModelAttribute TravelerDTO dto,
                                   BindingResult result,
                                   Model model) {
        if (result.hasErrors()) {
            return "auth/register";
        }

        try {
            userService.registerTraveler(dto, password);
            return "redirect:/auth/login?registered";
        } catch (IllegalArgumentException e) {
            model.addAttribute("error", e.getMessage());
            return "auth/register";
        }
    }
}
