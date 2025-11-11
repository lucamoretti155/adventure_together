package com.lucamoretti.adventure_together.service.trip;

import com.lucamoretti.adventure_together.dto.trip.TripItineraryDTO;
import java.util.List;

/**
  Interfaccia per il servizio di gestione degli itinerari di viaggio.
  Fornisce metodi per creare, aggiornare, eliminare e recuperare itinerari di viaggio,
  nonché per cercare itinerari in base a criteri specifici come paese, area geografica e categorie.
 */

public interface TripItineraryService {

    TripItineraryDTO createItinerary(TripItineraryDTO dto);
    TripItineraryDTO updateItinerary(Long id, TripItineraryDTO dto);
    void deleteItinerary(Long id);

    TripItineraryDTO getById(Long id);
    List<TripItineraryDTO> getAll();

    List<TripItineraryDTO> findByCountry(Long countryId);
    List<TripItineraryDTO> findByGeoArea(Long geoAreaId);
    List<TripItineraryDTO> findByCategory(Long categoryId);
    List<TripItineraryDTO> findByCategories(List<Long> categoryIds);
}
