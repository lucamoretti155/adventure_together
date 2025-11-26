package com.lucamoretti.adventure_together.model.booking.decorator;

import com.lucamoretti.adventure_together.model.booking.IBooking;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CancellationInsuranceTest {

    @Mock
    IBooking baseBooking;

    @Test
    void cancellationInsurance_addsFivePercent() {
        when(baseBooking.getInsuranceCost()).thenReturn(100.0);
        when(baseBooking.getTripCost()).thenReturn(1000.0);

        CancellationInsurance ci = new CancellationInsurance(baseBooking);

        // 100 + 50 (5% * 1000)
        assertEquals(150.0, ci.getInsuranceCost());
    }
}
