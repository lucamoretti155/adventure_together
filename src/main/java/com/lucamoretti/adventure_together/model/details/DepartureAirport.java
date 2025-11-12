package com.lucamoretti.adventure_together.model.details;

import jakarta.persistence.*;
import lombok.*;

/* Rappresenta un aeroporto di partenza
 Esempio: Milano Malpensa, Roma Fiumicino
 Viene utilizzato per specificare gli aeroporti di partenza dei viaggi
 Un viaggio può avere più aeroporti di partenza associati
 Viene inizialmente popolato con i principali aeroporti, ma può essere esteso dagli admin
*/

@Data @NoArgsConstructor @AllArgsConstructor
@Entity @Table(name = "departure_airports")
public class DepartureAirport {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable=false, length=8)
    private String code; // es. "MXP"

    @Column(nullable=false)
    private String name;

    @Column(nullable=false)
    private String city;


    // non aggiungo relazioni many to many con Trip
    // perchè gestito direttamente da Trip con una tabella di join

}

