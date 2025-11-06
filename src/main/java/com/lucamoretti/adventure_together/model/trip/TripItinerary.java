package com.lucamoretti.adventure_together.model.trip;

import com.lucamoretti.adventure_together.model.details.Category;
import com.lucamoretti.adventure_together.model.details.Country;
import com.lucamoretti.adventure_together.model.details.DepartureAirport;
import com.lucamoretti.adventure_together.model.user.Planner;
import jakarta.persistence.*;
import lombok.*;
import java.util.LinkedHashSet;
import java.util.Set;

/*
Rappresenta l'itinerario di un viaggio organizzato
Contiene informazioni generali sull'itinerario, come titolo, descrizione, immagine rappresentativa,
durata, numero minimo e massimo di partecipanti, e le relazioni con paesi, aeroporti di partenza e categorie di viaggio.
Contiene anche i dettagli giornalieri dell'itinerario tramite la relazione OneToMany con TripItineraryDay.
Viene utilizzato per definire l'itinerario di un viaggio che può essere poi associato a più viaggi effettivi.
Esso viene creato e gestito dai travel planner per organizzare i viaggi offerti sulla piattaforma.
Di fatto rappresenta il "modello" di un viaggio organizzato e la classe Trip conterrà i riferimenti
a TripItinerary per definire quali sono le date e i prezzi associati.
 */

@Data @NoArgsConstructor @AllArgsConstructor
@Entity @Table(name = "trip_itineraries")
public class TripItinerary {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable=false, unique = true) // il titolo deve essere unico per facilitare la ricerca
    private String title;

    // Descrizione sommaria dell'itinerario del viaggio
    // memorizzata come LOB per gestire contenuti HTML estesi
    // il planner avrà a disposizione un editor WYSIWYG per creare contenuti HTML che verranno salvati qui come testo
    // e poi renderizzati nella view del viaggio
    @Lob
    @Column(nullable=false)
    private String description;

    // Path dell'immagine rappresentativa dell'itinerario salvata nel file system
    @Column(nullable=false)
    private String picturePath;

    // Durata del viaggio in giorni
    // deve essere coerente con il numero di TripItineraryDay associati
    @Column(nullable=false)
    private int durationInDays;

    // Numero minimo e massimo di partecipanti al viaggio
    // Servono per gestire le prenotazioni e garantire che il viaggio possa partire
    @Column(nullable=false)
    private int minParticipants;

    @Column(nullable=false)
    private int maxParticipants;

    // Ritorna true se l'itinerario copre più di una nazione
    public boolean isMultiDestination() {
        return countries != null && countries.size() > 1;
    }

    /* Dettagli giornalieri dell'itinerario
     TripItinerary è il proprietario della relazione OneToMany con TripItineraryDay
     Le varie tuple vengono ordinate per numero del giorno
     cascade ALL e orphanRemoval true per gestire correttamente la persistenza dei giorni insieme all'itinerario
     Uso di LinkedHashSet per mantenere l'ordine di inserimento
     L'annotazione @OrderBy assicura che i giorni siano sempre ordinati per dayNumber in modo ascendente
     */
    @OneToMany(mappedBy = "tripItinerary", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("dayNumber ASC")
    private Set<TripItineraryDay> days = new LinkedHashSet<>();

    // relazione molti a molti con Country
    @ManyToMany
    @JoinTable(name = "itinerary_countries",
            joinColumns = @JoinColumn(name = "itinerary_id"),
            inverseJoinColumns = @JoinColumn(name = "country_id"))
    private Set<Country> countries = new LinkedHashSet<>();

    // relazione molti a molti con DepartureAirport
    @ManyToMany
    @JoinTable(name = "itinerary_departure_airports",
            joinColumns = @JoinColumn(name = "itinerary_id"),
            inverseJoinColumns = @JoinColumn(name = "airport_id"))
    private Set<DepartureAirport> departureAirports = new LinkedHashSet<>();

    // relazione molti a molti con Category
    @ManyToMany
    @JoinTable(name = "itinerary_categories",
            joinColumns = @JoinColumn(name = "itinerary_id"),
            inverseJoinColumns = @JoinColumn(name = "category_id"))
    private Set<Category> categories = new LinkedHashSet<>();

    // Il planner (utente con ruolo planner o admin) che ha creato e gestisce l'itinerario
    // Il proprietario della relazione è TripItinerary
    // non è necessario mantenere una lista di Trip nel Planner
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "planner_id", nullable = false)
    private Planner planner;
}
