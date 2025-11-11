package com.lucamoretti.adventure_together.service.trip;

import com.lucamoretti.adventure_together.dto.trip.TripDTO;
import java.time.LocalDate;
import java.util.List;
/*
 Interfaccia del servizio TripService per la gestione delle operazioni sui viaggi (Trip).
 Fornisce metodi per creare, gestire lo stato e recuperare viaggi.
*/

public interface TripService {

    // Creazione di un nuovo Trip (planner)
    TripDTO createTrip(TripDTO dto);

    // Aggiornamento automatico dello stato (scheduler o admin)
    TripDTO handleTrip(Long tripId);

    // Cancellazione manuale (admin)
    TripDTO cancelTrip(Long tripId);

    // Recupera tutti i trip
    List<TripDTO> getAll();

    // Recupera un trip per id
    TripDTO getById(Long id);

    // Recupera tutti i trip associati a un planner specifico
    List<TripDTO> getTripsByPlanner(Long plannerId);

    // Recupera tutti i trip ancora prenotabili (ToBeConfirmed + ConfirmedOpen)
    List<TripDTO> getBookableTrips();

    // Recupera tutti i trip prenotabili con partenza entro 30 giorni (per homepage)
    List<TripDTO> getUpcomingBookableTrips();

    // Recupera tutti i trip futuri
    List<TripDTO> getFutureTrips();

    // Recupera tutti i trip in uno stato specifico
    List<TripDTO> getTripsByState(Class<?> stateClass);

    // Recupera tutti i trip futuri non cancellati tra due date
    List<TripDTO> getTripsNotCancelledBetween(LocalDate from, LocalDate to);
}


