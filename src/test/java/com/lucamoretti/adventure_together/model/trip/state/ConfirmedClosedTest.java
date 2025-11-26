package com.lucamoretti.adventure_together.model.trip.state;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

class ConfirmedClosedTest extends TripStateBaseTest {

    private ConfirmedClosed state;

    @BeforeEach
    void setup() {
        state = new ConfirmedClosed();
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
}
