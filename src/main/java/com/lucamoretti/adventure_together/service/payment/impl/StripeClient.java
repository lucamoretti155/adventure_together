package com.lucamoretti.adventure_together.service.payment.impl;

import com.lucamoretti.adventure_together.dto.payment.PaymentIntentDTO;
import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.PaymentIntent;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import java.util.HashMap;
import java.util.Map;

/*
    Client per interagire con l'API di Stripe per la gestione dei pagamenti.
    Si occupa di creare e confermare i Payment Intent su Stripe.
 */

@Service
@RequiredArgsConstructor
public class StripeClient {

    @Value("${STRIPE_SECRET_KEY}")
    private String stripeSecretKey;

    @PostConstruct
    public void init() {
        Stripe.apiKey = stripeSecretKey;
    }

    public PaymentIntentDTO createPaymentIntent(double amount, String currency) {
        try {
            Map<String, Object> params = new HashMap<>();
            params.put("amount", (long) (amount * 100));
            params.put("currency", currency);
            params.put("automatic_payment_methods", Map.of("enabled", true));

            PaymentIntent intent = PaymentIntent.create(params);
            return new PaymentIntentDTO(intent.getId(), intent.getClientSecret());
        } catch (StripeException e) {
            throw new RuntimeException("Failed to create payment intent", e);
        }
    }

    public void confirmPayment(String paymentIntentId) {
        try {
            PaymentIntent intent = PaymentIntent.retrieve(paymentIntentId);
            intent.confirm();
        } catch (StripeException e) {
            throw new RuntimeException("Failed to confirm payment", e);
        }
    }
}

