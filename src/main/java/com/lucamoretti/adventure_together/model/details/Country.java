package com.lucamoretti.adventure_together.model.details;

import jakarta.persistence.*;
import lombok.*;

/* Rappresenta una nazione all'interno di un'area geografica
 Esempio: Italia, Francia, Germania
 Una country apparterrà ad una sola geoArea, mentre una geoArea potrà avere più country associate
 Viene utilizzata per categorizzare i viaggi in base alla/e nazione/i di destinazione
*/

@Data @NoArgsConstructor @AllArgsConstructor
@Entity @Table(name = "countries")
public class Country {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable=false, unique=true)
    private String country;

    // Associazione ManyToOne con GeoArea
    // FetchType.LAZY per caricare la geoArea solo quando necessario
    // optional = false indica che una country deve sempre avere una geoArea associata
    // il proprietario della relazione è Country
    @ManyToOne(optional = false, fetch = FetchType.LAZY) // Una country appartiene a una sola geoArea
    @JoinColumn(name = "geo_area_id", nullable = false)
    private GeoArea geoArea; // Associazione ManyToOne con GeoArea

    // non aggiungo relazioni many to many con TripItinerary
    // perchè gestito direttamente da TripItinerary con una tabella di join
}
