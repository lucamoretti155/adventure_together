package com.lucamoretti.adventure_together.controller.booking;

import com.lucamoretti.adventure_together.service.payment.StripeWebhookService;
import com.stripe.exception.SignatureVerificationException;
import com.stripe.model.Event;
import com.stripe.net.Webhook;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.beans.factory.annotation.Value;

/*
    Controller per la gestione dei webhook di Stripe.
    Riceve gli eventi webhook, verifica la firma e delega al servizio di gestione centralizzata.
 */

@RestController
@RequiredArgsConstructor
public class StripeWebhookController {

    private final StripeWebhookService stripeWebhookService;

    @Value("${stripe.webhook-secret}")
    private String webhookSecret;

    @PostMapping("/stripe/webhook")
    public ResponseEntity<String> handleStripeWebhook(
            @RequestBody String payload,
            @RequestHeader("Stripe-Signature") String sigHeader) {

        Event event;

        try {
            // Verifica firma Stripe
            event = Webhook.constructEvent(payload, sigHeader, webhookSecret);
        } catch (SignatureVerificationException e) {
            return ResponseEntity.badRequest().body("Invalid signature");
        }

        // Delego al service centralizzato che si occupa di processare l'evento (deserializzazione e finalizzazione)
        stripeWebhookService.processEvent(event);

        return ResponseEntity.ok("Received");
    }
}

