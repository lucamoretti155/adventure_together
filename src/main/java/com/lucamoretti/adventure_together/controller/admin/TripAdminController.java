package com.lucamoretti.adventure_together.controller.admin;

import com.lucamoretti.adventure_together.dto.trip.TripDTO;
import com.lucamoretti.adventure_together.dto.user.AdminDTO;
import com.lucamoretti.adventure_together.dto.user.PlannerDTO;
import com.lucamoretti.adventure_together.service.trip.TripService;
import com.lucamoretti.adventure_together.service.user.UserService;
import com.lucamoretti.adventure_together.util.exception.DataIntegrityException;
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

import java.util.List;

/*
    Controller per la gestione delle operazioni amministrative sui Trip.
    Permette agli amministratori di cancellare viaggi tramite interfaccia web.
 */

@Controller
@RequestMapping("/admin")
@RequiredArgsConstructor
public class TripAdminController {

    private final UserService UserService;
    private final PasswordGeneratorService passwordGeneratorService;
    private final TripService tripService;

    // End point per cancellare un Trip
    // Get mostra la lista di tutti i Trip in stato ToBeConfirmed
    // Post riceve l'id del Trip da cancellare

    @GetMapping("/cancel-trip")
    public String showCancelTripForm(Model model) {
        List<TripDTO> trips = tripService.getToBeConfirmedTrips();
        model.addAttribute("trips", trips);
        return "admin/cancel-trip";
    }

    @PostMapping("/cancel-trip")
    public String cancelTrip(Long tripId, RedirectAttributes redirectAttributes) {
        try {
            tripService.cancelTrip(tripId);
            redirectAttributes.addFlashAttribute("successMessage", "Viaggio cancellato con successo.");
        } catch (DataIntegrityException e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Errore durante l'annullamento del viaggio: " + e.getMessage());
        }
        return "redirect:/admin/cancel-trip";
    }

}
