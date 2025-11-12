package com.lucamoretti.adventure_together.dto.payment;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.*;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PaymentDTO {
    private Long id;

    @NotNull(message = "La data del pagamento non può essere null")
    @PastOrPresent(message = "La data del pagamento non può essere nel futuro")
    private LocalDate paymentDate;

    @Positive(message = "L'importo pagato deve essere maggiore di zero")
    private double amountPaid;

    @PositiveOrZero(message = "L'importo dell'assicurazione non può essere negativo")
    private double amountInsurance;

    @NotNull(message = "L'ID della prenotazione non può essere null")
    @Positive(message = "L'ID della prenotazione deve essere maggiore di zero")
    private Long bookingId;

    private String paymentIntentId;
    private String clientSecret;
    private String status;
    private String paymentMethod;
    private String currency;


    public static PaymentDTO fromEntity(com.lucamoretti.adventure_together.model.payment.Payment entity) {
        return PaymentDTO.builder()
                .id(entity.getId())
                .paymentDate(entity.getPaymentDate())
                .amountPaid(entity.getAmountPaid())
                .amountInsurance(entity.getAmountInsurance())
                .bookingId(entity.getBooking() != null ? entity.getBooking().getId() : null)
                .build();
    }
}

