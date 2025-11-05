package com.lucamoretti.adventure_together.model.details;


import jakarta.persistence.*;
import lombok.*;

// Rappresenta una categoria di viaggio
// Esempio: Avventura, Cultura, Relax
// Viene utilizzata per categorizzare i viaggi in base al tipo di esperienza
// Un viaggio può appartenere a più categorie, e una categoria può includere più viaggi
// Viene aggiornata solo dagli admin per aggiungere nuove categorie di viaggio

@Data @NoArgsConstructor @AllArgsConstructor
@Entity @Table(name = "categories")
public class Category {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable=false, unique=true)
    private String name;

    // non aggiungo relazioni many to many con Trip
    // perchè gestito direttamente da Trip con una tabella di join

}

