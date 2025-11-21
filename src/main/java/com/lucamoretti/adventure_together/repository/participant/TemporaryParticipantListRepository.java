package com.lucamoretti.adventure_together.repository.participant;

import com.lucamoretti.adventure_together.model.participant.TemporaryParticipantList;
import org.springframework.data.jpa.repository.JpaRepository;

// Repository per la gestione delle liste temporanee di partecipanti.

public interface TemporaryParticipantListRepository extends JpaRepository<TemporaryParticipantList, Long> {}
