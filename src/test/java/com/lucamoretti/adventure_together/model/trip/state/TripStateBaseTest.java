package com.lucamoretti.adventure_together.model.trip.state;

import com.lucamoretti.adventure_together.model.trip.Trip;

import org.junit.jupiter.api.BeforeEach;


import static org.mockito.Mockito.*;

class TripStateBaseTest {

    protected Trip trip;

    @BeforeEach
    void baseSetup() {
        trip = mock(Trip.class);
    }
}
