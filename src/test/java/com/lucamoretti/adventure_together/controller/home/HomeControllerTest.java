package com.lucamoretti.adventure_together.controller.home;

import com.lucamoretti.adventure_together.dto.trip.TripDTO;
import com.lucamoretti.adventure_together.dto.trip.TripItineraryDTO;
import com.lucamoretti.adventure_together.util.exception.ResourceNotFoundException;
import com.lucamoretti.adventure_together.service.trip.TripService;
import com.lucamoretti.adventure_together.service.trip.TripItineraryService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import org.springframework.ui.ExtendedModelMap;
import org.springframework.ui.Model;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class HomeControllerTest {

    @Mock private TripService tripService;
    @Mock private TripItineraryService tripItineraryService;
    @Mock private RedirectAttributes redirectAttributes;

    @InjectMocks
    private HomeController controller;

    private TripDTO trip;
    private TripItineraryDTO itinerary;

    @BeforeEach
    void setup() {
        trip = TripDTO.builder()
                .id(10L)
                .tripItineraryId(5L)
                .build();

        itinerary = TripItineraryDTO.builder()
                .id(5L)
                .title("Itinerario Test")
                .build();
    }

    /* -------------------------------------------------------
     * GET /
     * ------------------------------------------------------- */
    @Test
    void redirectHome_ok() {
        String view = controller.redirectHome();
        assertEquals("redirect:/home", view);
    }

    /* -------------------------------------------------------
     * GET /home
     * ------------------------------------------------------- */
    @Test
    void home_ok() {
        Model model = new ExtendedModelMap();

        when(tripItineraryService.getAll()).thenReturn(List.of(itinerary));
        when(tripService.getUpcomingBookableTrips()).thenReturn(List.of(trip));

        String view = controller.home(model);

        assertEquals("home/index", view);
        assertEquals(List.of(itinerary), model.getAttribute("itineraries"));
        assertEquals(List.of(trip), model.getAttribute("trips"));
        assertEquals(true, model.getAttribute("hasTrips"));
    }

    @Test
    void home_noTrips_setsHasTripsFalse() {
        Model model = new ExtendedModelMap();

        when(tripItineraryService.getAll()).thenReturn(List.of(itinerary));
        when(tripService.getUpcomingBookableTrips()).thenReturn(List.of());

        String view = controller.home(model);

        assertEquals("home/index", view);
        assertEquals(false, model.getAttribute("hasTrips"));
    }

    /* -------------------------------------------------------
     * GET /search?title=
     * ------------------------------------------------------- */
    @Test
    void search_emptyTitle_redirectsHome() {
        String view = controller.search("   ", redirectAttributes);

        assertEquals("redirect:/home", view);
        verify(redirectAttributes).addFlashAttribute(
                "errorMessage", "Inserisci un itinerario da cercare."
        );
    }

    @Test
    void search_found_redirectsToTrip() {
        when(tripItineraryService.getByTitle("Itinerario Test"))
                .thenReturn(itinerary);

        String view = controller.search("Itinerario Test", redirectAttributes);

        assertEquals("redirect:/trips/trip-itinerary/5", view);
    }

    @Test
    void search_notFound_redirectsHome() {
        doThrow(new ResourceNotFoundException("Itinerario", "title", "X"))
                .when(tripItineraryService).getByTitle("X");

        String view = controller.search("X", redirectAttributes);

        assertEquals("redirect:/home", view);

        verify(redirectAttributes).addFlashAttribute(
                "errorMessage", "Itinerario cercato non presente"
        );
    }
}
