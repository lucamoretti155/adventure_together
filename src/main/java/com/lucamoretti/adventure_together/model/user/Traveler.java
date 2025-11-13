package com.lucamoretti.adventure_together.model.user;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;

// Sottoclasse Traveler che estende User
// Mappata discriminatore "TRAVELER"
// Contiene attributi specifici per i viaggiatori
// Sono gli enduser dell'applicazione che prenotano i viaggi organizzati dai planner

@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Entity
@DiscriminatorValue("TRAVELER")
public class Traveler extends User {
    //è stato scelto di usare LocalDate per facilitare la gestione delle date senza orario
    private LocalDate dateOfBirth;

    // telefono come String per evitare problemi con zeri iniziali/indicativi
    @Column(length = 32)
    private String telephone;

    public Traveler(String email, String password, String firstName, String lastName, boolean active,
                    LocalDate dateOfBirth, String telephone) {
        super(null, email, password, firstName, lastName, active, null);
        this.dateOfBirth = dateOfBirth;
        this.telephone = telephone;
    }
}

