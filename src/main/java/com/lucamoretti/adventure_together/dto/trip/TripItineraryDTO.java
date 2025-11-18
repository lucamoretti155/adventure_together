package com.lucamoretti.adventure_together.dto.trip;

import com.lucamoretti.adventure_together.model.trip.TripItinerary;
import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/*
 DTO per trasferire i dati di TripItineraryDay tra i vari strati dell'applicazione
 Contiene gli stessi campi dell'entità TripItinerary, ma senza le associazioni complesse
 Include metodi statici per convertire tra DTO ed entità
 */

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TripItineraryDTO {

    private Long id;

    @NotBlank(message = "Il titolo non può essere vuoto")
    private String title;

    @NotBlank(message = "La descrizione non può essere vuota")
    private String description;

    @NotBlank(message = "Il percorso dell'immagine non può essere vuoto")
    private String picturePath;

    // solo per upload, non obbligatorio
    // se non viene fornita, viene assegnata una immagine di default
    private MultipartFile pictureFile;

    @Min(value = 1, message = "La durata deve essere di almeno 1 giorno")
    private int durationInDays;

    @Min(value = 1, message = "Il numero minimo di partecipanti deve essere almeno 1")
    private int minParticipants;

    @Min(value = 1, message = "Il numero massimo di partecipanti deve essere almeno 1")
    private int maxParticipants;

    @AssertTrue(message = "Minimo partecipanti non può essere maggiore del massimo partecipanti")
    public boolean isValidParticipantsRange() {
        return minParticipants <= maxParticipants;
    }

    // Relazioni
    private Set<Long> countryIds;
    private Set<Long> categoryIds;
    private Set<Long> departureAirportIds;
    private Long plannerId;

    // Lista dei giorni dell'itinerario
    // Ha senso mappare tutto il dto dato che contiene più campi e ha senso esporli come DTO completo non solo l'ID
    private List<TripItineraryDayDTO> days;

    public static TripItineraryDTO fromEntity(TripItinerary entity) {
        return TripItineraryDTO.builder()
                .id(entity.getId())
                .title(entity.getTitle())
                .description(entity.getDescription())
                .picturePath(entity.getPicturePath())
                .durationInDays(entity.getDurationInDays())
                .minParticipants(entity.getMinParticipants())
                .maxParticipants(entity.getMaxParticipants())
                // Set degli ID delle nazioni
                .countryIds(entity.getCountries() != null // controllo null per evitare NPE
                        ? entity.getCountries().stream() // creo uno stream delle Country
                        .map(c -> c.getId())  // mappa ogni Country all'ID quindi lo Stream diventa Stream<Long>
                        .collect(Collectors.toSet())  // raccoglie i Long in un Set<Long>
                        : Set.of()) // se null, ritorna Set vuoto
                // Set degli ID delle categorie (come sopra)
                .categoryIds(entity.getCategories() != null
                        ? entity.getCategories().stream()
                        .map(cat -> cat.getId())
                        .collect(Collectors.toSet())
                        : Set.of())
                // Set degli ID degli aeroporti di partenza (come sopra)
                .departureAirportIds(entity.getDepartureAirports() != null
                        ? entity.getDepartureAirports().stream()
                        .map(da -> da.getId())
                        .collect(Collectors.toSet())
                        : Set.of())
                .plannerId(entity.getPlanner() != null ? entity.getPlanner().getId() : null)
                // List dei giorni dell'itinerario
                .days(entity.getDays() != null
                        ? entity.getDays().stream()
                        .map(TripItineraryDayDTO::fromEntity) // invoca il mapper statico fromEntity che coverte l'entity nel suo DTO
                        .toList() // materializza lo stream in una List
                        : List.of())
                .build();
    }

    public TripItinerary toEntity() {
        TripItinerary itinerary = new TripItinerary();
        itinerary.setId(id);
        itinerary.setTitle(title);
        itinerary.setDescription(description);
        itinerary.setPicturePath(picturePath);
        itinerary.setDurationInDays(durationInDays);
        itinerary.setMinParticipants(minParticipants);
        itinerary.setMaxParticipants(maxParticipants);
        // countries / categories / departureAirports / planner / days: verranno settati nel Service
        return itinerary;
    }
}
