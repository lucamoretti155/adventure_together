package com.lucamoretti.adventure_together.controller.traveler;

import com.lucamoretti.adventure_together.dto.booking.BookingDTO;
import com.lucamoretti.adventure_together.dto.details.DepartureAirportDTO;
import com.lucamoretti.adventure_together.dto.payment.PaymentDTO;
import com.lucamoretti.adventure_together.dto.review.ReviewDTO;
import com.lucamoretti.adventure_together.dto.trip.TripDTO;
import com.lucamoretti.adventure_together.dto.trip.TripItineraryDTO;
import com.lucamoretti.adventure_together.model.user.Traveler;
import com.lucamoretti.adventure_together.model.user.User;
import com.lucamoretti.adventure_together.service.booking.BookingService;
import com.lucamoretti.adventure_together.service.details.DepartureAirportService;
import com.lucamoretti.adventure_together.service.payment.PaymentService;
import com.lucamoretti.adventure_together.service.review.ReviewService;
import com.lucamoretti.adventure_together.service.trip.TripItineraryService;
import com.lucamoretti.adventure_together.service.trip.TripService;
import com.lucamoretti.adventure_together.service.user.UserService;
import com.lucamoretti.adventure_together.util.exception.ResourceNotFoundException;
import com.lucamoretti.adventure_together.util.exception.UnauthorizedActionException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.ui.ExtendedModelMap;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.mvc.support.RedirectAttributesModelMap;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TravelerControllerTest {

    @Mock
    private TripService tripService;
    @Mock
    private TripItineraryService tripItineraryService;
    @Mock
    private BookingService bookingService;
    @Mock
    private DepartureAirportService airportService;
    @Mock
    private PaymentService paymentService;
    @Mock
    private ReviewService reviewService;
    @Mock
    private UserService userService;

    @InjectMocks
    private TravelerController controller;

    private User traveler;
    private BookingDTO booking;
    private TripDTO trip;
    private TripItineraryDTO itin;
    private DepartureAirportDTO airport;
    private PaymentDTO payment;
    private ReviewDTO review;

    @BeforeEach
    void setup() {
        traveler = new Traveler();
        traveler.setId(999L);
        traveler.setEmail("traveler@test.com");

        booking = BookingDTO.builder()
                .id(44L)
                .travelerId(999L)
                .tripId(11L)
                .departureAirportId(5L)
                .bookingDate(LocalDate.now())
                .insuranceType("basic")
                .build();

        trip = TripDTO.builder()
                .id(11L)
                .tripItineraryId(3L)
                .tripIndividualCost(1000.0)
                .dateDeparture(LocalDate.now())
                .dateReturn(LocalDate.now())
                .dateStartBookings(LocalDate.now())
                .dateEndBookings(LocalDate.now())
                .build();

        itin = TripItineraryDTO.builder()
                .id(3L)
                .title("Itinerary Test")
                .build();

        airport = DepartureAirportDTO.builder()
                .id(5L)
                .code("MXP")
                .name("Milano Malpensa")
                .city("Milano")
                .build();

        payment = PaymentDTO.builder()
                .id(22L)
                .bookingId(44L)
                .status("succeeded")
                .amountPaid(1200.0)
                .clientSecret("stripeId")
                .build();

        review = ReviewDTO.builder()
                .id(1L)
                .tripId(11L)
                .travelerId(999L)
                .score(5)
                .textReview("Ottimo viaggio!")
                .build();

        // Collego il payment al booking, perché il controller fa booking.getPayment().getId()
        booking.setPayment(payment);
    }

    /* -------------------------------------------------------
     *  DASHBOARD
     * ------------------------------------------------------- */

    @Test
    void dashboard_returnsView() {
        String viewName = controller.showTravelerDashboard();
        assertEquals("traveler/dashboard", viewName);
    }

    /* -------------------------------------------------------
     *  BOOKINGS LIST
     * ------------------------------------------------------- */

    @Test
    void viewBookings_returnsBookingsList() {
        List<BookingDTO> bookings = List.of(booking);
        when(bookingService.getBookingsByTravelerId(999L)).thenReturn(bookings);

        Model model = new ExtendedModelMap();

        String viewName = controller.viewBookings(traveler, model);

        assertEquals("traveler/bookings-list", viewName);
        assertEquals(bookings, model.getAttribute("bookings"));
        verify(bookingService).getBookingsByTravelerId(999L);
    }

    /* -------------------------------------------------------
     *  BOOKING DETAILS
     * ------------------------------------------------------- */

    @Test
    void viewBookingDetails_ok() {
        when(bookingService.getBookingById(44L)).thenReturn(booking);
        when(tripService.getById(11L)).thenReturn(trip);
        when(tripItineraryService.getById(3L)).thenReturn(itin);
        when(airportService.getDepartureAirportById(5L)).thenReturn(airport);
        when(paymentService.getPaymentById(22L)).thenReturn(payment);
        when(reviewService.getReviewByTripIdAndTravelerId(11L, 999L))
                .thenReturn(review);

        Model model = new ExtendedModelMap();

        String viewName = controller.viewBookingDetails(44L, traveler, model);

        assertEquals("traveler/booking-details", viewName);
        assertEquals(booking, model.getAttribute("booking"));
        assertEquals(trip, model.getAttribute("trip"));
        assertEquals(itin, model.getAttribute("itinerary"));
        assertEquals(airport, model.getAttribute("airport"));
        assertEquals(payment, model.getAttribute("payment"));
        assertEquals(review, model.getAttribute("review"));

        verify(reviewService).getReviewByTripIdAndTravelerId(11L, 999L);
    }

    @Test
    void viewBookingDetails_wrongUser_throws404() {
        when(bookingService.getBookingById(44L)).thenReturn(booking);

        User other = new Traveler();
        other.setId(555L);

        Model model = new ExtendedModelMap();

        assertThrows(ResourceNotFoundException.class,
                () -> controller.viewBookingDetails(44L, other, model));

        verify(bookingService).getBookingById(44L);
    }

    /* -------------------------------------------------------
     *  SHOW REVIEW FORM
     * ------------------------------------------------------- */

    @Test
    void showReviewForm_ok() {
        when(bookingService.getBookingById(44L)).thenReturn(booking);
        when(userService.getCurrentUserId()).thenReturn(999L);
        when(tripService.getById(11L)).thenReturn(trip);

        Model model = new ExtendedModelMap();

        String viewName = controller.showReviewForm(44L, model);

        assertEquals("traveler/review-form", viewName);
        assertTrue(model.containsAttribute("booking"));
        assertTrue(model.containsAttribute("trip"));
        assertTrue(model.containsAttribute("reviewDTO"));

        ReviewDTO dtoInModel = (ReviewDTO) model.getAttribute("reviewDTO");
        assertEquals(11L, dtoInModel.getTripId());
        assertEquals(999L, dtoInModel.getTravelerId());
    }

    @Test
    void showReviewForm_wrongUser_throws403() {
        // booking appartenente a travelerId 999
        when(bookingService.getBookingById(44L)).thenReturn(booking);
        // utente corrente diverso
        when(userService.getCurrentUserId()).thenReturn(555L);

        Model model = new ExtendedModelMap();

        assertThrows(UnauthorizedActionException.class,
                () -> controller.showReviewForm(44L, model));
    }

    /* -------------------------------------------------------
     *  SUBMIT REVIEW
     * ------------------------------------------------------- */

    @Test
    void submitReview_invalidForm_redirectsBack() {
        when(bookingService.getBookingById(44L)).thenReturn(booking);
        when(userService.getCurrentUserId()).thenReturn(999L);

        ReviewDTO reviewDTO = ReviewDTO.builder()
                .tripId(11L)
                .travelerId(999L)
                .score(0)          // qualcosa di non valido
                .textReview("")
                .build();

        BindingResult bindingResult = mock(BindingResult.class);
        when(bindingResult.hasErrors()).thenReturn(true);

        RedirectAttributes redirectAttributes = new RedirectAttributesModelMap();

        String viewName = controller.submitReview(44L, reviewDTO, bindingResult, redirectAttributes);

        assertEquals("redirect:/traveler/review/44", viewName);
        assertTrue(redirectAttributes.getFlashAttributes().containsKey("reviewDTO"));
        assertTrue(redirectAttributes.getFlashAttributes()
                .containsKey("org.springframework.validation.BindingResult.reviewDTO"));
    }

    @Test
    void submitReview_ok() {
        when(bookingService.getBookingById(44L)).thenReturn(booking);
        when(userService.getCurrentUserId()).thenReturn(999L);

        ReviewDTO input = ReviewDTO.builder()
                .tripId(11L)
                .travelerId(999L)
                .score(5)
                .textReview("Perfetto")
                .build();

        BindingResult bindingResult = mock(BindingResult.class);
        when(bindingResult.hasErrors()).thenReturn(false);

        ReviewDTO saved = ReviewDTO.builder()
                .id(1L)
                .tripId(11L)
                .travelerId(999L)
                .score(5)
                .textReview("Perfetto")
                .build();

        when(reviewService.createReview(11L, 999L, input)).thenReturn(saved);

        RedirectAttributes redirectAttributes = new RedirectAttributesModelMap();

        String viewName = controller.submitReview(44L, input, bindingResult, redirectAttributes);

        assertEquals("redirect:/traveler/booking-details/44", viewName);
        assertEquals("Recensione inviata con successo!",
                redirectAttributes.getFlashAttributes().get("successMessage"));

        verify(reviewService).createReview(11L, 999L, input);
    }
}
