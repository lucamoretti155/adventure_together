package com.lucamoretti.adventure_together.service.details;

import com.lucamoretti.adventure_together.dto.details.GeoAreaDTO;

import java.util.List;

// Interfaccia per il servizio di gestione delle aree geografiche

public interface GeoAreaService {
    GeoAreaDTO createGeoArea(GeoAreaDTO dto);
    List<GeoAreaDTO> getAllGeoAreas();
    GeoAreaDTO getGeoAreaById(Long id);
}
