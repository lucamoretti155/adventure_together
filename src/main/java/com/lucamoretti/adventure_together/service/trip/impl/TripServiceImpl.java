package com.lucamoretti.adventure_together.service.trip.impl;

import com.lucamoretti.adventure_together.dto.trip.TripDTO;
import com.lucamoretti.adventure_together.model.trip.Trip;
import com.lucamoretti.adventure_together.repository.trip.TripRepository;
import com.lucamoretti.adventure_together.repository.trip.TripItineraryRepository;
import com.lucamoretti.adventure_together.repository.user.PlannerRepository;
import com.lucamoretti.adventure_together.service.trip.TripService;
import com.lucamoretti.adventure_together.service.validation.DataValidationService;
import com.lucamoretti.adventure_together.util.exception.DataIntegrityException;
import com.lucamoretti.adventure_together.util.exception.ResourceNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.time.LocalDate;
import java.util.List;

/*
 Implementazione dell'interfaccia TripService per la gestione delle operazioni sui viaggi (Trip).
 Fornisce metodi per creare, gestire lo stato e recuperare viaggi.
*/

@Service
@RequiredArgsConstructor
@Transactional
public class TripServiceImpl implements TripService {

    private final TripRepository tripRepository;
    private final TripItineraryRepository itineraryRepository;
    private final PlannerRepository plannerRepository;
    private final DataValidationService dataValidationService;

    // Creazione di un nuovo Trip (planner)
    @Override
    public TripDTO createTrip(TripDTO dto) {
        //Validazione

        //Validazione date attraverso il servizio dedicato
        dataValidationService.validateTripDates(dto.getDateStartBookings(), dto.getDateEndBookings());
        dataValidationService.validateTripDates(dto.getDateDeparture(), dto.getDateReturn());
        // Controllo data di partenza non nel passato
        if (dto.getDateDeparture().isBefore(LocalDate.now()))
            throw new DataIntegrityException("La data di partenza non può essere nel passato");

        // Entità e associazioni

        // Creazione entità Trip da DTO
        Trip trip = dto.toEntity();
        // Associazione con itinerary
        var itinerary = itineraryRepository.findById(dto.getTripItineraryId())
                .orElseThrow(() -> new ResourceNotFoundException("TripItinerary", "id", dto.getTripItineraryId()));
        // Associazione con planner
        var planner = plannerRepository.findById(dto.getPlannerId())
                .orElseThrow(() -> new ResourceNotFoundException("Planner", "id", dto.getPlannerId()));

        trip.setTripItinerary(itinerary);
        trip.setPlanner(planner);

        // --- Stato iniziale ---
        trip.open(); // setta stato ToBeConfirmed

        Trip saved = tripRepository.save(trip);
        return TripDTO.fromEntity(saved);
    }

    // Gestione automatica dello stato attraverso il pattern State (scheduler o admin)
    @Override
    public TripDTO handleTrip(Long tripId) {
        Trip trip = tripRepository.findById(tripId)
                .orElseThrow(() -> new ResourceNotFoundException("Trip", "id", tripId));
        // delega alla logica del pattern State verificando le varie condizioni per passare allo stato successivo
        trip.handle();
        // Salvataggio delle modifiche allo stato
        tripRepository.save(trip);

        return TripDTO.fromEntity(trip);
    }

    // Cancellazione manuale del trip (admin)
    // viene fatto un check sullo stato attuale del trip che deve essere ToBeConfirmed
    // ma non viene cancellato il trip dal database
    @Override
    public TripDTO cancelTrip(Long tripId) {
        Trip trip = tripRepository.findById(tripId)
                .orElseThrow(() -> new ResourceNotFoundException("Trip", "id", tripId));

        // Permesso cancellare solo se in stato ToBeConfirmed
        if (!(trip.getState() instanceof com.lucamoretti.adventure_together.model.trip.state.ToBeConfirmed)) {
            throw new DataIntegrityException("Un viaggio può essere cancellato solo se si trova nello stato 'ToBeConfirmed'");
        }

        trip.cancel(); // lo stato eseguirà effettivamente la cancellazione
        tripRepository.save(trip);

        return TripDTO.fromEntity(trip);
    }


    // Query generiche

    @Override
    public List<TripDTO> getAll() {
        return tripRepository.findAll().stream()
                .map(TripDTO::fromEntity)
                .toList();
    }

    @Override
    public TripDTO getById(Long id) {
        return tripRepository.findById(id)
                .map(TripDTO::fromEntity)
                .orElseThrow(() -> new ResourceNotFoundException("Trip", "id", id));
    }

    @Override
    public List<TripDTO> getTripsByPlanner(Long plannerId) {
        return tripRepository.findByPlanner_Id(plannerId).stream()
                .map(TripDTO::fromEntity)
                .toList();
    }


    // Query specifiche per varie funzionalità dell'applicazione

    // Recupera tutti i trip ancora prenotabili (ToBeConfirmed + ConfirmedOpen)
    @Override
    public List<TripDTO> getBookableTrips() {
        return tripRepository.findOpenForBooking().stream()
                .filter(t -> t.getDateEndBookings().isAfter(LocalDate.now())) // sicurezza extra lato logico
                .map(TripDTO::fromEntity)
                .toList();
    }

    // Recupera tutti i Trip ancora in stato ToBeConfirmed
    @Override
    public List<TripDTO> getToBeConfirmedTrips() {
        return tripRepository.findByState(com.lucamoretti.adventure_together.model.trip.state.ToBeConfirmed.class).stream()
                .map(TripDTO::fromEntity)
                .toList();
    }

    // Recupera tutti i trip ancora prenotabili (ToBeConfirmed + ConfirmedOpen) per un TripItinerary specifico
    @Override
    public List<TripDTO> getBookableTripsByItinerary(Long itineraryId) {
        return tripRepository.findOpenForBookingByItinerary(itineraryId).stream()
                .filter(t -> t.getDateEndBookings().isAfter(LocalDate.now())) // sicurezza extra lato logico
                .map(TripDTO::fromEntity)
                .toList();
    }

    // Recupera tutti i trip prenotabili con partenza entro 30 giorni (per homepage)
    @Override
    public List<TripDTO> getUpcomingBookableTrips() {
        LocalDate today = LocalDate.now();
        LocalDate todayPlus30 = today.plusDays(30);
        return tripRepository.findUpcomingBookableTrips(today, todayPlus30).stream()
                .map(TripDTO::fromEntity)
                .toList();
    }
    // Recupera tutti i trip futuri
    @Override
    public List<TripDTO> getFutureTrips() {
        return tripRepository.findFutureTrips(LocalDate.now()).stream()
                .map(TripDTO::fromEntity)
                .toList();
    }
    // Recupera tutti i trip in uno stato specifico
    @Override
    public List<TripDTO> getTripsByState(Class<?> stateClass) {
        return tripRepository.findByState(stateClass).stream()
                .map(TripDTO::fromEntity)
                .toList();
    }
    // Recupera tutti i trip futuri non cancellati tra due date
    @Override
    public List<TripDTO> getTripsNotCancelledBetween(LocalDate from, LocalDate to) {
        return tripRepository.findByDateDepartureBetweenNotCancelled(from, to).stream()
                .map(TripDTO::fromEntity)
                .toList();
    }

    // Conta il numero di partecipanti a un trip
    @Override
    public int countParticipants(Long tripId) {
        Trip trip = tripRepository.findById(tripId)
                .orElseThrow(() -> new ResourceNotFoundException("Trip", "id", tripId));
        return trip.getCurrentParticipantsCount();
    }


}
