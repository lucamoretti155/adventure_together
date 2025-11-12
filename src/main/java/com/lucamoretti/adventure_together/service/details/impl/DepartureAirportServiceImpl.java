package com.lucamoretti.adventure_together.service.details.impl;

import com.lucamoretti.adventure_together.dto.details.DepartureAirportDTO;
import com.lucamoretti.adventure_together.model.details.DepartureAirport;
import com.lucamoretti.adventure_together.repository.details.DepartureAirportRepository;
import com.lucamoretti.adventure_together.service.details.DepartureAirportService;
import com.lucamoretti.adventure_together.util.exception.DuplicateResourceException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

// Implementazione del servizio per la gestione degli aeroporti di partenza

@Service
@RequiredArgsConstructor
public class DepartureAirportServiceImpl implements DepartureAirportService {

    private final DepartureAirportRepository departureAirportRepository;

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
}
