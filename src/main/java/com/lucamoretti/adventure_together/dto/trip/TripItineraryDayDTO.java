package com.lucamoretti.adventure_together.dto.trip;

import com.lucamoretti.adventure_together.model.trip.TripItineraryDay;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
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

    @Min(value = 1, message = "Il numero del giorno deve essere maggiore o uguale a 1")
    private int dayNumber;

    @NotBlank(message = "Il titolo del giorno non può essere vuoto")
    private String title;

    @NotBlank(message = "La descrizione del giorno non può essere vuota")
    private String description;

    @NotNull(message = "L'ID dell'itinerario di viaggio non può essere nullo")
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

