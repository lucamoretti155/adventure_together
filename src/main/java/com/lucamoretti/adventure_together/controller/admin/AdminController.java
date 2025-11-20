package com.lucamoretti.adventure_together.controller.admin;

import com.lucamoretti.adventure_together.dto.user.AdminDTO;
import com.lucamoretti.adventure_together.dto.user.PlannerDTO;
import com.lucamoretti.adventure_together.service.user.UserService;
import com.lucamoretti.adventure_together.util.exception.DuplicateResourceException;
import com.lucamoretti.adventure_together.util.passwordGenerator.PasswordGeneratorService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/*
  Controller per la gestione delle operazioni amministrative relative agli utenti Planner e Admin.
  Consente la creazione, visualizzazione e disattivazione degli utenti Planner e Admin.
 */

@Controller
@RequestMapping("/admin")
@RequiredArgsConstructor
public class AdminController {

    private final UserService UserService;
    private final PasswordGeneratorService passwordGeneratorService;

    // Mostra la dashboard per Admin
    @GetMapping("/dashboard")
    public String showAdminDashboard() {
        return "admin/dashboard";
    }

    // Creazione di un nuovo Planner

    // Mostra la form per la creazione di un nuovo Planner
    @GetMapping("/create-planner")
    public String showCreatePlannerForm(Model model) {
        if (!model.containsAttribute("plannerDTO")) { // Per mantenere i dati in caso di errore di validazione
            model.addAttribute("plannerDTO", new PlannerDTO()); // DTO vuoto per la form
        }
        return "admin/create-planner";
    }

   // Gestisce il submit della form per creare un nuovo Planner
    @PostMapping("/create-planner")
    public String createPlanner(
            @Valid @ModelAttribute("plannerDTO") PlannerDTO plannerDTO,
            BindingResult bindingResult,
            RedirectAttributes redirectAttributes) {

        // Validazione dei campi
        if (bindingResult.hasErrors()) {
            redirectAttributes.addFlashAttribute("org.springframework.validation.BindingResult.plannerDTO", bindingResult);
            redirectAttributes.addFlashAttribute("plannerDTO", plannerDTO);
            return "admin/create-planner";
        }

        try {
            // Password temporanea generata random attraverso il servizio dedicato (rispetta criteri di sicurezza)
            String tempPassword = passwordGeneratorService.generateSecurePassword();

            UserService.registerPlanner(plannerDTO, tempPassword);

            redirectAttributes.addFlashAttribute("successMessage",
                    "Planner creato con successo! Una password temporanea è stata impostata.");

            return "redirect:/admin/planner-list";

        } catch (DuplicateResourceException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
            redirectAttributes.addFlashAttribute("plannerDTO", plannerDTO);
            return "redirect:/admin/create-planner";
        }
    }

    // Creazione nuovo Admin

    // Mostra la form per la creazione di un nuovo Admin
    @GetMapping("/create-admin")
    public String showCreateAdminForm(Model model) {
        if (!model.containsAttribute("adminDTO")) { // Per mantenere i dati in caso di errore di validazione
            model.addAttribute("adminDTO", new AdminDTO()); // DTO vuoto per la form
        }
        return "admin/create-admin";
    }

    // Gestisce il submit della form per creare un nuovo Admin
    @PostMapping("/create-admin")
    public String createAdmin(
            @Valid @ModelAttribute("adminDTO") AdminDTO adminDTO,
            BindingResult bindingResult,
            RedirectAttributes redirectAttributes) {
        // Validazione dei campi
        if (bindingResult.hasErrors()) {
            redirectAttributes.addFlashAttribute("org.springframework.validation.BindingResult.adminDTO", bindingResult);
            redirectAttributes.addFlashAttribute("adminDTO", adminDTO);
            return "admin/create-admin";
        }
        try {
            // Password temporanea generata random attraverso il servizio dedicato (rispetta criteri di sicurezza)
            String tempPassword = passwordGeneratorService.generateSecurePassword();

            UserService.registerAdmin(adminDTO, tempPassword);

            redirectAttributes.addFlashAttribute("successMessage",
                    "Admin creato con successo! Una password temporanea è stata impostata.");

            return "redirect:/admin/admin-list";

        } catch (DuplicateResourceException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
            redirectAttributes.addFlashAttribute("adminDTO", adminDTO);
            return "redirect:/admin/create-admin";
        }
    }


    //Elenco dei planner esistenti (per gestione o attivazione)
    @GetMapping("/planner-list")
    public String listPlanners(Model model) {
        model.addAttribute("planners", UserService.getAllPlanners());
        return "/admin/planner-list";
    }

    //Elenco dei admin esistenti (per gestione o attivazione)
    @GetMapping("/admin-list")
    public String listAdmins(Model model) {
        model.addAttribute("admins", UserService.getAllAdmins());
        return "/admin/admin-list";
    }

    // Disattiva un utente (planner o admin)
    // il metodo chiama il servizio UserService per disattivare l'utenza indicata
    @PostMapping("/deactivate-user")
    public String deactivateUser(Long userId, RedirectAttributes redirectAttributes) {
        try {
            UserService.deactivateUser(userId);
            redirectAttributes.addFlashAttribute("successMessage", "Utente disattivato con successo.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }
        return "redirect:/admin/dashboard";
    }

    // Disattiva un utente (planner o admin)
    // il metodo chiama il servizio UserService per disattivare l'utenza indicata
    @PostMapping("/activate-user")
    public String activateUser(Long userId, RedirectAttributes redirectAttributes) {
        try {
            UserService.activateUser(userId);
            redirectAttributes.addFlashAttribute("successMessage", "Utente disattivato con successo.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }
        return "redirect:/admin/dashboard";
    }


}
