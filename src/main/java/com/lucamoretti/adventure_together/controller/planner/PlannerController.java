package com.lucamoretti.adventure_together.controller.planner;

import com.lucamoretti.adventure_together.dto.details.DepartureAirportDTO;
import com.lucamoretti.adventure_together.dto.participant.ParticipantDTO;
import com.lucamoretti.adventure_together.dto.review.ReviewDTO;
import com.lucamoretti.adventure_together.dto.trip.TripDTO;
import com.lucamoretti.adventure_together.dto.trip.TripItineraryDTO;
import com.lucamoretti.adventure_together.dto.user.UserDTO;
import com.lucamoretti.adventure_together.service.details.CategoryService;
import com.lucamoretti.adventure_together.service.details.CountryService;
import com.lucamoretti.adventure_together.service.details.DepartureAirportService;
import com.lucamoretti.adventure_together.service.review.ReviewService;
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
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;
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
    private final ReviewService reviewService;

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

    // Gestisce la creazione di un nuovo TripItinerary
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

        // Logica per salvare il TripItinerary
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


    // Mostra la lista degli TripItinerary creati dal planner
    @GetMapping("/trip-itinerary-created-list")
    public String showTripItineraryCreatedList(Model model) {
        // Ottieni l'ID del planner autenticato (da implementare)
        Long plannerId = userService.getCurrentUserId();
        // Recupera gli itinerari creati dal planner
        model.addAttribute("tripItineraries", tripItineraryService.getByPlannerId(plannerId));
        model.addAttribute("pageTitle", "I miei itinerari");
        return "planner/all-trip-itineraries";
    }

    // Mostra la lista degli Trip creati dal planner
    @GetMapping("/trip-created-list")
    public String showTripCreatedList(@RequestParam(value = "startDate", required = false) LocalDate startDate,
                                      @RequestParam(value = "endDate", required = false) LocalDate endDate, Model model) {
        // Ottieni l'ID del planner autenticato (da implementare)
        Long plannerId = userService.getCurrentUserId();
        // Recupera gli itinerari creati dal planner
        try{
            List<TripDTO> trips = tripService.getTripsByPlannerBetweenDates(plannerId, startDate, endDate);
            model.addAttribute("trips", trips);
            model.addAttribute("pageTitle", "I miei trip");
            model.addAttribute("startDate", startDate);
            model.addAttribute("endDate", endDate);
            return "planner/all-trips";
        }catch (DataIntegrityException e){
            model.addAttribute("errorMessage", e.getMessage());
            return "planner/all-trips";
        }
    }

    // mostra form per l'update di un TripItinerary esistente
    @GetMapping("/update-trip-itinerary/{id}")
    public String showUpdateTripItineraryForm(@PathVariable("id") Long TripItineraryId, Model model) {
        TripItineraryDTO dto;
        if (model.containsAttribute("tripItineraryDTO")) {
            dto = (TripItineraryDTO) model.getAttribute("tripItineraryDTO");
        } else {
            dto = tripItineraryService.getById(TripItineraryId);
        }
        model.addAttribute("tripItineraryDTO", dto);
        model.addAttribute("countries", countryService.getAllCountries());
        model.addAttribute("categories", categoryService.getAllCategories());
        model.addAttribute("airports", departureAirportService.getAllDepartureAirports());
        model.addAttribute("hideSearchBar", true);
        return "planner/update-trip-itinerary";
    }

    // Gestisce l'update di un TripItinerary esistente
    @PostMapping("/update-trip-itinerary/{id}")
    public String updateTripItinerary(@PathVariable("id") Long TripItineraryId, @Valid @ModelAttribute("tripItineraryDTO") TripItineraryDTO tripItineraryDTO, BindingResult bindingResult, RedirectAttributes redirectAttributes) {
        // Validazione dei campi
        if (bindingResult.hasErrors()) {
            redirectAttributes.addFlashAttribute("org.springframework.validation.BindingResult.tripItineraryDTO", bindingResult);
            redirectAttributes.addFlashAttribute("tripItineraryDTO", tripItineraryDTO);
            return "redirect:/planner/update-trip-itinerary/" + TripItineraryId;
        }
        try {
            tripItineraryService.updateItinerary(TripItineraryId, tripItineraryDTO);
            redirectAttributes.addFlashAttribute("successMessage", "Itinerario aggiornato con successo!");
            return "redirect:/planner/trip-itinerary-created-list";
        } catch (DataIntegrityException | ResourceNotFoundException | DuplicateResourceException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
            redirectAttributes.addFlashAttribute("tripItineraryDTO", tripItineraryDTO);
            return "redirect:/planner/update-trip-itinerary/" + TripItineraryId;
        }
    }

    //lista di tutti i TripItinerary esistenti
    @GetMapping("/all-trip-itineraries")
    public String showAllTripItineraries(Model model) {
        List<TripItineraryDTO> tripItineraries = tripItineraryService.getAll();
        model.addAttribute("tripItineraries", tripItineraries);
        model.addAttribute("pageTitle", "Tutti gli itinerari");
        return "planner/all-trip-itineraries";
    }

    //lista di tutti i Trip esistenti con filtro date di partenza
    @GetMapping("/all-trips")
    public String showAllTrips(@RequestParam(value = "startDate", required = false) LocalDate startDate,
                               @RequestParam(value = "endDate", required = false) LocalDate endDate,
                               Model model) {
        try{
        List<TripDTO> trips = tripService.getTripsBetweenDates(startDate, endDate);
        model.addAttribute("trips", trips);
        model.addAttribute("pageTitle", "Tutti i trip");
        model.addAttribute("startDate", startDate);
        model.addAttribute("endDate", endDate);
        return "planner/all-trips";}
        catch(DataIntegrityException e){
            model.addAttribute("errorMessage", e.getMessage());
            return "planner/all-trips";
        }
    }

    // Mostra il dettaglio di un Trip con la lista dei partecipanti
    @GetMapping("/trip-detail/{id}")
    public String showTripDetail(@PathVariable("id") Long tripId, Model model) {
        TripDTO tripDTO = tripService.getById(tripId);
        model.addAttribute("trip", tripDTO);
        TripItineraryDTO itineraryDTO = tripItineraryService.getById(tripDTO.getTripItineraryId());
        List<DepartureAirportDTO> airports = departureAirportService.getDepartureAirportsBySetOfIds(itineraryDTO.getDepartureAirportIds());
        model.addAttribute("airports", airports);
        List<ParticipantDTO> participants = tripService.getParticipantsByTripId(tripId);
        model.addAttribute("participants", participants);
        List<ReviewDTO> reviews = reviewService.getTripReviews(tripId);
        model.addAttribute("reviews", reviews);
        return "planner/trip-detail";
    }


}
