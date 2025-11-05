package com.lucamoretti.adventure_together.model.review;

import com.lucamoretti.adventure_together.model.trip.Trip;
import com.lucamoretti.adventure_together.model.user.Traveler;
import jakarta.persistence.*;
import lombok.*;

/*
 Rappresenta una recensione lasciata da un viaggiatore per un viaggio specifico.
 Contiene il testo della recensione, il punteggio assegnato (da 1 a 5), e le associazioni
 ManyToOne con Trip e Traveler per indicare a quale viaggio si riferisce la recensione
 e chi l'ha scritta.
*/

@Data @NoArgsConstructor @AllArgsConstructor
@Entity
@Table(
        name = "reviews",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = {"trip_id", "traveler_id"})  // Un viaggiatore può lasciare una sola recensione per ogni viaggio
        }
)
public class Review {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Testo della recensione, non prevede limiti di lunghezza
    // Per scelta prevedo che debba essere sempre presente
    @Lob @Column(nullable=false)
    private String textReview;

    // Punteggio assegnato alla recensione, da 1 a 5
    // Non può essere nullo e viene validato a livello di logica applicativa
    @Column(nullable=false)
    private int score; // 1..5

    // Relazioni con altre entità

    // Associazione ManyToOne con Trip
    // Una recensione è per un solo viaggio, ma un viaggio può avere molte recensioni
    // Il proprietario della relazione è Review che contiene la foreign key
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "trip_id",nullable = false)
    private Trip trip;

    // Associazione ManyToOne con Traveler
    // Una recensione è scritta da un solo viaggiatore, ma un viaggiatore può scrivere molte recensioni
    // Il proprietario della relazione è Review che contiene la foreign key
    // Ovviamente solo i traveler che hanno effettuato il viaggio possono scrivere recensioni per quel viaggio
    // Tecnicamente anche l'admin potrebbe scrivere recensioni,
    // ma dato che non risulterà fra i partecipanti a livello di logica applicativa non potrà farlo
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "traveler_id", nullable = false)
    private Traveler traveler;
}
