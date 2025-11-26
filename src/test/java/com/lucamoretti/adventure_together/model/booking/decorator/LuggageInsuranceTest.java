package com.lucamoretti.adventure_together.model.booking.decorator;

import com.lucamoretti.adventure_together.model.booking.IBooking;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class LuggageInsuranceTest {

    @Mock
    IBooking baseBooking;

    @Test
    void luggageInsurance_adds20PerPerson() {
        when(baseBooking.getInsuranceCost()).thenReturn(100.0);
        when(baseBooking.getNumParticipants()).thenReturn(3);

        LuggageInsurance li = new LuggageInsurance(baseBooking);

        // extra = 20 * 3 = 60 → totale = 160
        assertEquals(160.0, li.getInsuranceCost());
    }

    @Test
    void luggageInsurance_minOneParticipant() {
        when(baseBooking.getInsuranceCost()).thenReturn(100.0);
        when(baseBooking.getNumParticipants()).thenReturn(0);

        LuggageInsurance li = new LuggageInsurance(baseBooking);

        // almeno 1 → extra = 20
        assertEquals(120.0, li.getInsuranceCost());
    }
}
