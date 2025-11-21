package com.lucamoretti.adventure_together.model.participant;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDate;

// Rappresenta un partecipante temporaneo associato a una lista di partecipanti temporanei.
// viene utilizzato durante il processo di prenotazione prima che i partecipanti vengano convertiti in entità permanenti.

@Entity
@Table(name = "temporary_participants")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TemporaryParticipant {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String firstName;
    private String lastName;
    private LocalDate dateOfBirth;

    @ManyToOne
    @JoinColumn(name = "list_id")
    private TemporaryParticipantList list;
}
