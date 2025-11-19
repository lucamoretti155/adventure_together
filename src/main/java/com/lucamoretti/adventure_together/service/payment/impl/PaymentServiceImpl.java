package com.lucamoretti.adventure_together.service.payment.impl;

import com.lucamoretti.adventure_together.dto.payment.PaymentDTO;
import com.lucamoretti.adventure_together.dto.payment.PaymentIntentDTO;
import com.lucamoretti.adventure_together.model.booking.Booking;
import com.lucamoretti.adventure_together.model.payment.Payment;
import com.lucamoretti.adventure_together.repository.payment.PaymentRepository;
import com.lucamoretti.adventure_together.service.payment.PaymentService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/*
    Implementazione del servizio di gestione dei pagamenti.
    Si occupa di avviare e confermare i pagamenti utilizzando Stripe come gateway di pagamento.
    Gestisce la persistenza delle informazioni di pagamento nel database.
 */

@Service
@RequiredArgsConstructor
public class PaymentServiceImpl implements PaymentService {

    private final StripeClient stripeClient;
    private final PaymentRepository paymentRepository;

    @Override
    public PaymentIntentDTO startPayment(Booking booking) {
        return stripeClient.createPaymentIntent(booking.getTotalCost(), "eur");
    }

    @Override
    @Transactional
    public PaymentDTO confirmPayment(String paymentIntentId, Booking booking, double insuranceCost) {
        stripeClient.confirmPayment(paymentIntentId);

        Payment payment = new Payment();
        payment.setBooking(booking);
        payment.setAmountPaid(booking.getTotalCost());
        payment.setAmountInsurance(insuranceCost);

        paymentRepository.save(payment);
        booking.setPayment(payment);

        return PaymentDTO.fromEntity(payment);
    }
}

