package com.lucamoretti.adventure_together.dto.trip;

import com.lucamoretti.adventure_together.model.trip.Trip;
import com.lucamoretti.adventure_together.service.review.ReviewService;
import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.*;
import org.springframework.web.bind.annotation.RequestParam;


import java.time.LocalDate;
import java.util.Optional;

/*
 DTO per trasferire i dati di Trip tra i vari strati dell'applicazione
 Contiene gli stessi campi dell'entità Trip, ma senza le associazioni complesse
 Include metodi statici per convertire tra DTO ed entità
 */

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TripDTO {

    private Long id;

    @NotNull(message = "La data di inizio prenotazioni non può essere nulla")
    private LocalDate dateStartBookings;
    @NotNull(message = "La data di fine prenotazioni non può essere nulla")
    private LocalDate dateEndBookings;
    @NotNull(message = "La data di partenza non può essere nulla")
    private LocalDate dateDeparture;
    @NotNull(message = "La data di ritorno non può essere nulla")
    private LocalDate dateReturn;
    @Positive(message = "Il costo individuale del viaggio deve essere positivo")
    private double tripIndividualCost;

    private String state;                  // nome semplice della classe di stato
    private String templateMailPath;

    // Relazioni come ID
    @NotNull(message = "L'itinerario di viaggio associato non può essere nullo")
    private Long tripItineraryId;

    @NotNull(message = "Il planner associato non può essere nullo")
    private Long plannerId;

    // Aggiunta per facilitare la visualizzazione
    private String tripItineraryTitle;
    private String tripItineraryPicturePath;
    private int maxParticipants;
    private int currentParticipantsCount;


    @AssertTrue(message = "La data di fine prenotazioni deve essere prima della data di partenza")
    public boolean isBookingPeriodValid() {
        return dateStartBookings != null && dateEndBookings != null && dateDeparture != null
                && dateEndBookings.isBefore(dateDeparture);
    }

    @AssertTrue(message = "La data di ritorno deve essere dopo la data di partenza")
    public boolean isReturnAfterDeparture() {
        return dateReturn != null && dateDeparture != null && dateReturn.isAfter(dateDeparture);
    }


    public static TripDTO fromEntity(Trip entity) {
        return TripDTO.builder()
                .id(entity.getId())
                .dateStartBookings(entity.getDateStartBookings())
                .dateEndBookings(entity.getDateEndBookings())
                .dateDeparture(entity.getDateDeparture())
                .dateReturn(entity.getDateReturn())
                .tripIndividualCost(entity.getTripIndividualCost())
                // Stato come nome semplice della classe
                .state(entity.getState() != null ? entity.getState().getClass().getSimpleName() : null)
                .templateMailPath(entity.getTemplateMailPath())
                // Relazioni come ID
                .tripItineraryId(entity.getTripItinerary() != null ? entity.getTripItinerary().getId() : null)
                .plannerId(entity.getPlanner() != null ? entity.getPlanner().getId() : null)
                //aggiunta per facilitare la visualizzazione
                .tripItineraryTitle(entity.getTripItinerary() != null ? entity.getTripItinerary().getTitle() : null)
                .tripItineraryPicturePath(entity.getTripItinerary() != null ? entity.getTripItinerary().getPicturePath() : null)
                .maxParticipants(entity.getTripItinerary() != null ? entity.getTripItinerary().getMaxParticipants() : 0)
                .currentParticipantsCount(entity.getCurrentParticipantsCount())
                .build();
    }

    public Trip toEntity() {
        Trip trip = new Trip();
        trip.setId(id);
        trip.setDateStartBookings(dateStartBookings);
        trip.setDateEndBookings(dateEndBookings);
        trip.setDateDeparture(dateDeparture);
        trip.setDateReturn(dateReturn);
        trip.setTripIndividualCost(tripIndividualCost);
        trip.setTemplateMailPath(templateMailPath);
        // state, tripItinerary, planner, bookings: gestiti nel Service
        return trip;
    }


}
