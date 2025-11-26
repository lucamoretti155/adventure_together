package com.lucamoretti.adventure_together.model.trip.state;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

class CancelledTest extends TripStateBaseTest {

    private Cancelled state;

    @BeforeEach
    void setupState() {
        state = new Cancelled();
    }

    @Test
    void handle_doesNothing() {
        state.handle(trip);
        verify(trip, never()).setState(any());
        verify(trip, never()).notifyAllListeners(any());
    }

    @Test
    void cancel_doesNothing() {
        state.cancel(trip);
        verify(trip, never()).setState(any());
        verify(trip, never()).notifyAllListeners(any());
    }
}

