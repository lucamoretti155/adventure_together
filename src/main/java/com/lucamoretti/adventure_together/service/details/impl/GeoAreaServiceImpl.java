package com.lucamoretti.adventure_together.service.details.impl;

import com.lucamoretti.adventure_together.dto.details.GeoAreaDTO;
import com.lucamoretti.adventure_together.model.details.GeoArea;
import com.lucamoretti.adventure_together.repository.details.GeoAreaRepository;
import com.lucamoretti.adventure_together.service.details.GeoAreaService;
import com.lucamoretti.adventure_together.util.exception.DuplicateResourceException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

// Implementazione del servizio di gestione delle aree geografiche

@Service
@RequiredArgsConstructor
public class GeoAreaServiceImpl implements GeoAreaService {

    private final GeoAreaRepository geoAreaRepository;

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
}
