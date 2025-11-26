package com.lucamoretti.adventure_together.controller.trips;

import com.lucamoretti.adventure_together.dto.trip.TripDTO;
import com.lucamoretti.adventure_together.dto.trip.TripItineraryDTO;
import com.lucamoretti.adventure_together.dto.trip.TripItineraryDayDTO;
import com.lucamoretti.adventure_together.service.trip.TripItineraryDayService;
import com.lucamoretti.adventure_together.service.trip.TripItineraryService;
import com.lucamoretti.adventure_together.service.trip.TripService;
import com.lucamoretti.adventure_together.service.review.ReviewService;
import com.lucamoretti.adventure_together.dto.details.GeoAreaDTO;
import com.lucamoretti.adventure_together.dto.details.CountryDTO;
import com.lucamoretti.adventure_together.dto.details.DepartureAirportDTO;
import com.lucamoretti.adventure_together.service.details.CountryService;
import com.lucamoretti.adventure_together.service.details.GeoAreaService;
import com.lucamoretti.adventure_together.service.details.CategoryService;
import com.lucamoretti.adventure_together.service.details.DepartureAirportService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.ui.ExtendedModelMap;
import org.springframework.ui.Model;

import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TripControllerTest {

    @Mock private TripService tripService;
    @Mock private TripItineraryService tripItineraryService;
    @Mock private CountryService countryService;
    @Mock private GeoAreaService geoAreaService;
    @Mock private CategoryService categoryService;
    @Mock private DepartureAirportService departureAirportService;
    @Mock private TripItineraryDayService tripItineraryDayService;
    @Mock private ReviewService reviewService;

    @InjectMocks
    private TripController controller;

    private GeoAreaDTO geoArea;
    private CountryDTO country;
    private TripItineraryDTO itinerary;
    private TripDTO trip;
    private DepartureAirportDTO airport;
    private TripItineraryDayDTO day;

    @BeforeEach
    void setup() {
        geoArea = GeoAreaDTO.builder().id(7L).geoArea("Nord Europa").build();

        country = CountryDTO.builder()
                .id(5L)
                .country("Islanda")
                .geoAreaId(7L)
                .build();

        itinerary = TripItineraryDTO.builder()
                .id(12L)
                .title("Islanda Adventure")
                .countryIds(Set.of(5L))
                .departureAirportIds(Set.of(3L))
                .build();

        trip = TripDTO.builder().id(44L).tripItineraryId(12L).build();

        airport = DepartureAirportDTO.builder().id(3L).code("MXP").name("Malpensa").build();

        day = TripItineraryDayDTO.builder().id(99L).dayNumber(1).title("Arrivo a Reykjavik").build();
    }

    /* -------------------------------------------------------
     *  GET /trips/dashboard
     * ------------------------------------------------------- */
    @Test
    void dashboard_ok() {
        Model model = new ExtendedModelMap();

        when(categoryService.getAllCategories()).thenReturn(List.of());
        when(geoAreaService.getAllGeoAreas()).thenReturn(List.of());
        when(countryService.getAllCountries()).thenReturn(List.of());

        String view = controller.dashboard(model);

        assertEquals("trips/dashboard", view);
        assertTrue(model.containsAttribute("categories"));
        assertTrue(model.containsAttribute("geoAreas"));
        assertTrue(model.containsAttribute("countries"));
    }

    /* -------------------------------------------------------
     *  GET /trips/categories
     * ------------------------------------------------------- */
    @Test
    void itinerariesByCategories_empty_getAll() {
        Model model = new ExtendedModelMap();
        when(tripItineraryService.getAll()).thenReturn(List.of(itinerary));

        String view = controller.itinerariesByCategories(null, model);

        assertEquals("trips/itineraries-by-categories", view);
        assertEquals(List.of(itinerary), model.getAttribute("tripItineraries"));
    }

    @Test
    void itinerariesByCategories_filtered() {
        Model model = new ExtendedModelMap();
        when(tripItineraryService.getAllByCategoryIds(List.of(7L)))
                .thenReturn(List.of(itinerary));

        String view = controller.itinerariesByCategories(List.of(7L), model);

        assertEquals("trips/itineraries-by-categories", view);
        assertEquals(List.of(itinerary), model.getAttribute("tripItineraries"));
        assertEquals(List.of(7L), model.getAttribute("selectedCategories"));
    }

    /* -------------------------------------------------------
     *  GET /trips/geo-area/{id}
     * ------------------------------------------------------- */
    @Test
    void dashboardGeoArea_ok() {
        Model model = new ExtendedModelMap();

        when(geoAreaService.getGeoAreaById(7L)).thenReturn(geoArea);
        when(countryService.getAllCountriesByGeoAreaId(7L)).thenReturn(List.of(country));
        when(tripItineraryService.getAllByGeoAreaId(7L)).thenReturn(List.of(itinerary));

        String view = controller.dashboardGeoArea(7L, model);

        assertEquals("trips/geo-area", view);
        assertEquals(geoArea, model.getAttribute("geoArea"));
        assertEquals(List.of(country), model.getAttribute("countries"));
        assertEquals(List.of(itinerary), model.getAttribute("itinerariesByGeoArea"));
    }

    /* -------------------------------------------------------
     *  GET /trips/country/{id}
     * ------------------------------------------------------- */
    @Test
    void dashboardCountry_ok() {
        Model model = new ExtendedModelMap();

        when(countryService.getCountryById(5L)).thenReturn(country);
        when(geoAreaService.getGeoAreaById(7L)).thenReturn(geoArea);
        when(tripItineraryService.getAllByCountryId(5L)).thenReturn(List.of(itinerary));

        String view = controller.dashboardCountry(5L, model);

        assertEquals("trips/country", view);
        assertEquals(country, model.getAttribute("country"));
        assertEquals(geoArea, model.getAttribute("geoArea"));
        assertEquals(List.of(itinerary), model.getAttribute("itinerariesByCountry"));
    }

    /* -------------------------------------------------------
     *  GET /trips/trip-itinerary/{id}
     * ------------------------------------------------------- */
    @Test
    void dashboardTripItinerary_ok() {
        Model model = new ExtendedModelMap();

        when(tripItineraryService.getById(12L)).thenReturn(itinerary);
        when(countryService.getCountryBySetOfId(Set.of(5L))).thenReturn(List.of(country));
        when(departureAirportService.getDepartureAirportsBySetOfIds(Set.of(3L)))
                .thenReturn(List.of(airport));
        when(tripItineraryDayService.getDaysByItinerary(12L))
                .thenReturn(List.of(day));
        when(tripService.getBookableTripsByItinerary(12L))
                .thenReturn(List.of(trip));
        when(reviewService.getAllReviewsByTripItineraryId(12L))
                .thenReturn(List.of());
        when(reviewService.getAverageScoreForTripItinerary(12L))
                .thenReturn(4.7F);

        String view = controller.dashboardTripItinerary(12L, model);

        assertEquals("trips/trip-itinerary", view);

        assertEquals(itinerary, model.getAttribute("tripItinerary"));
        assertEquals(List.of(country), model.getAttribute("countries"));
        assertEquals(List.of(airport), model.getAttribute("departureAirports"));
        assertEquals(List.of(day), model.getAttribute("itineraryDays"));
        assertEquals(List.of(trip), model.getAttribute("trips"));
        assertEquals(List.of(), model.getAttribute("reviews"));
        assertEquals(4.7F, model.getAttribute("averageRating"));
    }
}
