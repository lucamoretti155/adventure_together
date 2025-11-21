package com.lucamoretti.adventure_together.service.booking;

import com.lucamoretti.adventure_together.dto.booking.BookingDTO;
import com.lucamoretti.adventure_together.dto.payment.PaymentIntentDTO;

/*
 Interfaccia per il servizio di preparazione della prenotazione e del pagamento.
 */

public interface BookingPreparationService {
    PaymentIntentDTO startBookingAndPayment(BookingDTO bookingDto);
}