package com.lucamoretti.adventure_together.dto.trip;

import com.lucamoretti.adventure_together.model.trip.Trip;
import lombok.*;


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

    private LocalDate dateStartBookings;
    private LocalDate dateEndBookings;
    private LocalDate dateDeparture;
    private LocalDate dateReturn;

    private double tripIndividualCost;

    private String state;                  // nome semplice della classe di stato
    private int currentParticipantsCount;  // calcolato da bookings.size()
    private String templateMailPath;

    // Relazioni come ID
    private Long tripItineraryId;
    private Long plannerId;

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
                // Conteggio attuale dei partecipanti dalle bookings
                .currentParticipantsCount(
                        entity.getBookings() != null ? entity.getBookings().size() : 0
                )
                .templateMailPath(entity.getTemplateMailPath())
                // Relazioni come ID
                .tripItineraryId(entity.getTripItinerary() != null ? entity.getTripItinerary().getId() : null)
                .plannerId(entity.getPlanner() != null ? entity.getPlanner().getId() : null)
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
