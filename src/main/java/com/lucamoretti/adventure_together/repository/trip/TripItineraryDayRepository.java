package com.lucamoretti.adventure_together.repository.trip;

import com.lucamoretti.adventure_together.model.trip.TripItineraryDay;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/*
 Interfaccia repository per la gestione delle entità TripItineraryDay.
 Estende JpaRepository per fornire operazioni CRUD e query personalizzate.
 */

@Repository
public interface TripItineraryDayRepository extends JpaRepository<TripItineraryDay, Long> {

    // Trova tutti i giorni di un itinerario di viaggio specifico, ordinati per numero del giorno in modo ascendente.
    List<TripItineraryDay> findByTripItinerary_IdOrderByDayNumberAsc(Long tripItineraryId);

    // Verifica se un giorno specifico in un itinerario di viaggio esiste dato l'id dell'itinerario e il numero del giorno.
    boolean existsByTripItinerary_IdAndDayNumber(Long tripItineraryId, int dayNumber);
}

