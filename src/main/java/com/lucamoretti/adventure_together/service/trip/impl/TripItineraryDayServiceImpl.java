package com.lucamoretti.adventure_together.service.trip.impl;

import com.lucamoretti.adventure_together.dto.trip.TripItineraryDayDTO;
import com.lucamoretti.adventure_together.model.trip.TripItinerary;
import com.lucamoretti.adventure_together.model.trip.TripItineraryDay;
import com.lucamoretti.adventure_together.repository.trip.TripItineraryDayRepository;
import com.lucamoretti.adventure_together.repository.trip.TripItineraryRepository;
import com.lucamoretti.adventure_together.service.trip.TripItineraryDayService;
import com.lucamoretti.adventure_together.util.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
/*
 Implementazione del servizio per la gestione dei giorni dell'itinerario di viaggio
 Fornisce metodi per creare, aggiornare, eliminare e recuperare i giorni
 Utilizza repository per interagire con il database e gestisce le eccezioni
 Viene usata l'annotazione @Transactional per garantire la coerenza delle operazioni (atomicità)
 (ad esempio, rollback in caso di errori)
*/

@Service
@RequiredArgsConstructor
@Transactional
public class TripItineraryDayServiceImpl implements TripItineraryDayService {

    private final TripItineraryDayRepository dayRepository;
    private final TripItineraryRepository itineraryRepository;

    // Metodo per creare un nuovo giorno nell'itinerario
    // Effettua il controllo per evitare duplicati basati sul numero del giorno
    // Se l'itinerario non esiste, lancia una ResourceNotFoundException
    // Se il giorno esiste già, lancia un'IllegalArgumentException
    @Override
    public TripItineraryDayDTO createDay(Long itineraryId, TripItineraryDayDTO dto) {
        TripItinerary itinerary = itineraryRepository.findById(itineraryId)
                .orElseThrow(() -> new ResourceNotFoundException("TripItinerary", "id", itineraryId));

        if (dayRepository.existsByTripItinerary_IdAndDayNumber(itineraryId, dto.getDayNumber())) {
            throw new IllegalArgumentException(
                    "Day " + dto.getDayNumber() + " already exists for itinerary " + itineraryId);
        }

        TripItineraryDay entity = dto.toEntity();
        entity.setTripItinerary(itinerary);
        TripItineraryDay saved = dayRepository.save(entity);

        return TripItineraryDayDTO.fromEntity(saved);
    }
    // Metodo per aggiornare un giorno esistente nell'itinerario
    // Se il giorno non esiste, lancia una ResourceNotFoundException
    // Aggiorna i campi titolo, descrizione e numero del giorno
    // Restituisce il DTO aggiornato
    @Override
    public TripItineraryDayDTO updateDay(Long id, TripItineraryDayDTO dto) {
        TripItineraryDay entity = dayRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("TripItineraryDay", "id", id));

        entity.setTitle(dto.getTitle());
        entity.setDescription(dto.getDescription());
        entity.setDayNumber(dto.getDayNumber());

        return TripItineraryDayDTO.fromEntity(dayRepository.save(entity));
    }
    // Metodo per eliminare un giorno dall'itinerario
    // Se il giorno non esiste, lancia una ResourceNotFoundException
    // Elimina l'entità dal repository
    @Override
    public void deleteDay(Long id) {
        TripItineraryDay entity = dayRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("TripItineraryDay", "id", id));
        dayRepository.delete(entity);
    }
    // Metodo per ottenere tutti i giorni di un itinerario specifico
    // Restituisce una lista di DTO ordinati per numero del giorno
    // Se l'itinerario non esiste, restituisce una lista vuota
    @Override
    public List<TripItineraryDayDTO> getDaysByItinerary(Long itineraryId) {
        return dayRepository.findByTripItinerary_IdOrderByDayNumberAsc(itineraryId)
                .stream()
                .map(TripItineraryDayDTO::fromEntity)
                .toList();
    }
}

