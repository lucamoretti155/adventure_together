package com.lucamoretti.adventure_together.dto.booking;

import com.lucamoretti.adventure_together.dto.participant.ParticipantDTO;
import com.lucamoretti.adventure_together.dto.payment.PaymentDTO;
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

    // impostata dal sistema alla data corrente
    private LocalDate bookingDate;

    private Integer numParticipants;

    // riferimenti obbligatori per la creazione della prenotazione
    @NotNull(message = "Trip ID è obbligatorio")
    private Long tripId;

    private String tripItineraryTitle; // solo per DTO in output
    private String tripItineraryPicturePath; // solo per DTO in output

    @NotNull(message = "Traveler ID è obbligatorio")
    private Long travelerId;

    @NotNull(message = "L'aeroporto di partenza è obbligatorio")
    private Long departureAirportId;

    private String insuranceType;

    // il pagamento passa per 2 fasi: prima la creazione della booking (senza pagamento)
    // poi il pagamento vero e proprio (collegato successivamente alla booking)
    // quindi questo campo non può essere valorizzato in fase di creazione della booking
    private PaymentDTO payment;

    // campo calcolato (costo totale della prenotazione)
    private Double totalCost;

    // non è incluso il collegamento al pagamento (gestito separatamente)

    /*
      Lista completa dei partecipanti, inclusi:
       - il Traveler che prenota (inserito dal service come primo Participant)
       - eventuali accompagnatori aggiunti dal form
     */
    @Valid
    @Size(min=1, message = "Il numero di partecipanti deve essere almeno 1")
    @Builder.Default // evita null pointer exception in fase di build
    private List<ParticipantDTO> participants = new ArrayList<>();

    public static BookingDTO fromEntity(Booking booking) {
        return BookingDTO.builder()
                .id(booking.getId())
                .bookingDate(booking.getBookingDate())
                .numParticipants(booking.getNumParticipants()) // calcolato dinamicamente
                .tripId(booking.getTrip().getId())
                .tripItineraryTitle(booking.getTrip().getTripItinerary().getTitle())
                .tripItineraryPicturePath(booking.getTrip().getTripItinerary().getPicturePath())
                .travelerId(booking.getTraveler().getId())
                .departureAirportId(booking.getDepartureAirport().getId())
                .totalCost(booking.getPayment().getAmountPaid())
                .participants(
                        booking.getParticipants().stream()
                                .map(ParticipantDTO::fromEntity)
                                .toList()
                )
                .payment(booking.getPayment() != null
                        ? PaymentDTO.fromEntity(booking.getPayment())
                        : null)
                .insuranceType(booking.getInsuranceType())
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

        // Aggiungo i partecipanti passati dal form (se esistono)
        if (participants != null && !participants.isEmpty()) {
            for (Participant p : participants) {
                p.setBooking(booking); // collega bidirezionalmente
                finalParticipants.add(p);
            }
        }

        booking.setParticipants(finalParticipants);

        booking.setInsuranceType(this.insuranceType);

        return booking;
    }
}


