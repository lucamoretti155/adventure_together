package com.lucamoretti.adventure_together.controller.planner;

import com.lucamoretti.adventure_together.dto.details.CountryDTO;
import com.lucamoretti.adventure_together.dto.trip.TripDTO;
import com.lucamoretti.adventure_together.dto.trip.TripItineraryDTO;
import com.lucamoretti.adventure_together.dto.user.UserDTO;
import com.lucamoretti.adventure_together.model.user.Planner;
import com.lucamoretti.adventure_together.model.user.User;
import com.lucamoretti.adventure_together.service.details.CategoryService;
import com.lucamoretti.adventure_together.service.details.CountryService;
import com.lucamoretti.adventure_together.service.details.DepartureAirportService;
import com.lucamoretti.adventure_together.service.trip.TripItineraryService;
import com.lucamoretti.adventure_together.service.trip.TripService;
import com.lucamoretti.adventure_together.service.user.UserService;
import com.lucamoretti.adventure_together.util.exception.DataIntegrityException;
import com.lucamoretti.adventure_together.util.exception.DuplicateResourceException;
import com.lucamoretti.adventure_together.util.exception.ResourceNotFoundException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.ArrayList;
import java.util.List;

/*
    Controller per la gestione del planner dei viaggi.
    Permette agli utenti Planner e Admin di creare e visualizzare i loro TripItinerary e Trip creati.
 */

@Controller
@RequestMapping("/planner")
@RequiredArgsConstructor
public class PlannerController {

    private final TripItineraryService tripItineraryService;
    private final TripService tripService;
    private final UserService userService;
    private final CountryService countryService;
    private final CategoryService categoryService;
    private final DepartureAirportService departureAirportService;

    // Mostra la dashboard per Planner
    @GetMapping("/dashboard")
    public String showAdminDashboard() {
        return "planner/dashboard";
    }

    // Mostra la form per la creazione di un nuovo TripItinerary
    @GetMapping("/create-trip-itinerary")
    public String showCreateTripItineraryForm(Model model, Authentication auth) {

        TripItineraryDTO dto;

        if (model.containsAttribute("tripItineraryDTO")) {
            dto = (TripItineraryDTO) model.getAttribute("tripItineraryDTO");
        } else {
            dto = new TripItineraryDTO();
        }

        UserDTO user = userService.getByEmail(auth.getName()).orElseThrow(()
                -> new ResourceNotFoundException("Utente", "email", auth.getName()));

        dto.setPlannerId(user.getId());

        model.addAttribute("tripItineraryDTO", dto);
        model.addAttribute("countries", countryService.getAllCountries());
        model.addAttribute("categories", categoryService.getAllCategories());
        model.addAttribute("airports", departureAirportService.getAllDepartureAirports());
        model.addAttribute("hideSearchBar", true);
        return "planner/create-trip-itinerary";
    }

    @PostMapping("/create-trip-itinerary")
    public String createTripItinerary(@Valid @ModelAttribute("tripItineraryDTO") TripItineraryDTO tripItineraryDTO, BindingResult bindingResult, RedirectAttributes redirectAttributes) {
        //System.out.println(">>> POST RICEVUTO");
        //System.out.println("DTO ricevuto = " + tripItineraryDTO);
        // Validazione dei campi
        if (bindingResult.hasErrors()) {
            //System.out.println(bindingResult.getAllErrors());
            redirectAttributes.addFlashAttribute("org.springframework.validation.BindingResult.tripItineraryDTO", bindingResult);
            redirectAttributes.addFlashAttribute("tripItineraryDTO", tripItineraryDTO);
            return "redirect:/planner/create-trip-itinerary";
        }

        // Logica per salvare il TripItinerary (da implementare)
        try{
            tripItineraryService.createItinerary(tripItineraryDTO);
            redirectAttributes.addFlashAttribute("successMessage", "Itinerario creato con successo!");
            return "redirect:/planner/trip-itinerary-created-list";
        } catch (DataIntegrityException | ResourceNotFoundException | DuplicateResourceException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
            redirectAttributes.addFlashAttribute("tripItineraryDTO", tripItineraryDTO);
            return "redirect:/planner/create-trip-itinerary";
        }
    }


    // Mostra form per creazione nuovo Trip
    @GetMapping("/create-trip")
    public String showCreateTripForm(Model model, Authentication auth) {
        TripDTO dto;
        if (model.containsAttribute("tripDTO")) {
            dto = (TripDTO) model.getAttribute("tripDTO");
        } else {
            dto = new TripDTO();
        }
        Long userId = userService.getCurrentUserId();
        dto.setPlannerId(userId);
        model.addAttribute("tripDTO", dto);
        model.addAttribute("hideSearchBar", true);
        List<TripItineraryDTO> tripItineraries = tripItineraryService.getAll();
        model.addAttribute("tripItineraries", tripItineraries);
        return "planner/create-trip";
    }

    // Gestisce la creazione di un nuovo Trip
    @PostMapping("/create-trip")
    public String createTrip(@Valid @ModelAttribute("tripDTO") TripDTO tripDTO, BindingResult bindingResult, RedirectAttributes redirectAttributes) {
        // Validazione dei campi
        if (bindingResult.hasErrors()) {
            redirectAttributes.addFlashAttribute("org.springframework.validation.BindingResult.tripDTO", bindingResult);
            redirectAttributes.addFlashAttribute("tripDTO", tripDTO);
            return "redirect:/planner/create-trip";
        }

        try{
            tripService.createTrip(tripDTO);
            redirectAttributes.addFlashAttribute("successMessage", "Viaggio creato con successo!");
            return "redirect:/planner/trip-created-list";
        } catch (DataIntegrityException | ResourceNotFoundException | DuplicateResourceException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
            redirectAttributes.addFlashAttribute("tripDTO", tripDTO);
            return "redirect:/planner/create-trip";
        }
    }

    @GetMapping("/trip-itinerary-created-list")
    public String showTripItineraryCreatedList(Model model) {
        // Ottieni l'ID del planner autenticato (da implementare)
        Long plannerId = userService.getCurrentUserId();
        // Recupera gli itinerari creati dal planner
        model.addAttribute("itineraries", tripItineraryService.getByPlannerId(plannerId));
        return "planner/trip-itinerary-created-list";
    }

    @GetMapping("/trip-created-list")
    public String showTripCreatedList(Model model) {
        // Ottieni l'ID del planner autenticato (da implementare)
        Long plannerId = userService.getCurrentUserId();
        // Recupera gli itinerari creati dal planner
        model.addAttribute("trips", tripService.getTripsByPlanner(plannerId));
        return "planner/trip-created-list";
    }
}
