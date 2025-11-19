package com.lucamoretti.adventure_together.controller.auth;

import com.lucamoretti.adventure_together.dto.user.TravelerDTO;
import com.lucamoretti.adventure_together.service.user.UserService;
import com.lucamoretti.adventure_together.util.exception.DataIntegrityException;
import com.lucamoretti.adventure_together.util.exception.DuplicateResourceException;
import com.lucamoretti.adventure_together.util.exception.ResourceNotFoundException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/*
    Controller per la gestione dell'autenticazione di tutti gli utenti e registrazione degli utenti Traveler
*/

@Controller
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final UserService userService;

    // LOGIN PAGE

    // la gestione effettiva del login viene fatta da Spring Security
    // nel form HTML l'endpoint POST è /auth/authenticate (vedi SecurityConfig)
    @GetMapping("/login")
    public String showLoginPage(@RequestParam(value = "error", required = false) String error,
                                @RequestParam(value = "logout", required = false) String logout,
                                @RequestParam(value = "registered", required = false) String registered,
                                Model model) {
        if (error != null) model.addAttribute("errorMessage", "Credenziali non valide");
        if (logout != null) model.addAttribute("logout", "Logout effettuato con successo");
        if (registered != null) model.addAttribute("registered", "Registrazione completata, effettua il login");
        return "auth/login";
    }

    // REGISTRAZIONE

    // Mostra la pagina di registrazione per i viaggiatori.
    @GetMapping("/register")
    public String showRegisterForm(@ModelAttribute("travelerDTO") TravelerDTO dto,
                                   Model model)  {
        if (!model.containsAttribute("travelerDTO")) { // Per mantenere i dati in caso di errori di validazione
            model.addAttribute("travelerDTO", new TravelerDTO());
        }
        return "auth/register";
    }
    // Gestisce la registrazione di un nuovo viaggiatore.
    @PostMapping("/register")
    public String registerTraveler(@RequestParam String password,
                                   @Valid @ModelAttribute("travelerDTO") TravelerDTO dto,
                                   BindingResult result,
                                   RedirectAttributes redirectAttributes) {

        // Errori di validazione dei campi
        if (result.hasErrors()) {
            redirectAttributes.addFlashAttribute("travelerDTO", dto);
            redirectAttributes.addFlashAttribute(
                    "org.springframework.validation.BindingResult.travelerDTO", result);
            return "redirect:/auth/register";
        }

        try {
            userService.registerTraveler(dto, password);

            redirectAttributes.addFlashAttribute(
                    "registered", "Registrazione completata, effettua il login");
            return "redirect:/auth/login";

        } catch (DataIntegrityException | DuplicateResourceException e) {  // per password o età non valida o email già esistente

            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
            redirectAttributes.addFlashAttribute("travelerDTO", dto);
            return "redirect:/auth/register";
        }
    }

    // LOGOUT

    @GetMapping("/logout")
    public String logoutSuccess(Model model) {
        model.addAttribute("logoutMessage", "Hai effettuato il logout con successo.");
        return "auth/logout";
    }

    // RESET PASSWORD

    // Form per richiesta reset password
    @GetMapping("/forgot-password")
    public String showForgotPasswordForm(Model model) {
        model.addAttribute("email", "");
        return "auth/forgot-password"; // template Thymeleaf
    }

    // Invio email per reset password
    @PostMapping("/forgot-password")
    public String processForgotPassword(
            @RequestParam("email") String email,
            RedirectAttributes redirectAttributes) {

        try {
            userService.generatePasswordResetToken(email);
            redirectAttributes.addFlashAttribute("successMessage", "Email inviata! Controlla la tua casella di posta.");
        } catch (ResourceNotFoundException e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Nessun account trovato con questa email.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Si è verificato un errore inatteso.");
        }
        return "redirect:/auth/forgot-password";
    }
    // Form per il reset della password
    @GetMapping("/reset-password")
    public String showResetPasswordForm(
            @RequestParam("token") String token,
            Model model) {

        model.addAttribute("token", token);
        return "auth/reset-password";  // template Thymeleaf
    }

    // Gestione del reset della password
    @PostMapping("/reset-password")
    public String processResetPassword(
            @RequestParam("token") String token,
            @RequestParam("newPassword") String newPassword,
            RedirectAttributes redirectAttributes) {

        try {
            userService.resetPassword(token, newPassword);
            redirectAttributes.addFlashAttribute("successMessage", "Password aggiornata! Ora puoi effettuare il login.");
            return "redirect:/auth/login";

        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
            return "redirect:/auth/reset-password?token=" + token;
        }
    }












}
