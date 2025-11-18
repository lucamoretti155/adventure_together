package com.lucamoretti.adventure_together.model.user;

import jakarta.persistence.*;
import lombok.*;

// Sottoclasse Planner che estende User
// Mappata con discriminatore "PLANNER"
// Contiene attributi specifici per i planner
// I planner sono gli utenti che organizzano i viaggi nell'applicazione
// L'utenza è creata dagli admin, il traveler al primo accesso resettera la propria password

@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Entity
@DiscriminatorValue("PLANNER")
public class Planner extends User {
    //ipotizzo sia il codice assegnato dall'azienda al dipendente
    //nessuna logica particolare, solo un identificativo univoco
    @Column(nullable = false, unique = true)
    private String employeeId;

    public Planner(String email, String password, String firstName, String lastName, boolean active, String employeeId) {
        super(null, email, password, firstName, lastName, active, null);
        this.employeeId = employeeId;
    }
}
