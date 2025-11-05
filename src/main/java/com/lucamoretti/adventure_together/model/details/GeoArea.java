package com.lucamoretti.adventure_together.model.details;

import jakarta.persistence.*;
import lombok.*;

/* Rappresenta un'area geografica per la categorizzazione dei viaggi
 Viene aggiornato solamente dagli admin per aggiungere nuove aree geografiche
 Contiene un identificativo univoco e il nome dell'area geografica
 Ad esso verranno associate diverse country (es. "Italia", "Francia", "Giappone", etc.)
 Una country apparterrà ad una sola geoArea, mentre una geoArea potrà avere più country associate
 Viene utilizzata per raggruppare i viaggi in base all'area geografica di destinazione
*/

@Data @NoArgsConstructor @AllArgsConstructor
@Entity @Table(name = "geo_areas")
public class GeoArea {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable=false, unique=true)
    private String geoArea; // es. "Asia", "Europa", "Africa", "America Settentrionale", etc.
}

