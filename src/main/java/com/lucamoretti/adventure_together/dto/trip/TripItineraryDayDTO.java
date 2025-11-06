package com.lucamoretti.adventure_together.dto.trip;

import com.lucamoretti.adventure_together.model.trip.TripItineraryDay;
import lombok.*;

/* DTO per trasferire i dati di TripItineraryDay tra i vari strati dell'applicazione
 Contiene gli stessi campi dell'entità TripItineraryDay, ma senza le associazioni complesse
 Include metodi statici per convertire tra DTO ed entità
*/

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TripItineraryDayDTO {
    private Long id;
    private int dayNumber;
    private String title;
    private String description;
    private Long tripItineraryId;   // riferimento al TripItinerary

    // Metodo statico per convertire un'entità TripItineraryDay in un DTO
    // utile per trasferire i dati all'esterno dell'applicazione
    public static TripItineraryDayDTO fromEntity(TripItineraryDay entity) {
        return TripItineraryDayDTO.builder()
                .id(entity.getId())
                .dayNumber(entity.getDayNumber())
                .title(entity.getTitle())
                .description(entity.getDescription())
                .tripItineraryId(entity.getTripItinerary().getId())
                .build();
    }
    // Metodo per convertire il DTO in un'entità TripItineraryDay
    // utile per salvare o aggiornare l'entità nel database
    public TripItineraryDay toEntity() {
        TripItineraryDay day = new TripItineraryDay();
        day.setId(id);
        day.setDayNumber(dayNumber);
        day.setTitle(title);
        day.setDescription(description);
        return day;
    }
}

