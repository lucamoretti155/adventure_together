package com.lucamoretti.adventure_together.model.trip;

import com.lucamoretti.adventure_together.model.booking.Booking;
import com.lucamoretti.adventure_together.model.trip.state.ToBeConfirmed;
import com.lucamoretti.adventure_together.model.trip.state.TripState;
import com.lucamoretti.adventure_together.model.user.Planner;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
@ExtendWith(MockitoExtension.class)
class TripTest {

    private Trip trip;
    private TripItinerary itinerary;
    private Planner planner;

    @BeforeEach
    void setup() {
        // oggetto reale, niente mock → niente stubbing inutile
        itinerary = new TripItinerary();
        itinerary.setMinParticipants(3);
        itinerary.setMaxParticipants(10);

        // anche il planner può essere un semplice POJO
        planner = new Planner();

        trip = new Trip();
        trip.setId(1L);
        trip.setDateStartBookings(LocalDate.now().minusDays(1));
        trip.setDateEndBookings(LocalDate.now().plusDays(5));
        trip.setDateDeparture(LocalDate.now().plusDays(10));
        trip.setDateReturn(LocalDate.now().plusDays(17));
        trip.setTripIndividualCost(1500);
        trip.setTripItinerary(itinerary);
        trip.setPlanner(planner);
    }

    // -------------------------------------------------------------------------
    // OPEN()
    // -------------------------------------------------------------------------

    @Test
    void open_setsInitialState() {
        trip.open();

        assertTrue(trip.getState() instanceof ToBeConfirmed);
    }

    @Test
    void open_whenAlreadyOpen_throws() {
        trip.open();

        assertThrows(IllegalStateException.class, () -> trip.open());
    }

    // -------------------------------------------------------------------------
    // HANDLE (delegato allo state)
    // -------------------------------------------------------------------------

    @Test
    void handle_callsStateHandle() {
        TripState state = mock(TripState.class);
        trip.setState(state);

        trip.handle();

        verify(state).handle(trip);
    }

    // -------------------------------------------------------------------------
    // CANCEL (delegato allo state)
    // -------------------------------------------------------------------------

    @Test
    void cancel_callsStateCancel() {
        TripState state = mock(TripState.class);
        trip.setState(state);

        trip.cancel();

        verify(state).cancel(trip);
    }

    // -------------------------------------------------------------------------
    // addBooking / removeBooking
    // -------------------------------------------------------------------------

    @Test
    void addBooking_setsRelationOnBothSides() {
        Booking b = mock(Booking.class);

        trip.addBooking(b);

        assertTrue(trip.getBookings().contains(b));
        verify(b).setTrip(trip);
    }

    @Test
    void removeBooking_clearsRelationOnBothSides() {
        Booking b = mock(Booking.class);
        trip.addBooking(b);

        trip.removeBooking(b);

        assertFalse(trip.getBookings().contains(b));
        verify(b).setTrip(null);
    }

    // -------------------------------------------------------------------------
    // getCurrentParticipantsCount
    // -------------------------------------------------------------------------

    @Test
    void getCurrentParticipantsCount_sumsParticipants() {
        Booking b1 = mock(Booking.class);
        Booking b2 = mock(Booking.class);

        when(b1.getNumParticipants()).thenReturn(2);
        when(b2.getNumParticipants()).thenReturn(3);

        trip.addBooking(b1);
        trip.addBooking(b2);

        assertEquals(5, trip.getCurrentParticipantsCount());
    }

    @Test
    void getCurrentParticipantsCount_ignoresNullBooking() {
        trip.getBookings().add(null);
        assertEquals(0, trip.getCurrentParticipantsCount());
    }

    @Test
    void getCurrentParticipantsCount_ignoresBookingErrors() {
        Booking b = mock(Booking.class);
        when(b.getNumParticipants()).thenThrow(new RuntimeException());
        trip.addBooking(b);

        assertEquals(0, trip.getCurrentParticipantsCount());
    }

    // -------------------------------------------------------------------------
    // hasAvailableSpots
    // -------------------------------------------------------------------------

    @Test
    void hasAvailableSpots_trueWhenSpaceAvailable() {
        Booking b = mock(Booking.class);
        when(b.getNumParticipants()).thenReturn(5);
        trip.addBooking(b);

        // maxParticipants = 10 impostato nel setup
        assertTrue(trip.hasAvailableSpots(3)); // 5/10 → 3 posti disponibili
    }

    @Test
    void hasAvailableSpots_falseWhenTooMany() {
        Booking b = mock(Booking.class);
        when(b.getNumParticipants()).thenReturn(8);
        trip.addBooking(b);

        assertFalse(trip.hasAvailableSpots(5)); // rimangono 2 posti
    }

    // -------------------------------------------------------------------------
    // notifyAllListeners
    // -------------------------------------------------------------------------

    @Test
    void notifyAllListeners_callsUpdateOnEachBooking() {
        Booking b1 = mock(Booking.class);
        Booking b2 = mock(Booking.class);

        trip.setBaseUrl("http://localhost:8080");

        trip.addBooking(b1);
        trip.addBooking(b2);

        trip.notifyAllListeners("/mail/template");

        verify(b1).update(eq("/mail/template"), eq("http://localhost:8080/home"));
        verify(b2).update(eq("/mail/template"), eq("http://localhost:8080/home"));
    }
}
