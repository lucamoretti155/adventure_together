package com.lucamoretti.adventure_together.service.details.impl;

import com.lucamoretti.adventure_together.dto.details.GeoAreaDTO;
import com.lucamoretti.adventure_together.model.details.GeoArea;
import com.lucamoretti.adventure_together.repository.details.GeoAreaRepository;
import com.lucamoretti.adventure_together.service.details.GeoAreaService;
import com.lucamoretti.adventure_together.util.exception.DuplicateResourceException;
import com.lucamoretti.adventure_together.util.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

// Implementazione del servizio di gestione delle aree geografiche

@Service
@RequiredArgsConstructor
public class GeoAreaServiceImpl implements GeoAreaService {

    private final GeoAreaRepository geoAreaRepository;

    // Creazione di una nuova area geografica
    @Override
    @Transactional
    public GeoAreaDTO createGeoArea(GeoAreaDTO dto) {
        if (geoAreaRepository.existsByGeoAreaIgnoreCase(dto.getGeoArea())) {
            throw new DuplicateResourceException("La GeoArea esiste già: " + dto.getGeoArea());
        }

        GeoArea geoArea = new GeoArea();
        geoArea.setGeoArea(dto.getGeoArea());

        GeoArea saved = geoAreaRepository.save(geoArea);
        return GeoAreaDTO.fromEntity(saved);
    }

    // Recupero di tutte le aree geografiche
    @Override
    @Transactional (readOnly = true)
    public List<GeoAreaDTO> getAllGeoAreas() {
        List<GeoArea> geoAreas = geoAreaRepository.findAll();
        return geoAreas.stream()
                .map(GeoAreaDTO::fromEntity)
                .toList();
    }

    // Recupero di un'area geografica per ID
    @Override
    @Transactional (readOnly = true)
    public GeoAreaDTO getGeoAreaById(Long id) {
        GeoArea geoArea = geoAreaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("GeoArea", "id", id));
        return GeoAreaDTO.fromEntity(geoArea);
    }
}
