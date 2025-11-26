package com.lucamoretti.adventure_together.service.payment;

import com.lucamoretti.adventure_together.dto.payment.PaymentIntentDTO;
import com.stripe.exception.StripeException;
import com.stripe.model.PaymentIntent;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyMap;
import static org.mockito.Mockito.doReturn;

@ExtendWith(MockitoExtension.class)
class StripeClientTest {

    @InjectMocks
    private StripeClient stripeClient;

    @Mock
    private PaymentIntent paymentIntentMock;

    @BeforeEach
    void setup() {
        ReflectionTestUtils.setField(stripeClient, "stripeSecretKey", "sk_test_123");
        stripeClient.init();
    }

    // ---------------------------------------------------
    // CREATE PAYMENT INTENT
    // ---------------------------------------------------

    @Test
    void createPaymentIntent_success() throws Exception {

        PaymentIntent intent = new PaymentIntent();
        intent.setId("pi_123");
        intent.setClientSecret("secret_abc");
        intent.setAmount(1000L);

        MockedStatic<PaymentIntent> staticMock = Mockito.mockStatic(PaymentIntent.class);
        staticMock.when(() -> PaymentIntent.create(anyMap())).thenReturn(intent);

        PaymentIntentDTO dto = stripeClient.createPaymentIntent(10.0, "eur", "{\"booking\":1}");

        assertEquals("pi_123", dto.getPaymentIntentId());
        assertEquals("secret_abc", dto.getClientSecret());
        assertEquals(1000L, dto.getTotal());

        staticMock.close();
    }

    @Test
    void createPaymentIntent_error_throws() throws Exception {

        MockedStatic<PaymentIntent> staticMock = Mockito.mockStatic(PaymentIntent.class);
        staticMock.when(() -> PaymentIntent.create(anyMap()))
                .thenThrow(new StripeException("err", null, null, 400, null) {
                });

        assertThrows(RuntimeException.class, () ->
                stripeClient.createPaymentIntent(10.0, "eur", "{}")
        );

        staticMock.close();
    }

    // ---------------------------------------------------
    // RETRIEVE PAYMENT INTENT
    // ---------------------------------------------------

    @Test
    void retrievePaymentIntent_success() throws Exception {

        PaymentIntent intent = new PaymentIntent();
        intent.setId("pi_555");

        MockedStatic<PaymentIntent> staticMock = Mockito.mockStatic(PaymentIntent.class);
        staticMock.when(() -> PaymentIntent.retrieve("pi_555")).thenReturn(intent);

        PaymentIntent result = stripeClient.retrievePaymentIntent("pi_555");

        assertEquals("pi_555", result.getId());

        staticMock.close();
    }

    @Test
    void retrievePaymentIntent_error_throws() throws Exception {

        MockedStatic<PaymentIntent> staticMock = Mockito.mockStatic(PaymentIntent.class);
        staticMock.when(() -> PaymentIntent.retrieve("pi_err"))
                .thenThrow(new StripeException("err", null, null, 400, null) {
                });

        assertThrows(RuntimeException.class,
                () -> stripeClient.retrievePaymentIntent("pi_err"));

        staticMock.close();
    }

    // ---------------------------------------------------
    // CONFIRM PAYMENT INTENT
    // ---------------------------------------------------

    @Test
    void confirmPaymentIntent_success() throws Exception {

        PaymentIntent retrieved = Mockito.spy(new PaymentIntent());
        retrieved.setId("pi_777");

        PaymentIntent confirmed = new PaymentIntent();
        confirmed.setId("pi_777_confirmed");

        MockedStatic<PaymentIntent> staticMock = Mockito.mockStatic(PaymentIntent.class);

        staticMock.when(() -> PaymentIntent.retrieve("pi_777"))
                .thenReturn(retrieved);

        doReturn(confirmed).when(retrieved).confirm();

        PaymentIntent result = stripeClient.confirmPaymentIntent("pi_777");

        assertEquals("pi_777_confirmed", result.getId());

        staticMock.close();
    }

    @Test
    void confirmPaymentIntent_error_throws() throws Exception {

        MockedStatic<PaymentIntent> staticMock = Mockito.mockStatic(PaymentIntent.class);

        staticMock.when(() -> PaymentIntent.retrieve("pi_err"))
                .thenThrow(new StripeException("err", null, null, 400, null) {
                });

        assertThrows(RuntimeException.class,
                () -> stripeClient.confirmPaymentIntent("pi_err"));

        staticMock.close();
    }
}
