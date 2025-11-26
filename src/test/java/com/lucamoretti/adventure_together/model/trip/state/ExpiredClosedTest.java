package com.lucamoretti.adventure_together.model.trip.state;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

class ExpiredClosedTest extends TripStateBaseTest {

    private ExpiredClosed state;

    @BeforeEach
    void setupState() {
        state = new ExpiredClosed();
    }

    @Test
    void handle_doesNothing() {
        state.handle(trip);

        verify(trip, never()).setState(any());
    }

    @Test
    void cancel_doesNothing() {
        state.cancel(trip);

        verify(trip, never()).setState(any());
    }

    @Test
    void canAcceptBooking_false() {
        assertFalse(state.canAcceptBooking());
    }
}
