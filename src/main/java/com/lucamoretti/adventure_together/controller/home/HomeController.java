package com.lucamoretti.adventure_together.controller.home;

import com.lucamoretti.adventure_together.dto.details.CategoryDTO;
import com.lucamoretti.adventure_together.dto.details.CountryDTO;
import com.lucamoretti.adventure_together.dto.details.GeoAreaDTO;
import com.lucamoretti.adventure_together.dto.trip.TripDTO;
import com.lucamoretti.adventure_together.dto.trip.TripItineraryDTO;
import com.lucamoretti.adventure_together.model.details.Country;
import com.lucamoretti.adventure_together.model.details.GeoArea;
import com.lucamoretti.adventure_together.service.details.CategoryService;
import com.lucamoretti.adventure_together.service.details.CountryService;
import com.lucamoretti.adventure_together.service.details.GeoAreaService;
import com.lucamoretti.adventure_together.service.trip.TripItineraryService;
import com.lucamoretti.adventure_together.service.trip.TripService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;

/*
    Controller per la gestione della home page
 */

@Controller
@RequestMapping("/")
@RequiredArgsConstructor
public class HomeController {

    private final TripService tripService;
    private final TripItineraryService tripItineraryService;

    // semplice redirect alla home page
    @GetMapping("/")
    public String redirectHome() {
        return "redirect:/home";
    }

    // Mostra una barra di ricerca basata sul titolo dell'itinerario
    // e una sezione con i viaggi in evidenza (prossimi viaggi prenotabili con data partenza all'interno dei 30 giorni)
    @GetMapping("/home")
    public String home(Model model) {

        // Lista di tutti gli itinerari per la barra di ricerca basata sul titolo dell'itinerario
        List<TripItineraryDTO> itineraries = tripItineraryService.getAll();
        model.addAttribute("itineraries", itineraries);

        // seconda parte  con i viaggi in evidenza
        List<TripDTO> trips = tripService.getUpcomingBookableTrips();
        model.addAttribute("trips", trips);
        // flag utilizzato per mostrare o nascondere la sezione viaggi in evidenza
        model.addAttribute("hasTrips", !trips.isEmpty());

        return "home/index";
    }

    // ricerca itinerario per titolo e redirect alla pagina di dettaglio (per la barra di ricerca globale)
    @GetMapping("/search")
    public String search(@RequestParam String title) {

        TripItineraryDTO iti = tripItineraryService.getByTitle(title);

        return "redirect:/trips/trip-itinerary/" + iti.getId();
    }

}
