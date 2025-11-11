package com.lucamoretti.adventure_together.dto.booking;

import com.lucamoretti.adventure_together.dto.participant.ParticipantDTO;
import com.lucamoretti.adventure_together.model.booking.Booking;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import lombok.*;
import java.time.LocalDate;
import java.util.List;

/*
 * Data Transfer Object per la gestione delle prenotazioni (Booking).
 * Utilizzato per trasferire i dati tra il client e il server.
 */

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BookingDTO {

    // id della booking (null in fase di creazione)
    private Long id;

    // impostata dal sistema alla data corrente; non deve essere futura
    @PastOrPresent(message = "La data di prenotazione non può essere futura")
    private LocalDate bookingDate;

    // calcolato dinamicamente da booking.getNumParticipants()
    // non richiesto in input, ma utile in output (solo read)
    @Min(value = 1, message = "Il numero totale di partecipanti deve essere almeno 1")
    private Integer numParticipants;

    // riferimenti obbligatori per la creazione della prenotazione
    @NotNull(message = "Trip ID è obbligatorio")
    private Long tripId;

    @NotNull(message = "Traveler ID è obbligatorio")
    private Long travelerId;

    @NotNull(message = "L'aeroporto di partenza è obbligatorio")
    private Long departureAirportId;

    // campo calcolato (costo totale della prenotazione)
    @Positive(message = "Il costo totale deve essere positivo")
    private Double totalCost;

    /*
      Lista completa dei partecipanti, inclusi:
       - il Traveler che prenota (inserito dal service come primo Participant)
       - eventuali accompagnatori aggiunti dal form
     */
    @Valid
    @Size(max = 20, message = "Puoi prenotare per un massimo di 20 persone")
    private List<ParticipantDTO> participants;

    public static BookingDTO fromEntity(Booking booking) {
        return BookingDTO.builder()
                .id(booking.getId())
                .bookingDate(booking.getBookingDate())
                .numParticipants(booking.getNumParticipants()) // calcolato dinamicamente
                .tripId(booking.getTrip().getId())
                .travelerId(booking.getTraveler().getId())
                .departureAirportId(booking.getDepartureAirport().getId())
                .totalCost(booking.getTotalCost())
                .participants(
                        booking.getParticipants().stream()
                                .map(ParticipantDTO::fromEntity)
                                .toList()
                )
                .build();
    }
}


