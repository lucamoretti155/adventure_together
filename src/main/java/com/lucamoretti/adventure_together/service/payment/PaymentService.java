package com.lucamoretti.adventure_together.service.payment;

import com.lucamoretti.adventure_together.dto.payment.PaymentDTO;
import com.lucamoretti.adventure_together.dto.payment.PaymentIntentDTO;
import com.lucamoretti.adventure_together.model.booking.Booking;

/*
    Interfaccia per il servizio di gestione dei pagamenti.
    Definisce i metodi per avviare e confermare i pagamenti.
 */

public interface PaymentService {

    PaymentIntentDTO startPayment(Booking booking);

    PaymentDTO confirmPayment(String paymentIntentId, Booking booking, double insuranceCost);
}

