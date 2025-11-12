package com.lucamoretti.adventure_together.dto.booking;

import com.lucamoretti.adventure_together.dto.participant.ParticipantDTO;
import com.lucamoretti.adventure_together.model.booking.Booking;
import com.lucamoretti.adventure_together.model.details.DepartureAirport;
import com.lucamoretti.adventure_together.model.participant.Participant;
import com.lucamoretti.adventure_together.model.trip.Trip;
import com.lucamoretti.adventure_together.model.user.Traveler;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import lombok.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

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

    // non è incluso il collegamento al pagamento (gestito separatamente)

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

    public Booking toEntity(Trip trip, Traveler traveler, DepartureAirport departureAirport, List<Participant> participants) {
        Booking booking = new Booking();

        booking.setId(this.id); // può essere null in creazione
        booking.setTrip(trip);
        booking.setTraveler(traveler);
        booking.setDepartureAirport(departureAirport);

        // Se non specificata, imposta la data di prenotazione al giorno corrente
        booking.setBookingDate(
                this.bookingDate != null ? this.bookingDate : LocalDate.now()
        );


        // Creo una lista finale di partecipanti che include il traveler
        List<Participant> finalParticipants = new ArrayList<>();

        // Primo participant = traveler (inserito automaticamente)
        Participant self = new Participant();
        self.setFirstName(traveler.getFirstName());
        self.setLastName(traveler.getLastName());
        self.setDateOfBirth(traveler.getDateOfBirth());
        self.setBooking(booking);
        finalParticipants.add(self);

        // Aggiungo gli altri partecipanti passati dal form (se esistono)
        if (participants != null && !participants.isEmpty()) {
            for (Participant p : participants) {
                p.setBooking(booking); // collega bidirezionalmente
                finalParticipants.add(p);
            }
        }

        booking.setParticipants(finalParticipants);



        return booking;
    }


}


