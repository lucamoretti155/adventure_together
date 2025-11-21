package com.lucamoretti.adventure_together.dto.review;

import com.lucamoretti.adventure_together.model.review.Review;
import com.lucamoretti.adventure_together.model.trip.Trip;
import com.lucamoretti.adventure_together.model.user.Traveler;
import jakarta.validation.constraints.*;
import lombok.*;

/*
 DTO per la gestione delle recensioni.
 Contiene campi per il testo della recensione, il punteggio, e gli ID del viaggio e del viaggiatore.
 Include validazioni per garantire che i dati siano corretti prima di essere elaborati.
 Fornisce metodi di conversione tra l'Entity Review e il DTO ReviewDTO.
*/

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReviewDTO {

    private Long id;

    @NotBlank(message = "Il testo della recensione non può essere vuoto.")
    @Size(min = 10, max = 5000, message = "La recensione deve contenere almeno 10 caratteri.")
    private String textReview;

    @Min(value = 1, message = "Il punteggio minimo è 1.")
    @Max(value = 5, message = "Il punteggio massimo è 5.")
    private int score;

    @NotNull(message = "L'ID del viaggio è obbligatorio.")
    @Positive(message = "L'ID del viaggio deve essere un valore positivo.")
    private Long tripId;

    @NotNull(message = "L'ID del viaggiatore è obbligatorio.")
    @Positive(message = "L'ID del viaggiatore deve essere un valore positivo.")
    private Long travelerId;

    // Campi aggiuntivi utili per la UI
    private String tripTitle;

    // Conversione Entity → DTO
    public static ReviewDTO fromEntity(Review entity) {
        return ReviewDTO.builder()
                .id(entity.getId())
                .textReview(entity.getTextReview())
                .score(entity.getScore())
                .tripId(entity.getTrip() != null ? entity.getTrip().getId() : null)
                .tripTitle(entity.getTrip() != null ? entity.getTrip().getTripItinerary().getTitle() : null)
                .travelerId(entity.getTraveler() != null ? entity.getTraveler().getId() : null)
                .build();
    }

    // Conversione DTO → Entity
    // (richiede che Trip e Traveler siano già recuperati dai rispettivi repository)
    public Review toEntity(Trip trip, Traveler traveler) {
        Review review = new Review();
        review.setId(this.id);
        review.setTextReview(this.textReview);
        review.setScore(this.score);
        review.setTrip(trip);
        review.setTraveler(traveler);
        return review;
    }
}

