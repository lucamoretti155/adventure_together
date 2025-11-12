package com.lucamoretti.adventure_together.service.details.impl;

import com.lucamoretti.adventure_together.dto.details.DepartureAirportDTO;
import com.lucamoretti.adventure_together.model.details.DepartureAirport;
import com.lucamoretti.adventure_together.repository.details.DepartureAirportRepository;
import com.lucamoretti.adventure_together.service.details.DepartureAirportService;
import com.lucamoretti.adventure_together.util.exception.DuplicateResourceException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;

// Implementazione del servizio per la gestione degli aeroporti di partenza

@Service
@RequiredArgsConstructor
public class DepartureAirportServiceImpl implements DepartureAirportService {

    private final DepartureAirportRepository departureAirportRepository;

    // Creazione di un nuovo aeroporto di partenza
    @Override
    @Transactional
    public DepartureAirportDTO createDepartureAirport(DepartureAirportDTO dto) {
        if (departureAirportRepository.existsByCodeIgnoreCase(dto.getCode())) {
            throw new DuplicateResourceException("Il codice aeroporto già esiste " + dto.getCode());
        }

        DepartureAirport airport = new DepartureAirport();
        airport.setCode(dto.getCode());
        airport.setName(dto.getName());
        airport.setCity(dto.getCity());

        DepartureAirport saved = departureAirportRepository.save(airport);
        return DepartureAirportDTO.fromEntity(saved);
    }

    // Recupero di tutti gli aeroporti di partenza
    @Override
    public List<DepartureAirportDTO> getAllDepartureAirports() {
        List<DepartureAirport> airports = departureAirportRepository.findAll();
        return airports.stream()
                .map(DepartureAirportDTO::fromEntity)
                .toList();
    }

    // Recupero di aeroporti di partenza in base a un insieme di ID
    @Override
    public List<DepartureAirportDTO> getDepartureAirportsBySetOfIds(Set<Long> ids) {
        List<DepartureAirport> airports = departureAirportRepository.findAllByIdIn(ids);
        return airports.stream()
                .map(DepartureAirportDTO::fromEntity)
                .toList();
    }
}
