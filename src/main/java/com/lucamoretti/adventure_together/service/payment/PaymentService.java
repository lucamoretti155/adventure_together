package com.lucamoretti.adventure_together.service.payment;

import com.lucamoretti.adventure_together.dto.payment.PaymentDTO;
import com.lucamoretti.adventure_together.dto.payment.PaymentIntentDTO;
import com.lucamoretti.adventure_together.model.booking.Booking;

/*
    Interfaccia per il servizio di visualizzazione dei pagamenti.
 */

public interface PaymentService {

    PaymentDTO getPaymentById(Long paymentId);
}

