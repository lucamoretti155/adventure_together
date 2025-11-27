package com.lucamoretti.adventure_together.dto.payment;

import lombok.*;

/*
    Data Transfer Object per rappresentare un Payment Intent di Stripe.
    Contiene l'ID del Payment Intent e il client secret necessario per completare il pagamento.
 */

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PaymentIntentDTO {
    private String paymentIntentId;
    private String clientSecret;
    private double total; // importo utile per frontend
}
