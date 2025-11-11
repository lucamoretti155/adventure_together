package com.lucamoretti.adventure_together.model.trip;

import com.lucamoretti.adventure_together.model.booking.Booking;
import com.lucamoretti.adventure_together.model.participant.Participant;
import com.lucamoretti.adventure_together.model.trip.state.ConfirmedOpen;
import com.lucamoretti.adventure_together.model.trip.state.ToBeConfirmed;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class TripTest {

    @Test
    void open_setsStateToToBeConfirmed_and_setsTemplate() {
        Trip trip = new Trip();
        assertNull(trip.getState());

        trip.open();

        assertNotNull(trip.getState());
        assertTrue(trip.getState() instanceof ToBeConfirmed);
        assertEquals("/mail/to-be-confirmed", trip.getTemplateMailPath());
    }

    @Test
    void open_whenAlreadyOpened_throwsIllegalStateException() {
        Trip trip = new Trip();
        trip.open();
        assertThrows(IllegalStateException.class, trip::open);
    }

    @Test
    void getCurrentParticipantsCount_returnsZero_whenNoBookings() {
        Trip trip = new Trip();
        assertEquals(0, trip.getCurrentParticipantsCount());
    }

}

