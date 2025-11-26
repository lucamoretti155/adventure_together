package com.lucamoretti.adventure_together.service.payment;

import com.lucamoretti.adventure_together.service.booking.BookingFinalizeService;
import com.stripe.model.Event;
import com.stripe.model.EventDataObjectDeserializer;
import com.stripe.model.PaymentIntent;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class StripeWebhookServiceTest {

    @Mock
    private BookingFinalizeService bookingFinalizeService;

    @InjectMocks
    private StripeWebhookService service;

    // ----------------------------------------------------------------------
    //  UTILITIES PER MOCKARE DESERIALIZZATORE STRIPE
    // ----------------------------------------------------------------------

    private Event mockEvent(
            String type,
            boolean hasObject,
            PaymentIntent intent,
            String rawJson
    ) {

        Event event = Mockito.spy(new Event());
        event.setType(type);

        // Solo gli eventi gestiti usano il deserializer
        if (!type.equals("payment_intent.succeeded")
                && !type.equals("payment_intent.payment_failed")) {

            // Evento NON gestito → nessuno stubbing
            return event;
        }

        EventDataObjectDeserializer deserializer = mock(EventDataObjectDeserializer.class);

        if (hasObject) {
            // solo succeeded/failed
            when(deserializer.getObject()).thenReturn(Optional.of(intent));
        } else {
            when(deserializer.getObject()).thenReturn(Optional.empty());
            when(deserializer.getRawJson()).thenReturn(rawJson);
        }

        // mock diretto del metodo getter
        doReturn(deserializer).when(event).getDataObjectDeserializer();

        return event;
    }


    // ----------------------------------------------------------------------
    //  PROCESS EVENT
    // ----------------------------------------------------------------------

    @Test
    void processEvent_success_callsFinalizeBooking() {

        PaymentIntent intent = new PaymentIntent();
        intent.setId("pi_123");

        Event event = mockEvent("payment_intent.succeeded", true, intent, null);

        service.processEvent(event);

        verify(bookingFinalizeService).finalizeBooking(intent);
    }

    @Test
    void processEvent_failed_doesNotThrow() {

        PaymentIntent intent = new PaymentIntent();
        intent.setId("pi_failed");

        Event event = mockEvent("payment_intent.payment_failed", true, intent, null);

        assertDoesNotThrow(() -> service.processEvent(event));

        verifyNoInteractions(bookingFinalizeService);
    }

    @Test
    void processEvent_unknownType_doesNothing() {

        Event event = mockEvent("random_event", true, new PaymentIntent(), null);

        assertDoesNotThrow(() -> service.processEvent(event));

        verifyNoInteractions(bookingFinalizeService);
    }

    // ----------------------------------------------------------------------
    //  HANDLE SUCCESS - RAW JSON
    // ----------------------------------------------------------------------

    @Test
    void handlePaymentIntentSucceeded_rawJson_parsedAndDelegated() {

        String raw = """
            {
              "id": "pi_raw",
              "payment_method": "pm_123",
              "metadata": { "bookingId": "99" }
            }
        """;

        Event event = mockEvent("payment_intent.succeeded", false, null, raw);

        service.processEvent(event);

        // Verifichiamo che finalizeBooking venga chiamato con QUALSIASI PaymentIntent costruito
        verify(bookingFinalizeService).finalizeBooking(any(PaymentIntent.class));
    }

    // ----------------------------------------------------------------------
    //  PARSE RAW PAYMENT INTENT
    // ----------------------------------------------------------------------

    @Test
    void parseRawPaymentIntent_validJson_parsedCorrectly() {

        String raw = """
            {
              "id": "pi_test",
              "payment_method": "pm_999",
              "metadata": {
                "bookingId": "77"
              }
            }
        """;

        PaymentIntent intent = ReflectionTestUtils.invokeMethod(service, "parseRawPaymentIntent", raw);

        assertNotNull(intent);
        assertEquals("pi_test", intent.getId());
        assertEquals("pm_999", intent.getPaymentMethod());
        assertEquals("77", intent.getMetadata().get("bookingId"));
    }

    @Test
    void parseRawPaymentIntent_noMetadata_ok() {

        String raw = """
            {
              "id": "pi_test",
              "payment_method": "pm_999"
            }
        """;

        PaymentIntent intent = ReflectionTestUtils.invokeMethod(service, "parseRawPaymentIntent", raw);

        assertNotNull(intent);
        assertEquals("pi_test", intent.getId());
        assertEquals("pm_999", intent.getPaymentMethod());
        assertNull(intent.getMetadata());
    }

    @Test
    void parseRawPaymentIntent_invalidJson_throws() {

        String invalid = "{ not valid json }";

        assertThrows(RuntimeException.class, () ->
                ReflectionTestUtils.invokeMethod(service, "parseRawPaymentIntent", invalid)
        );
    }

    // ----------------------------------------------------------------------
    //  HANDLE FAILED
    // ----------------------------------------------------------------------

    @Test
    void handlePaymentIntentFailed_deserialized_noException() {

        PaymentIntent intent = new PaymentIntent();
        intent.setId("pi_fail");

        Event event = mockEvent("payment_intent.payment_failed", true, intent, null);

        assertDoesNotThrow(() -> service.processEvent(event));
    }

    @Test
    void handlePaymentIntentFailed_rawJson_noException() {

        String raw = "{ \"id\": \"pi_fail_raw\", \"payment_method\": \"pm_x\" }";

        Event event = mockEvent("payment_intent.payment_failed", false, null, raw);

        assertDoesNotThrow(() -> service.processEvent(event));
    }
}
