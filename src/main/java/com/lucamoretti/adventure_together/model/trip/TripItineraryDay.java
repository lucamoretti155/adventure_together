package com.lucamoretti.adventure_together.model.trip;

import jakarta.persistence.*;
import lombok.*;

/* Rappresenta il dettaglio di un singolo giorno all'interno dell'itinerario di un viaggio
 Contiene il numero del giorno e la descrizione dettagliata delle attività previste per quel giorno
 Ogni TripItineraryDay è associato a un TripItinerary specifico
 La combinazione di trip_itinerary_id e day_number è unica per evitare duplicati
 La descrizione dell'itinerario del giorno è memorizzata come LOB per gestire contenuti HTML estesi
*/


@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Entity
@Table(name = "trip_itinerary_days",
        uniqueConstraints = @UniqueConstraint(columnNames = {"trip_itinerary_id","day_number"}))
// vincolo di unicità, evita duplicati del numero del giorno per lo stesso itinerario
public class TripItineraryDay {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name="day_number", nullable=false)
    private int dayNumber;

    @Column(name="title", nullable=false, length=200)
    private String title;

    // LOB per gestire contenuti HTML estesi nell'itinerario del giorno
    // il planner avrà a disposizione un editor WYSIWYG per creare contenuti HTML che verranno salvati qui come testo
    // e poi renderizzati nella view del viaggio
    @Lob
    @Column(name="description", nullable=false, columnDefinition="TEXT")
    private String description;

    // Associazione ManyToOne con TripItinerary
    // FetchType.LAZY per caricare il TripItinerary solo quando necessario
    // optional = false indica che un TripItineraryDay deve sempre avere un TripItinerary associato
    // TripItinerary è il proprietario della relazione
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "trip_itinerary_id", nullable = false)
    private TripItinerary tripItinerary;
}
