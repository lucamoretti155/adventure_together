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
    Implementazione del client per l'integrazione con l'API di Stripe.
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

    /*
     * Crea un PaymentIntent su Stripe includendo eventuali metadata
     */
    public PaymentIntentDTO createPaymentIntent(double amount,
                                                String currency,
                                                String metadataJson) {
        try {
            Map<String, Object> params = new HashMap<>();
            params.put("amount", (long) (amount * 100)); // amount in centesimi
            params.put("currency", currency);
            params.put("automatic_payment_methods", Map.of("enabled", true));

            // aggiunta metadata booking (serializzato JSON)
            Map<String, String> metadata = new HashMap<>();
            metadata.put("booking", metadataJson);
            params.put("metadata", metadata);

            PaymentIntent intent = PaymentIntent.create(params);

            return new PaymentIntentDTO(
                    intent.getId(),
                    intent.getClientSecret()
            );

        } catch (StripeException e) {
            throw new RuntimeException("Errore nella creazione del PaymentIntent Stripe", e);
        }
    }

    /*
     * Recupera un PaymentIntent esistente da Stripe
     */
    public PaymentIntent retrievePaymentIntent(String paymentIntentId) {
        try {
            return PaymentIntent.retrieve(paymentIntentId);
        } catch (StripeException e) {
            throw new RuntimeException("Impossibile recuperare PaymentIntent " + paymentIntentId, e);
        }
    }

    /*
     * Conferma manualmente un PaymentIntent (in rari casi)
     */
    public PaymentIntent confirmPaymentIntent(String paymentIntentId) {
        try {
            PaymentIntent intent = PaymentIntent.retrieve(paymentIntentId);
            return intent.confirm();
        } catch (StripeException e) {
            throw new RuntimeException("Errore nella conferma del pagamento", e);
        }
    }
}
