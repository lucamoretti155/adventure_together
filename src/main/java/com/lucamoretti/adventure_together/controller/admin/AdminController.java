package com.lucamoretti.adventure_together.controller.admin;

import com.lucamoretti.adventure_together.dto.user.PlannerDTO;
import com.lucamoretti.adventure_together.service.user.UserService;
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
  Controller per la gestione delle operazioni amministrative relative ai Planner.
  Consente la creazione, visualizzazione e disattivazione dei Planner.
 */

@Controller
@RequestMapping("/admin")
@RequiredArgsConstructor
public class AdminController {

    private final UserService UserService;
    private final PasswordGeneratorService passwordGeneratorService;

    // Mostra la form per la creazione di un nuovo Planner
    @GetMapping("/create-planner")
    public String showCreatePlannerForm(Model model) {
        if (!model.containsAttribute("plannerDTO")) { // Per mantenere i dati in caso di errore di validazione
            model.addAttribute("plannerDTO", new PlannerDTO()); // DTO vuoto per la form
        }
        return "admin/planners/create";
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
            return "redirect:/admin/planners/create";
        }

        try {
            // Password temporanea generata random attraverso il servizio dedicato (rispetta criteri di sicurezza)
            String tempPassword = passwordGeneratorService.generateSecurePassword();

            UserService.registerPlanner(plannerDTO, tempPassword);

            redirectAttributes.addFlashAttribute("successMessage",
                    "Planner creato con successo! Una password temporanea è stata impostata.");

            return "redirect:/admin/planners/list";

        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
            redirectAttributes.addFlashAttribute("plannerDTO", plannerDTO);
            return "redirect:/admin/planners/create";
        }
    }

    //Elenco dei planner esistenti (per gestione o attivazione)
    @GetMapping("/planner-list")
    public String listPlanners(Model model) {
        model.addAttribute("planners", UserService.getAllPlanners());
        return "admin/planners/list";
    }

    // Disattiva un planner
    // il metodo chiama il servizio UserService per disattivare l'utenza indicata
    @PostMapping("/deactivate-planner")
    public String deactivatePlanner(Long plannerId, RedirectAttributes redirectAttributes) {
        try {
            UserService.deactivateUser(plannerId);
            redirectAttributes.addFlashAttribute("successMessage", "Planner disattivato con successo.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }
        return "redirect:/admin/planners-list";
    }
}
