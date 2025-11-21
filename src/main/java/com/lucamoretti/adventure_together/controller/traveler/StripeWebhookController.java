package com.lucamoretti.adventure_together.controller.traveler;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.lucamoretti.adventure_together.service.booking.BookingFinalizeService;
import com.stripe.exception.SignatureVerificationException;
import com.stripe.model.Event;
import com.stripe.model.EventDataObjectDeserializer;
import com.stripe.model.PaymentIntent;
import com.stripe.net.Webhook;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import com.fasterxml.jackson.core.type.TypeReference;
import java.util.Map;


@RestController
@RequiredArgsConstructor
public class StripeWebhookController {

    private final BookingFinalizeService bookingFinalizeService;

    @Value("${stripe.webhook-secret}")
    private String webhookSecret;

    @PostMapping("/stripe/webhook")
    public ResponseEntity<String> handleStripeWebhook(
            @RequestBody String payload,
            @RequestHeader("Stripe-Signature") String sigHeader) {

        Event event;
        try {
            event = Webhook.constructEvent(payload, sigHeader, webhookSecret);
        } catch (SignatureVerificationException e) {
            return ResponseEntity.badRequest().body("Invalid signature");
        }

        // PAYMENT INTENT SUCCEEDED
        if ("payment_intent.succeeded".equals(event.getType())) {

            System.out.println(">>> MATCHED payment_intent.succeeded !!!");

            EventDataObjectDeserializer deserializer = event.getDataObjectDeserializer();

            // Se è deserializzabile bene, usiamo quello
            if (deserializer.getObject().isPresent()) {

                PaymentIntent intent = (PaymentIntent) deserializer.getObject().get();
                System.out.println(">>> DESERIALIZED PaymentIntent ID = " + intent.getId());
                bookingFinalizeService.finalizeBooking(intent);
                return ResponseEntity.ok("Received");
            }

            // Altrimenti facciamo parsing manuale
            System.out.println(">>> DESERIALIZER FAILED — USING RAW JSON");

            String rawJson = deserializer.getRawJson();

            try {
                ObjectMapper mapper = new ObjectMapper();
                JsonNode node = mapper.readTree(rawJson);

                PaymentIntent intent = new PaymentIntent();
                intent.setId(node.get("id").asText());
                intent.setPaymentMethod(node.get("payment_method").asText(null));

                // metadata
                if (node.has("metadata")) {
                    Map<String, String> metadata = mapper.convertValue(
                            node.get("metadata"),
                            new TypeReference<Map<String, String>>() {});
                    intent.setMetadata(metadata);
                }

                System.out.println(">>> MANUAL PARSE PaymentIntent ID = " + intent.getId());
                bookingFinalizeService.finalizeBooking(intent);

            } catch (Exception e) {
                System.out.println("❌ ERRORE NEL PARSE MANUALE");
                e.printStackTrace();
            }

            return ResponseEntity.ok("Received");
        }

        return ResponseEntity.ok("Received");
    }
}
