package com.lucamoretti.adventure_together.model.trip.state;

import com.lucamoretti.adventure_together.model.trip.TripItinerary;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.isA;
import static org.mockito.Mockito.*;

class ToBeConfirmedTest extends TripStateBaseTest {

    private ToBeConfirmed state;
    private TripItinerary itinerary;

    @BeforeEach
    void setup() {
        state = new ToBeConfirmed();

        itinerary = mock(TripItinerary.class);
        when(trip.getTripItinerary()).thenReturn(itinerary);
        when(itinerary.getMinParticipants()).thenReturn(3);
        when(itinerary.getMaxParticipants()).thenReturn(10);
        when(trip.getDateEndBookings()).thenReturn(LocalDate.now().minusDays(1));
    }

    @Test
    void handle_minParticipantsReached_movesToConfirmedOpen() {
        when(trip.getTripItinerary()).thenReturn(itinerary);
        when(itinerary.getMinParticipants()).thenReturn(3);
        when(trip.getCurrentParticipantsCount()).thenReturn(3);

        // End bookings in the future → NON scatta ExpiredClosed o ConfirmedClosed
        when(trip.getDateEndBookings()).thenReturn(LocalDate.now().plusDays(1));

        state.handle(trip);

        verify(trip).setState(isA(ConfirmedOpen.class));
    }

}
