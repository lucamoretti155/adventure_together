package com.lucamoretti.adventure_together.model.user;

import jakarta.persistence.*;
import lombok.*;

// Sottoclasse Admin che estende Planner
// Mappata con discriminatore "ADMIN"
// Non contiene attributi specifici per gli admin ma hanno facoltà aggiuntive rispetto ai planner
// Gli admin gestiscono l'applicazione a livello globale e hanno il massimo livello di accesso
// Hanno gli stessi attributi e stesse facoltà dei planner, ma in aggiunta possono gestire altri aspetti dell'applicazione

@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Entity
@DiscriminatorValue("ADMIN")
public class Admin extends Planner {
    //nessun attributo aggiuntivo per ora
}

