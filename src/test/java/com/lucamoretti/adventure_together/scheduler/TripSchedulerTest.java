package com.lucamoretti.adventure_together.scheduler;

import com.lucamoretti.adventure_together.model.trip.Trip;
import com.lucamoretti.adventure_together.model.trip.state.TripState;
import com.lucamoretti.adventure_together.repository.trip.TripRepository;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TripSchedulerTest {

    @Mock
    private TripRepository tripRepository;

    @InjectMocks
    private TripScheduler scheduler;

    // Dummy TripState implementations
    static class StateA extends TripState {
        @Override
        public void handle(Trip trip) {
            // no-op
        }

        @Override
        public void cancel(Trip trip) {

        }
    }

    static class StateB extends TripState {
        @Override
        public void handle(Trip trip) {
            // no-op
        }

        @Override
        public void cancel(Trip trip) {

        }
    }

    // ------------------------------------------------------------
    // 1) Stato cambia → save() deve essere chiamato
    // ------------------------------------------------------------

    @Test
    void updateTripStates_stateChanges_tripIsSaved() {
        Trip trip = mock(Trip.class);

        TripState before = new StateA();
        TripState after  = new StateB();

        when(trip.getState()).thenReturn(before, after);
        when(tripRepository.findOpenForBooking()).thenReturn(List.of(trip));

        scheduler.updateTripStates();

        verify(trip).handle();
        verify(tripRepository).save(trip);
    }

    // ------------------------------------------------------------
    // 2) Stato NON cambia → save() NON deve essere chiamato
    // ------------------------------------------------------------

    @Test
    void updateTripStates_stateDoesNotChange_tripNotSaved() {
        Trip trip = mock(Trip.class);

        TripState same = new StateA();

        when(trip.getState()).thenReturn(same, same);
        when(tripRepository.findOpenForBooking()).thenReturn(List.of(trip));

        scheduler.updateTripStates();

        verify(trip).handle();
        verify(tripRepository, never()).save(any());
    }

    // ------------------------------------------------------------
    // 3) Eccezione in handle() → catturata, nessun errore nel test
    // ------------------------------------------------------------

    @Test
    void updateTripStates_errorDuringHandle_isCaught() {
        Trip trip = mock(Trip.class);

        when(tripRepository.findOpenForBooking()).thenReturn(List.of(trip));
        when(trip.getState()).thenReturn(new StateA());
        doThrow(new RuntimeException("boom")).when(trip).handle();

        assertDoesNotThrow(() -> scheduler.updateTripStates());
    }

    // ------------------------------------------------------------
    // 4) Nessun trip → nessun salvataggio
    // ------------------------------------------------------------

    @Test
    void updateTripStates_noTrips_noAction() {
        when(tripRepository.findOpenForBooking()).thenReturn(List.of());

        scheduler.updateTripStates();

        verify(tripRepository, never()).save(any());
    }
}
