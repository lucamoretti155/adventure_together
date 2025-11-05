package com.lucamoretti.adventure_together.model.user;

import jakarta.persistence.*;
import lombok.*;

// Sottoclasse Admin che estende User
// Mappata nella tabella "admins" con discriminatore "ADMIN"
// Contiene attributi specifici per gli admin
// Gli admin gestiscono l'applicazione a livello globale e hanno il massimo livello di accesso
// Hanno gli stessi attributi e stesse facoltà dei planner, ma in aggiunta possono gestire altri aspetti dell'applicazione

@Data @NoArgsConstructor @EqualsAndHashCode(callSuper = true)
@Entity @DiscriminatorValue("ADMIN")
@Table(name = "admins")
public class Admin extends User {

    //ipotizzo sia il codice assegnato dall'azienda al dipendente
    //nessuna logica particolare, solo un identificativo univoco
    @Column(nullable = false, unique = true)
    private Long employeeId;

    public Admin(String email, String password, String firstName, String lastName, boolean active, Long employeeId) {
        super(null, email, password, firstName, lastName, active, null);
        this.employeeId = employeeId;
    }
}

