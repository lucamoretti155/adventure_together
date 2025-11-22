package com.lucamoretti.adventure_together.service.trip;

import com.lucamoretti.adventure_together.dto.trip.TripItineraryDTO;
import java.util.List;

/*
  Interfaccia per il servizio di gestione degli itinerari di viaggio.
  Fornisce metodi per creare, aggiornare, eliminare e recuperare itinerari di viaggio,
  nonché per cercare itinerari in base a criteri specifici come paese, area geografica e categorie.
 */

public interface TripItineraryService {

    TripItineraryDTO createItinerary(TripItineraryDTO dto);
    TripItineraryDTO updateItinerary(Long id, TripItineraryDTO dto);
    void deleteItinerary(Long id);

    TripItineraryDTO getById(Long id);
    TripItineraryDTO getByTitle(String title);
    List<TripItineraryDTO> getAll();
    List<TripItineraryDTO> getByPlannerId(Long id);

    List<TripItineraryDTO> getAllByCountryId(Long countryId);
    List<TripItineraryDTO> getAllByGeoAreaId(Long geoAreaId);
    List<TripItineraryDTO> getAllByCategoryId(Long categoryId);
    List<TripItineraryDTO> getAllByCategoryIds(List<Long> categoryIds);
}
