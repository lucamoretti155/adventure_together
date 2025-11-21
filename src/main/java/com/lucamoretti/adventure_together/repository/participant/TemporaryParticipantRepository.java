package com.lucamoretti.adventure_together.repository.participant;

import com.lucamoretti.adventure_together.model.participant.TemporaryParticipant;
import org.springframework.data.jpa.repository.JpaRepository;

// Repository per la gestione dei TemporaryParticipant nel database.

public interface TemporaryParticipantRepository extends JpaRepository<TemporaryParticipant, Long> {}
