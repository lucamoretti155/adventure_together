package com.lucamoretti.adventure_together.dto.payment;

import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PaymentIntentDTO {
    private String paymentIntentId;
    private String clientSecret;
}
