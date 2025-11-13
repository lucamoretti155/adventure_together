package com.lucamoretti.adventure_together.controller.trips;

import com.lucamoretti.adventure_together.dto.details.CategoryDTO;
import com.lucamoretti.adventure_together.dto.details.CountryDTO;
import com.lucamoretti.adventure_together.dto.details.DepartureAirportDTO;
import com.lucamoretti.adventure_together.dto.details.GeoAreaDTO;
import com.lucamoretti.adventure_together.dto.review.ReviewDTO;
import com.lucamoretti.adventure_together.dto.trip.TripDTO;
import com.lucamoretti.adventure_together.dto.trip.TripItineraryDTO;
import com.lucamoretti.adventure_together.dto.trip.TripItineraryDayDTO;
import com.lucamoretti.adventure_together.service.details.CategoryService;
import com.lucamoretti.adventure_together.service.details.CountryService;
import com.lucamoretti.adventure_together.service.details.DepartureAirportService;
import com.lucamoretti.adventure_together.service.details.GeoAreaService;
import com.lucamoretti.adventure_together.service.review.ReviewService;
import com.lucamoretti.adventure_together.service.trip.TripItineraryDayService;
import com.lucamoretti.adventure_together.service.trip.TripItineraryService;
import com.lucamoretti.adventure_together.service.trip.TripService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import java.util.List;

/*
    Dashboard per la visualizzazione di tutti i viaggi disponibili con filtri di ricerca
    L'utente avrà una lista completa di tutte le geoAree con all'interno i rispettivi paesi
    E' inoltre possibile filtrare i viaggi per categoria o per titolo viaggio
    Ogni viaggio porterà alla pagina di dettaglio dell'itinerario di viaggio
*/

@Controller
@RequestMapping("/trips")
@RequiredArgsConstructor
public class TripController {

    private final TripService tripService;
    private final TripItineraryService tripItineraryService;
    private final CountryService countryService;
    private final GeoAreaService geoAreaService;
    private final CategoryService categoryService;
    private final DepartureAirportService departureAirportService;
    private final TripItineraryDayService tripItineraryDayService;
    private final ReviewService reviewService;

    // dashboard principale con filtri di ricerca
    @GetMapping("/dashboard")
    public String dashboard(Model model) {
        // Liste di categorie, geoAree e countries per i filtri di ricerca
        model.addAttribute("categories", categoryService.getAllCategories());
        model.addAttribute("geoAreas", geoAreaService.getAllGeoAreas());
        model.addAttribute("countries", countryService.getAllCountries());
        return "trips/dashboard";
    }
    //pagina con Itinerari filtrati per categoria/e
    @GetMapping("/categories")
    public String itinerariesByCategories(
            @RequestParam(required = false) List<Long> categoryIds,
            Model model) {

        if (categoryIds == null || categoryIds.isEmpty()) {
            model.addAttribute("tripItineraries", tripItineraryService.getAll());
        } else {
            model.addAttribute("tripItineraries", tripItineraryService.getAllByCategoryIds(categoryIds));
        }

        model.addAttribute("selectedCategories", categoryIds);

        return "trips/itineraries-by-categories";
    }

    //pagina con itinerari filtrati per geoArea
    @GetMapping("/geo-area/{id}")
    public String dashboardGeoArea(@PathVariable Long id, Model model) {

        //dettagli geoArea per titolo pagina
        GeoAreaDTO geoArea = geoAreaService.getGeoAreaById(id);
        model.addAttribute("geoArea", geoArea);

        // Lista di countries appartenenti alla geoArea selezionata per i filtri di ricerca
        // Utile per creare lista per filtro di ricerca delle countries appartenenti alla geoArea della pagina
        List<CountryDTO> countries = countryService.getAllCountriesByGeoAreaId(id);
        model.addAttribute("countries", countries);

        List<TripItineraryDTO> itinerariesByGeoArea = tripItineraryService.getAllByGeoAreaId(id);
        model.addAttribute("itinerariesByGeoArea", itinerariesByGeoArea);

        return "trips/geo-area";
    }

    //pagina con itinerari filtrati per country
    @GetMapping("/country/{id}")
    public String dashboardCountry(@PathVariable Long id, Model model) {

        //dettagli country per titolo pagina
        CountryDTO country = countryService.getCountryById(id);
        model.addAttribute("country", country);

        //dettaglio geoArea per i filtri di ricerca
        GeoAreaDTO geoArea = geoAreaService.getGeoAreaById(country.getGeoAreaId());
        model.addAttribute("geoArea", geoArea);

        List<TripItineraryDTO> itinerariesByCountry = tripItineraryService.getAllByCountryId(id);
        model.addAttribute("itinerariesByCountry", itinerariesByCountry);

        return "trips/country";
    }
    //pagina di dettaglio itinerario di viaggio
    @GetMapping("/trip-itinerary/{id}")
    public String dashboardTripItinerary(@PathVariable Long id, Model model) {

        //dettagli itinerario per titolo pagina
        TripItineraryDTO tripItinerary = tripItineraryService.getById(id);
        model.addAttribute("tripItinerary", tripItinerary);

        //Lista di tutte le Countries associate all'itinerario selezionato
        List<CountryDTO> countries = countryService.getCountryBySetOfId(tripItinerary.getCountryIds());
        model.addAttribute("countries", countries);

        //Lista di tutti i DepartureAirport associati all'itinerario selezionato
        List<DepartureAirportDTO> departureAirports = departureAirportService.getDepartureAirportsBySetOfIds(tripItinerary.getDepartureAirportIds());
        model.addAttribute("departureAirports", departureAirports);

        //Lista di tutti i TripItineraryDay associati all'itinerario selezionato
        List<TripItineraryDayDTO> itineraryDays = tripItineraryDayService.getDaysByItinerary(id);
        model.addAttribute("itineraryDays", itineraryDays);

        //Lista di tutti i Trip associati all'itinerario selezionato (futuri e disponibili per la prenotazione)
        List<TripDTO> trips = tripService.getBookableTripsByItinerary(id);
        model.addAttribute("trips", trips);

        //Lista di tutte le reviews associate ai Trip (associati all'itinerario selezionato)
        List<ReviewDTO> reviews = reviewService.getAllReviewsByTripItineraryId(id);
        model.addAttribute("reviews", reviews);

        //media valutazione complessiva reviews
        Float averageRating = reviewService.getAverageScoreForTripItinerary(id);
        model.addAttribute("averageRating", averageRating);

        return "trips/trip-itinerary";

    }
}
