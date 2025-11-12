package com.lucamoretti.adventure_together.repository.participant;

import com.lucamoretti.adventure_together.model.participant.Participant;
import org.springframework.data.jpa.repository.JpaRepository;

/*
    Repository ParticipantRepository
    Interfaccia che estende JpaRepository per la gestione delle operazioni CRUD sull'entità Participant.
*/

public interface ParticipantRepository extends JpaRepository<Participant, Long> {
}
