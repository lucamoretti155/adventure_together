package com.lucamoretti.adventure_together.service.booking;

import com.stripe.model.PaymentIntent;

/*
 Interfaccia per il servizio di finalizzazione della prenotazione.
 */

public interface BookingFinalizeService {
    void finalizeBooking(PaymentIntent intent);
}
