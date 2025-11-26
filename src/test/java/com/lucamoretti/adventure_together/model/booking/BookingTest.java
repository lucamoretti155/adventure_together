package com.lucamoretti.adventure_together.model.booking;

import com.lucamoretti.adventure_together.model.details.DepartureAirport;
import com.lucamoretti.adventure_together.model.participant.Participant;
import com.lucamoretti.adventure_together.model.trip.Trip;
import com.lucamoretti.adventure_together.model.trip.TripItinerary;
import com.lucamoretti.adventure_together.model.user.Traveler;
import com.lucamoretti.adventure_together.service.mail.EmailService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(MockitoExtension.class)
class BookingTest {

    @Mock Trip trip;
    @Mock Traveler traveler;
    @Mock DepartureAirport departureAirport;

    @Mock EmailService emailService;

    private Booking booking;

    @BeforeEach
    void setup() {
        booking = new Booking();
        booking.setTrip(trip);
        booking.setTraveler(traveler);
        booking.setDepartureAirport(departureAirport);
        booking.setInsuranceType("basic");

        Booking.setEmailService(emailService);
    }

    // ----------------------------------------------------------
    // getNumParticipants()
    // ----------------------------------------------------------

    @Test
    void getNumParticipants_returnsCorrectSize() {
        Participant p1 = new Participant();
        Participant p2 = new Participant();

        booking.getParticipants().add(p1);
        booking.getParticipants().add(p2);

        assertEquals(2, booking.getNumParticipants());
    }

    @Test
    void getNumParticipants_handlesNullList() {
        booking.setParticipants(null);
        assertEquals(0, booking.getNumParticipants());
    }

    // ----------------------------------------------------------
    // getTripCost()
    // ----------------------------------------------------------

    @Test
    void getTripCost_calculatesCorrectly() {
        when(trip.getTripIndividualCost()).thenReturn(1000.0);

        Participant p1 = new Participant();
        Participant p2 = new Participant();
        booking.getParticipants().add(p1);
        booking.getParticipants().add(p2);

        // 1000 * 2 partecipanti
        assertEquals(2000.0, booking.getTripCost());
    }

    @Test
    void getTripCost_tripNull_throws() {
        booking.setTrip(null);

        assertThrows(IllegalStateException.class, () -> booking.getTripCost());
    }

    // ----------------------------------------------------------
    // getInsuranceCost()
    // ----------------------------------------------------------

    @Test
    void getInsuranceCost_isTenPercentOfTripCost() {
        when(trip.getTripIndividualCost()).thenReturn(1000.0);
        booking.getParticipants().add(new Participant());

        assertEquals(100.0, booking.getInsuranceCost());
    }

    // ----------------------------------------------------------
    // getTotalCost()
    // ----------------------------------------------------------

    @Test
    void getTotalCost_sumsTripCostAndInsurance() {
        when(trip.getTripIndividualCost()).thenReturn(1000.0);
        booking.getParticipants().add(new Participant());

        // 1000 + 100
        assertEquals(1100.0, booking.getTotalCost());
    }

    // ----------------------------------------------------------
    // update() → invia email
    // ----------------------------------------------------------

    @Test
    void update_sendsEmailCorrectly() {
        when(traveler.getEmail()).thenReturn("test@test.com");
        when(trip.getTripItinerary()).thenReturn(mock(TripItinerary.class));

        TripItinerary itin = trip.getTripItinerary();
        when(itin.getTitle()).thenReturn("Viaggio Test");

        booking.update("/mail/test", "http://localhost/home");

        verify(emailService).sendHtmlMessage(
                eq("test@test.com"),
                contains("Viaggio Test"),
                eq("/mail/test"),
                anyMap()
        );
    }
}
