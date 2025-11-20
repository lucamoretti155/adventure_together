package com.lucamoretti.adventure_together.service.payment.impl;

import com.lucamoretti.adventure_together.dto.payment.PaymentDTO;
import com.lucamoretti.adventure_together.dto.payment.PaymentIntentDTO;
import com.lucamoretti.adventure_together.model.booking.Booking;
import com.lucamoretti.adventure_together.model.payment.Payment;
import com.lucamoretti.adventure_together.repository.booking.BookingRepository;
import com.lucamoretti.adventure_together.repository.payment.PaymentRepository;
import com.lucamoretti.adventure_together.service.payment.PaymentService;
import com.lucamoretti.adventure_together.util.exception.ResourceNotFoundException;
import com.stripe.exception.StripeException;
import com.stripe.model.PaymentIntent;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

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
    private final BookingRepository bookingRepository;

    @Override
    public PaymentIntentDTO startPayment(Booking booking) {
        return stripeClient.createPaymentIntent(booking.getTotalCost(), "eur");
    }

    @Override
    @Transactional
    public PaymentDTO confirmPayment(String paymentIntentId, Long bookingId) {

       // recupero PaymentIntent da Stripe
        PaymentIntent intent;
        try {
            intent = PaymentIntent.retrieve(paymentIntentId);
        } catch (StripeException e) {
            throw new RuntimeException("Impossibile recuperare il payment intent", e);
        }

        // check stato
        if (!"succeeded".equals(intent.getStatus())) {
            throw new IllegalStateException("Il pagamento non è stato completato.");
        }

        // aggiorno pagamento esistente
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new ResourceNotFoundException("Booking", "id", bookingId));
        Payment payment = booking.getPayment();
        payment.setStatus("PAID");
        payment.setPaymentMethod(intent.getPaymentMethod());
        payment.setPaymentDate(LocalDate.now());

        paymentRepository.save(payment);
        booking.setPayment(payment);

        return PaymentDTO.fromEntity(payment);
    }
}

