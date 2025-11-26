package com.lucamoretti.adventure_together.controller.planner;

import com.lucamoretti.adventure_together.dto.user.UserDTO;
import com.lucamoretti.adventure_together.dto.trip.TripDTO;
import com.lucamoretti.adventure_together.dto.trip.TripItineraryDTO;
import com.lucamoretti.adventure_together.service.details.CategoryService;
import com.lucamoretti.adventure_together.service.details.CountryService;
import com.lucamoretti.adventure_together.service.details.DepartureAirportService;
import com.lucamoretti.adventure_together.service.review.ReviewService;
import com.lucamoretti.adventure_together.service.trip.TripItineraryService;
import com.lucamoretti.adventure_together.service.trip.TripService;
import com.lucamoretti.adventure_together.service.user.UserService;
import com.lucamoretti.adventure_together.util.exception.*;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import org.springframework.security.core.Authentication;
import org.springframework.ui.ConcurrentModel;
import org.springframework.ui.Model;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.mvc.support.RedirectAttributesModelMap;
import org.springframework.validation.BindingResult;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PlannerControllerTest {

    @Mock private TripItineraryService itineraryService;
    @Mock private TripService tripService;
    @Mock private UserService userService;
    @Mock private CountryService countryService;
    @Mock private CategoryService categoryService;
    @Mock private DepartureAirportService departureAirportService;
    @Mock private ReviewService reviewService;
    @Mock private BindingResult bindingResult;
    @Mock private Authentication auth;

    @InjectMocks
    private PlannerController controller;

    /* -------------------------------------------------------
     * DASHBOARD
     * ------------------------------------------------------- */
    @Test
    void dashboard_returnsCorrectView() {
        assertEquals("planner/dashboard", controller.showAdminDashboard());
    }

    /* -------------------------------------------------------
     * CREATE TRIP ITINERARY (GET)
     * ------------------------------------------------------- */
    @Test
    void showCreateTripItineraryForm_loadsData() {
        Model model = new ConcurrentModel();

        TripItineraryDTO dto = new TripItineraryDTO();
        Long plannerId = 55L;

        when(auth.getName()).thenReturn("test@mail.com");
        when(userService.getByEmail("test@mail.com"))
                .thenReturn(Optional.of(
                UserDTO.builder()
                        .id(plannerId)
                        .firstName("Test")
                        .lastName("P")
                        .email("test@mail.com")
                        .active(true)
                        .role("ROLE_PLANNER")
                        .build()
        ));        when(countryService.getAllCountries()).thenReturn(List.of());
        when(categoryService.getAllCategories()).thenReturn(List.of());
        when(departureAirportService.getAllDepartureAirports()).thenReturn(List.of());

        String view = controller.showCreateTripItineraryForm(model, auth);

        assertEquals("planner/create-trip-itinerary", view);
        assertTrue(model.containsAttribute("tripItineraryDTO"));
        assertEquals(plannerId, ((TripItineraryDTO) model.getAttribute("tripItineraryDTO")).getPlannerId());
    }

    /* -------------------------------------------------------
     * CREATE TRIP ITINERARY (POST)
     * ------------------------------------------------------- */
    @Test
    void createTripItinerary_validationErrors() {
        when(bindingResult.hasErrors()).thenReturn(true);

        RedirectAttributes attrs = new RedirectAttributesModelMap();
        TripItineraryDTO dto = new TripItineraryDTO();

        String result = controller.createTripItinerary(dto, bindingResult, attrs);

        assertEquals("redirect:/planner/create-trip-itinerary", result);
        assertTrue(attrs.getFlashAttributes().containsKey("tripItineraryDTO"));
    }

    @Test
    void createTripItinerary_success() {
        when(bindingResult.hasErrors()).thenReturn(false);

        TripItineraryDTO dto = new TripItineraryDTO();
        RedirectAttributes attrs = new RedirectAttributesModelMap();

        String result = controller.createTripItinerary(dto, bindingResult, attrs);

        verify(itineraryService).createItinerary(dto);
        assertEquals("redirect:/planner/trip-itinerary-created-list", result);
        assertTrue(attrs.getFlashAttributes().containsKey("successMessage"));
    }

    @Test
    void createTripItinerary_exceptionHandled() {
        when(bindingResult.hasErrors()).thenReturn(false);
        doThrow(new DuplicateResourceException("ERR"))
                .when(itineraryService).createItinerary(any());

        RedirectAttributes attrs = new RedirectAttributesModelMap();
        TripItineraryDTO dto = new TripItineraryDTO();

        String result = controller.createTripItinerary(dto, bindingResult, attrs);

        assertEquals("redirect:/planner/create-trip-itinerary", result);
        assertTrue(attrs.getFlashAttributes().containsKey("errorMessage"));
    }

    /* -------------------------------------------------------
     * CREATE TRIP (GET)
     * ------------------------------------------------------- */
    @Test
    void showCreateTripForm_loadsData() {
        Model model = new ConcurrentModel();

        Long plannerId = 33L;
        when(userService.getCurrentUserId()).thenReturn(plannerId);
        when(itineraryService.getAll()).thenReturn(List.of());

        String view = controller.showCreateTripForm(model, auth);

        assertEquals("planner/create-trip", view);
        assertTrue(model.containsAttribute("tripItineraries"));
        assertEquals(plannerId, ((TripDTO) model.getAttribute("tripDTO")).getPlannerId());
    }

    /* -------------------------------------------------------
     * CREATE TRIP (POST)
     * ------------------------------------------------------- */
    @Test
    void createTrip_validationErrors() {
        when(bindingResult.hasErrors()).thenReturn(true);
        TripDTO dto = new TripDTO();
        RedirectAttributes attrs = new RedirectAttributesModelMap();

        String result = controller.createTrip(dto, bindingResult, attrs);

        assertEquals("redirect:/planner/create-trip", result);
        assertTrue(attrs.getFlashAttributes().containsKey("tripDTO"));
    }

    @Test
    void createTrip_success() {
        when(bindingResult.hasErrors()).thenReturn(false);
        TripDTO dto = new TripDTO();
        RedirectAttributes attrs = new RedirectAttributesModelMap();

        String result = controller.createTrip(dto, bindingResult, attrs);

        verify(tripService).createTrip(dto);
        assertEquals("redirect:/planner/trip-created-list", result);
    }

    @Test
    void createTrip_exceptionHandled() {
        when(bindingResult.hasErrors()).thenReturn(false);
        doThrow(new DataIntegrityException("ERR"))
                .when(tripService).createTrip(any());

        TripDTO dto = new TripDTO();
        RedirectAttributes attrs = new RedirectAttributesModelMap();

        String result = controller.createTrip(dto, bindingResult, attrs);

        assertEquals("redirect:/planner/create-trip", result);
        assertTrue(attrs.getFlashAttributes().containsKey("errorMessage"));
    }

    /* -------------------------------------------------------
     * LIST CREATED ITINERARIES
     * ------------------------------------------------------- */
    @Test
    void showTripItineraryCreatedList_loadsData() {
        Model model = new ConcurrentModel();
        when(userService.getCurrentUserId()).thenReturn(20L);
        when(itineraryService.getByPlannerId(20L)).thenReturn(List.of());

        String view = controller.showTripItineraryCreatedList(model);

        assertEquals("planner/all-trip-itineraries", view);
        assertTrue(model.containsAttribute("tripItineraries"));
    }

    /* -------------------------------------------------------
     * LIST CREATED TRIPS
     * ------------------------------------------------------- */
    @Test
    void showTripCreatedList_success() {
        Model model = new ConcurrentModel();
        when(userService.getCurrentUserId()).thenReturn(20L);
        when(tripService.getTripsByPlannerBetweenDates(eq(20L), any(), any()))
                .thenReturn(List.of());

        String view = controller.showTripCreatedList(null, null, model);

        assertEquals("planner/all-trips", view);
        assertTrue(model.containsAttribute("trips"));
    }

    @Test
    void showTripCreatedList_exceptionHandled() {
        Model model = new ConcurrentModel();
        when(userService.getCurrentUserId()).thenReturn(20L);
        doThrow(new DataIntegrityException("ERR"))
                .when(tripService).getTripsByPlannerBetweenDates(any(), any(), any());

        String view = controller.showTripCreatedList(null, null, model);

        assertEquals("planner/all-trips", view);
        assertTrue(model.containsAttribute("errorMessage"));
    }

    /* -------------------------------------------------------
     * UPDATE TRIP ITINERARY
     * ------------------------------------------------------- */
    @Test
    void showUpdateTripItineraryForm_loadsData() {
        Model model = new ConcurrentModel();
        TripItineraryDTO dto = new TripItineraryDTO();
        when(itineraryService.getById(10L)).thenReturn(dto);

        when(countryService.getAllCountries()).thenReturn(List.of());
        when(categoryService.getAllCategories()).thenReturn(List.of());
        when(departureAirportService.getAllDepartureAirports()).thenReturn(List.of());

        String view = controller.showUpdateTripItineraryForm(10L, model);

        assertEquals("planner/update-trip-itinerary", view);
        assertTrue(model.containsAttribute("tripItineraryDTO"));
    }

    @Test
    void updateTripItinerary_validationErrors() {
        when(bindingResult.hasErrors()).thenReturn(true);
        TripItineraryDTO dto = new TripItineraryDTO();
        RedirectAttributes attrs = new RedirectAttributesModelMap();

        String result = controller.updateTripItinerary(10L, dto, bindingResult, attrs);

        assertEquals("redirect:/planner/update-trip-itinerary/10", result);
    }

    @Test
    void updateTripItinerary_success() {
        when(bindingResult.hasErrors()).thenReturn(false);
        TripItineraryDTO dto = new TripItineraryDTO();
        RedirectAttributes attrs = new RedirectAttributesModelMap();

        String result = controller.updateTripItinerary(10L, dto, bindingResult, attrs);

        verify(itineraryService).updateItinerary(10L, dto);
        assertEquals("redirect:/planner/trip-itinerary-created-list", result);
    }

    @Test
    void updateTripItinerary_exceptionHandled() {
        when(bindingResult.hasErrors()).thenReturn(false);
        doThrow(new DuplicateResourceException("ERR"))
                .when(itineraryService).updateItinerary(anyLong(), any());

        TripItineraryDTO dto = new TripItineraryDTO();
        RedirectAttributes attrs = new RedirectAttributesModelMap();

        String result = controller.updateTripItinerary(10L, dto, bindingResult, attrs);

        assertTrue(attrs.getFlashAttributes().containsKey("errorMessage"));
        assertEquals("redirect:/planner/update-trip-itinerary/10", result);
    }

    /* -------------------------------------------------------
     * LIST ALL ITINERARIES
     * ------------------------------------------------------- */
    @Test
    void showAllTripItineraries_loadsList() {
        Model model = new ConcurrentModel();
        when(itineraryService.getAll()).thenReturn(List.of());

        String view = controller.showAllTripItineraries(model);

        assertEquals("planner/all-trip-itineraries", view);
        assertTrue(model.containsAttribute("tripItineraries"));
    }

    /* -------------------------------------------------------
     * LIST ALL TRIPS
     * ------------------------------------------------------- */
    @Test
    void showAllTrips_success() {
        Model model = new ConcurrentModel();
        when(tripService.getTripsBetweenDates(any(), any())).thenReturn(List.of());

        String view = controller.showAllTrips(null, null, model);

        assertEquals("planner/all-trips", view);
        assertTrue(model.containsAttribute("trips"));
    }

    @Test
    void showAllTrips_exceptionHandled() {
        Model model = new ConcurrentModel();
        doThrow(new DataIntegrityException("ERR"))
                .when(tripService).getTripsBetweenDates(any(), any());

        String view = controller.showAllTrips(null, null, model);

        assertEquals("planner/all-trips", view);
        assertTrue(model.containsAttribute("errorMessage"));
    }

    /* -------------------------------------------------------
     * TRIP DETAIL
     * ------------------------------------------------------- */
    @Test
    void showTripDetail_loadsAllData() {
        Model model = new ConcurrentModel();

        TripDTO tripDTO = new TripDTO();
        tripDTO.setTripItineraryId(77L);

        TripItineraryDTO itineraryDTO = new TripItineraryDTO();
        itineraryDTO.setDepartureAirportIds(Set.of(1L));

        when(tripService.getById(10L)).thenReturn(tripDTO);
        when(itineraryService.getById(77L)).thenReturn(itineraryDTO);
        when(departureAirportService.getDepartureAirportsBySetOfIds(any()))
                .thenReturn(List.of());
        when(tripService.getParticipantsByTripId(10L)).thenReturn(List.of());
        when(reviewService.getTripReviews(10L)).thenReturn(List.of());

        String view = controller.showTripDetail(10L, model);

        assertEquals("planner/trip-detail", view);
        assertTrue(model.containsAttribute("trip"));
        assertTrue(model.containsAttribute("airports"));
        assertTrue(model.containsAttribute("participants"));
        assertTrue(model.containsAttribute("reviews"));
    }
}
