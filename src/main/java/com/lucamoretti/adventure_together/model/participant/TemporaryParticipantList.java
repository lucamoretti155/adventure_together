package com.lucamoretti.adventure_together.model.participant;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.ArrayList;
import java.util.List;

// Rappresenta una lista temporanea di partecipanti per una prenotazione in corso
// Utilizzata per gestire i partecipanti prima che la prenotazione venga finalizzata

@Entity
@Table(name = "temporary_participant_lists")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TemporaryParticipantList {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToMany(mappedBy = "list", cascade = CascadeType.ALL)
    private List<TemporaryParticipant> participants = new ArrayList<>();
}

