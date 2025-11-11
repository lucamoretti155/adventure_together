package com.lucamoretti.adventure_together.service.trip;

import com.lucamoretti.adventure_together.dto.trip.TripItineraryDayDTO;
import java.util.List;

/*
 Interfaccia per la gestione dei giorni dell'itinerario di viaggio
 Definisce i metodi per creare, aggiornare, eliminare e recuperare i giorni
 La implementazione concreta gestirà la logica di business e l'interazione con il database
*/


public interface TripItineraryDayService {

    TripItineraryDayDTO createDay(Long itineraryId, TripItineraryDayDTO dto);
    TripItineraryDayDTO updateDay(Long id, TripItineraryDayDTO dto);
    void deleteDay(Long id);
    List<TripItineraryDayDTO> getDaysByItinerary(Long itineraryId);
}

