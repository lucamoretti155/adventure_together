package com.lucamoretti.adventure_together.repository.participant;

import com.lucamoretti.adventure_together.model.participant.Participant;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

/*
    Repository ParticipantRepository
    Interfaccia che estende JpaRepository per la gestione delle operazioni CRUD sull'entità Participant.
*/

public interface ParticipantRepository extends JpaRepository<Participant, Long> {

    // Metodo per trovare tutti i partecipanti associati a un determinato viaggio (tripId)
    // facendo la join fra Participant e Booking
    List<Participant> findByBooking_Trip_Id(Long tripId);
}
