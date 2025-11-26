package com.lucamoretti.adventure_together.controller.booking;

import com.lucamoretti.adventure_together.dto.payment.PaymentIntentDTO;
import com.lucamoretti.adventure_together.dto.trip.TripDTO;
import com.lucamoretti.adventure_together.dto.trip.TripItineraryDTO;
import com.lucamoretti.adventure_together.dto.booking.BookingDTO;
import com.lucamoretti.adventure_together.dto.details.DepartureAirportDTO;
import com.lucamoretti.adventure_together.dto.participant.ParticipantDTO;
import com.lucamoretti.adventure_together.service.details.DepartureAirportService;
import com.lucamoretti.adventure_together.service.booking.BookingPreparationService;
import com.lucamoretti.adventure_together.service.trip.TripItineraryService;
import com.lucamoretti.adventure_together.service.trip.TripService;
import com.lucamoretti.adventure_together.service.user.UserService;
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
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BookingControllerTest {

    @Mock private TripService tripService;
    @Mock private BookingPreparationService bookingPreparationService;
    @Mock private DepartureAirportService airportService;
    @Mock private TripItineraryService tripItineraryService;
    @Mock private UserService userService;

    @InjectMocks
    private BookingController controller;

    private TripDTO trip;
    private TripItineraryDTO itin;
    private DepartureAirportDTO airport;

    @BeforeEach
    void setup() {
        trip = TripDTO.builder()
                .id(11L)
                .tripItineraryId(3L)
                .tripIndividualCost(500.0)
                .dateDeparture(LocalDate.now())
                .dateReturn(LocalDate.now())
                .dateStartBookings(LocalDate.now())
                .dateEndBookings(LocalDate.now())
                .build();

        itin = TripItineraryDTO.builder()
                .id(3L)
                .title("Itinerary Test")
                .departureAirportIds(Set.of(5L))
                .build();

        airport = DepartureAirportDTO.builder()
                .id(5L)
                .code("MXP")
                .name("Malpensa")
                .city("Milano")
                .build();
    }

    /* -------------------------------------------------------
     *  GET /bookings/new/{tripId}
     * ------------------------------------------------------- */

    @Test
    void showBookingForm_ok() {
        Model model = new ExtendedModelMap();

        when(tripService.getById(11L)).thenReturn(trip);
        when(userService.getCurrentUserId()).thenReturn(999L);
        when(tripItineraryService.getById(3L)).thenReturn(itin);
        when(airportService.getDepartureAirportsBySetOfIds(Set.of(5L)))
                .thenReturn(List.of(airport));

        String view = controller.showBookingForm(11L, model);

        assertEquals("booking/booking-form", view);
        assertEquals(trip, model.getAttribute("trip"));
        assertTrue(model.containsAttribute("bookingDTO"));
        assertEquals(List.of(airport), model.getAttribute("airports"));

        BookingDTO dto = (BookingDTO) model.getAttribute("bookingDTO");
        assertEquals(11L, dto.getTripId());
        assertEquals(999L, dto.getTravelerId());
        assertEquals(1, dto.getParticipants().size());
    }

    /* -------------------------------------------------------
     *  POST /bookings/checkout – invalid DTO
     * ------------------------------------------------------- */

    @Test
    void startCheckout_invalidForm_redirectsBack() {
        BookingDTO dto = BookingDTO.builder()
                .tripId(11L)
                .travelerId(999L)
                .participants(List.of())
                .build();

        BindingResult br = mock(BindingResult.class);
        when(br.hasErrors()).thenReturn(true);

        RedirectAttributes redirect = new RedirectAttributesModelMap();

        String view = controller.startCheckout(dto, br, new ExtendedModelMap(), redirect);

        assertEquals("redirect:/bookings/new/11", view);
        assertTrue(redirect.getFlashAttributes().containsKey("bookingDTO"));
        assertTrue(redirect.getFlashAttributes()
                .containsKey("org.springframework.validation.BindingResult.bookingDTO"));
    }

    /* -------------------------------------------------------
     *  POST /bookings/checkout – success
     * ------------------------------------------------------- */

    @Test
    void startCheckout_ok() {
        BookingDTO dto = BookingDTO.builder()
                .tripId(11L)
                .travelerId(999L)
                .participants(List.of(new ParticipantDTO()))
                .build();

        BindingResult br = mock(BindingResult.class);
        when(br.hasErrors()).thenReturn(false);

        PaymentIntentDTO intent = PaymentIntentDTO.builder()
                .clientSecret("secret_123")
                .total(575.0) // 500 base + 75 insurance
                .build();

        when(bookingPreparationService.startBookingAndPayment(dto)).thenReturn(intent);
        when(tripService.getById(11L)).thenReturn(trip);

        Model model = new ExtendedModelMap();

        String view = controller.startCheckout(dto, br, model, new RedirectAttributesModelMap());

        assertEquals("booking/booking-checkout", view);
        assertEquals("secret_123", model.getAttribute("clientSecret"));
        assertEquals(575.0, model.getAttribute("total"));
        assertEquals(500.0, model.getAttribute("base"));
        assertEquals(75.0, model.getAttribute("insurance"));
        assertEquals(1, model.getAttribute("numParticipant"));

        verify(bookingPreparationService).startBookingAndPayment(dto);
    }

    /* -------------------------------------------------------
     *  GET /bookings/success
     * ------------------------------------------------------- */

    @Test
    void bookingSuccess_returnsView() {
        String view = controller.bookingSuccess();
        assertEquals("booking/booking-success", view);
    }
}

