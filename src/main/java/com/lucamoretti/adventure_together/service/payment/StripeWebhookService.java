package com.lucamoretti.adventure_together.service.payment;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.lucamoretti.adventure_together.service.booking.BookingFinalizeService;
import com.stripe.model.Event;
import com.stripe.model.EventDataObjectDeserializer;
import com.stripe.model.PaymentIntent;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.Map;

/*
    Servizio per la gestione centralizzata degli eventi webhook di Stripe.
    Processa i vari tipi di eventi e delega le azioni appropriate ai servizi corrispondenti.
*/

@Service
@RequiredArgsConstructor
public class StripeWebhookService {

    private final BookingFinalizeService bookingFinalizeService;
    private final ObjectMapper objectMapper = new ObjectMapper();

    // Metodo principale per processare gli eventi webhook di Stripe
    public void processEvent(Event event) {
        switch (event.getType()) {
            case "payment_intent.succeeded" -> handlePaymentIntentSucceeded(event);
            case "payment_intent.payment_failed" -> handlePaymentIntentFailed(event);
        }
    }
    // Gestione del successo del PaymentIntent
    // finalizza il booking associato al pagamento che viene salvato nel DB
    private void handlePaymentIntentSucceeded(Event event) {

        EventDataObjectDeserializer deserializer = event.getDataObjectDeserializer();

        PaymentIntent intent;

        if (deserializer.getObject().isPresent()) {
            // Caso normale: Stripe lo deserializza correttamente
            intent = (PaymentIntent) deserializer.getObject().get();
        } else {
            // Caso raro: deve leggere il raw JSON
            intent = parseRawPaymentIntent(deserializer.getRawJson());
        }

        // Delego la finalizzazione del booking
        bookingFinalizeService.finalizeBooking(intent);
    }

    // Parsing manuale del PaymentIntent dal JSON raw
    private PaymentIntent parseRawPaymentIntent(String rawJson) {

        try {
            JsonNode node = objectMapper.readTree(rawJson);

            PaymentIntent intent = new PaymentIntent();
            intent.setId(node.get("id").asText());
            intent.setPaymentMethod(node.get("payment_method").asText(null));

            if (node.has("metadata")) {
                Map<String, String> metadata = objectMapper.convertValue(
                        node.get("metadata"),
                        new TypeReference<Map<String, String>>() {});
                intent.setMetadata(metadata);
            }

            return intent;

        } catch (Exception e) {
            throw new RuntimeException("Failed to manually parse PaymentIntent", e);
        }
    }

    // Gestione del fallimento del PaymentIntent
    // non salva il booking, ma potrebbe loggare o notificare
    private void handlePaymentIntentFailed(Event event) {

        EventDataObjectDeserializer deserializer = event.getDataObjectDeserializer();
        PaymentIntent intent;

        if (deserializer.getObject().isPresent()) {
            intent = (PaymentIntent) deserializer.getObject().get();
        } else {
            intent = parseRawPaymentIntent(deserializer.getRawJson());
        }

        System.out.println("❌ PAYMENT INTENT FAILED: " + intent.getId());

    }
}


