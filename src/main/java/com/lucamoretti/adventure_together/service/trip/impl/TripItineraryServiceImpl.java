package com.lucamoretti.adventure_together.service.trip.impl;

import com.lucamoretti.adventure_together.dto.trip.TripItineraryDTO;
import com.lucamoretti.adventure_together.dto.trip.TripItineraryDayDTO;
import com.lucamoretti.adventure_together.model.details.Category;
import com.lucamoretti.adventure_together.model.details.Country;
import com.lucamoretti.adventure_together.model.details.DepartureAirport;
import com.lucamoretti.adventure_together.model.trip.TripItinerary;
import com.lucamoretti.adventure_together.repository.details.CategoryRepository;
import com.lucamoretti.adventure_together.repository.details.CountryRepository;
import com.lucamoretti.adventure_together.repository.details.DepartureAirportRepository;
import com.lucamoretti.adventure_together.repository.trip.TripItineraryRepository;
import com.lucamoretti.adventure_together.repository.user.PlannerRepository;
import com.lucamoretti.adventure_together.service.trip.TripItineraryService;
import com.lucamoretti.adventure_together.util.exception.DataIntegrityException;
import com.lucamoretti.adventure_together.util.exception.DuplicateResourceException;
import com.lucamoretti.adventure_together.util.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
/*
  Implementazione del servizio per la gestione degli itinerari di viaggio.
  Fornisce metodi per creare, aggiornare, eliminare e recuperare itinerari di viaggio,
  nonché per cercare itinerari in base a criteri specifici come paese, area geografica e categorie.
 */

@Service
@RequiredArgsConstructor
@Transactional
public class TripItineraryServiceImpl implements TripItineraryService {

    private final TripItineraryRepository itineraryRepository;
    private final PlannerRepository plannerRepository;
    private final CountryRepository countryRepository;
    private final CategoryRepository categoryRepository;
    private final DepartureAirportRepository airportRepository;

    // metodo per creare un itinerario di viaggio
    @Override
    public TripItineraryDTO createItinerary(TripItineraryDTO dto) {

        // Controllo unicità titolo
        if (itineraryRepository.existsByTitle(dto.getTitle())) {
            throw new DuplicateResourceException("Il titolo dell'itinerario esiste già");
        }
        // Controllo integrità minimo/max partecipanti
        if (dto.getMinParticipants() > dto.getMaxParticipants()) {
            throw new DataIntegrityException("Il minimo partecipanti non può essere maggiore del massimo partecipanti");
        }
        // Controllo integrità durata in giorni
        if (dto.getDurationInDays() <= 0) {
            throw new DataIntegrityException("Durata in giorni deve essere maggiore di zero");
        }

        // Creazione dell'entità dall'DTO
        TripItinerary entity = dto.toEntity();

        // Set del planner associato all'itinerario
        // fa il check se il planner esiste, altrimenti lancia un'eccezione
        // viene usato il var perchè il tipo viene dedotto automaticamente dal compilatore (potrebbe essere sia planner che admin)
        var planner = plannerRepository.findById(dto.getPlannerId())
                .orElseThrow(() -> new ResourceNotFoundException("Planner", "id", dto.getPlannerId()));
        entity.setPlanner(planner);

        // Set della/delle country associata/e all'itinerario
        // Vengono usati i metodi "resolve..." di supporto per risolvere le relazioni e validare gli ID
        // i metodi sono definiti alla fine della classe

        entity.setCountries(resolveCountries(dto.getCountryIds()));
        entity.setCategories(resolveCategories(dto.getCategoryIds()));
        entity.setDepartureAirports(resolveAirports(dto.getDepartureAirportIds()));

        // Set dei giorni dell'itinerario
        // Se sono forniti nel DTO, vengono convertiti in entità e associati all'itinerario
        if (dto.getDays() != null && !dto.getDays().isEmpty()) {
            var dayEntities = dto.getDays().stream()
                    .map(TripItineraryDayDTO::toEntity)
                    .peek(d -> d.setTripItinerary(entity)) // back reference
                    .collect(Collectors.toSet());
            entity.setDays(dayEntities);
        }

        // Salvataggio dell'entità e ritorno del DTO corrispondente
        TripItinerary saved = itineraryRepository.save(entity);
        return TripItineraryDTO.fromEntity(saved);
    }
    // metodo per aggiornare un itinerario di viaggio
    @Override
    public TripItineraryDTO updateItinerary(Long id, TripItineraryDTO dto) {
        TripItinerary itinerary = itineraryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("TripItinerary", "id", id));

        // Controllo unicità titolo (escludendo l'itinerario corrente)
        // Se il titolo è stato modificato e il nuovo titolo esiste già, lancia eccezione
        // controlla se il titolo dell'itinerario esistente è diverso da quello nel DTO
        // se è diverso, controlla se esiste già un itinerario con quel titolo
        if (!itinerary.getTitle().equals(dto.getTitle()) &&
                itineraryRepository.existsByTitle(dto.getTitle())) {
            throw new DuplicateResourceException("Itinerary title already exists");
        }

        // Controllo integrità minimo/max partecipanti
        if (dto.getMinParticipants() > dto.getMaxParticipants()) {
            throw new DataIntegrityException("Il minimo partecipanti non può essere maggiore del massimo partecipanti");
        }
        // Aggiornamento campi semplici
        itinerary.setTitle(dto.getTitle());
        itinerary.setDescription(dto.getDescription());
        itinerary.setPicturePath(dto.getPicturePath());
        itinerary.setDurationInDays(dto.getDurationInDays());
        itinerary.setMinParticipants(dto.getMinParticipants());
        itinerary.setMaxParticipants(dto.getMaxParticipants());

        // Aggiornamento relazioni (solo se forniti nuovi ID)
        // e.g. casistica di aggiornamento solo del contenuto del viaggio senza cambiare Country, Category o DepartureAirport
        if (dto.getCountryIds() != null)
            itinerary.setCountries(resolveCountries(dto.getCountryIds()));
        if (dto.getCategoryIds() != null)
            itinerary.setCategories(resolveCategories(dto.getCategoryIds()));
        if (dto.getDepartureAirportIds() != null)
            itinerary.setDepartureAirports(resolveAirports(dto.getDepartureAirportIds()));

        // Aggiornamento giorni
        // Se i giorni sono forniti nel DTO, sostituisci quelli esistenti
        if (dto.getDays() != null) {
            // 1. Svuota i giorni attuali (grazie a orphanRemoval = true verranno eliminati)
            itinerary.getDays().clear();

            // 2. Ricrea i giorni dal DTO e ristabilisci la relazione inversa
            dto.getDays().forEach(dayDto -> {
                var dayEntity = dayDto.toEntity();
                dayEntity.setTripItinerary(itinerary);
                itinerary.getDays().add(dayEntity);
            });
        }

        return TripItineraryDTO.fromEntity(itineraryRepository.save(itinerary));
    }

    // metodo per eliminare un itinerario di viaggio
    @Override
    public void deleteItinerary(Long id) {
        TripItinerary entity = itineraryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("TripItinerary", "id", id));
        itineraryRepository.delete(entity);
    }

    // metodo per recuperare un itinerario di viaggio tramite id
    @Override
    @Transactional(readOnly = true)
    public TripItineraryDTO getById(Long id) {
        return itineraryRepository.findById(id)
                .map(TripItineraryDTO::fromEntity)
                .orElseThrow(() -> new ResourceNotFoundException("TripItinerary", "id", id));
    }
    // metodo per recuperare tutti gli itinerari di viaggio
    @Override
    @Transactional(readOnly = true)
    public List<TripItineraryDTO> getAll() {
        return itineraryRepository.findAll().stream()
                .map(TripItineraryDTO::fromEntity)
                .toList();
    }
    // metodo per cercare itinerari di viaggio in base al paese
    @Override
    @Transactional(readOnly = true)
    public List<TripItineraryDTO> getAllByCountryId(Long countryId) {
        return itineraryRepository.findByCountry(countryId).stream()
                .map(TripItineraryDTO::fromEntity)
                .toList();
    }
    // metodo per cercare itinerari di viaggio in base all'area geografica
    @Override
    @Transactional(readOnly = true)
    public List<TripItineraryDTO> getAllByGeoAreaId(Long geoAreaId) {
        return itineraryRepository.findByGeoArea(geoAreaId).stream()
                .map(TripItineraryDTO::fromEntity)
                .toList();
    }
    // metodo per cercare itinerari di viaggio in base alla categoria
    @Override
    @Transactional(readOnly = true)
    public List<TripItineraryDTO> getAllByCategoryId(Long categoryId) {
        return itineraryRepository.findByCategory(categoryId).stream()
                .map(TripItineraryDTO::fromEntity)
                .toList();
    }
    // metodo per cercare itinerari di viaggio in base a più categorie
    @Override
    @Transactional(readOnly = true)
    public List<TripItineraryDTO> getAllByCategoryIds(List<Long> categoryIds) {
        return itineraryRepository.findByCategories(categoryIds).stream()
                .map(TripItineraryDTO::fromEntity)
                .toList();
    }

    // metodi di supporto per la validazione e risoluzione delle relazioni con Country, Category, DepartureAirport
    // dato un set di ID, restituisce il set delle entità corrispondenti
    // lancia eccezione se uno degli ID non esiste
    // usato nei metodi di create e update

    private Set<Country> resolveCountries(Set<Long> ids) {
        if (ids == null || ids.isEmpty()) return Set.of(); // ritorna set vuoto se null o vuoto
        return ids.stream()
                .map(id -> countryRepository.findById(id)
                        .orElseThrow(() -> new ResourceNotFoundException("Country", "id", id)))
                .collect(Collectors.toSet());
    }

    private Set<Category> resolveCategories(Set<Long> ids) {
        if (ids == null || ids.isEmpty()) return Set.of();
        return ids.stream()
                .map(id -> categoryRepository.findById(id)
                        .orElseThrow(() -> new ResourceNotFoundException("Category", "id", id)))
                .collect(Collectors.toSet());
    }

    private Set<DepartureAirport> resolveAirports(Set<Long> ids) {
        if (ids == null || ids.isEmpty()) return Set.of();
        return ids.stream()
                .map(id -> airportRepository.findById(id)
                        .orElseThrow(() -> new ResourceNotFoundException("DepartureAirport", "id", id)))
                .collect(Collectors.toSet());
    }


}
