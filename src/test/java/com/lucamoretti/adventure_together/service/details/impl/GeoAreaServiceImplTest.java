package com.lucamoretti.adventure_together.service.details.impl;

import com.lucamoretti.adventure_together.dto.details.GeoAreaDTO;
import com.lucamoretti.adventure_together.model.details.GeoArea;
import com.lucamoretti.adventure_together.repository.details.GeoAreaRepository;
import com.lucamoretti.adventure_together.util.exception.DuplicateResourceException;
import com.lucamoretti.adventure_together.util.exception.ResourceNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GeoAreaServiceImplTest {

    @Mock
    private GeoAreaRepository geoAreaRepository;

    @InjectMocks
    private GeoAreaServiceImpl service;


    // ------------------------------------------------------------
    //                        CREATE
    // ------------------------------------------------------------

    @Test
    void createGeoArea_success() {

        GeoAreaDTO dto = GeoAreaDTO.builder()
                .geoArea("Europa")
                .build();

        when(geoAreaRepository.existsByGeoAreaIgnoreCase("Europa"))
                .thenReturn(false);

        GeoArea saved = new GeoArea();
        saved.setId(10L);
        saved.setGeoArea("Europa");

        when(geoAreaRepository.save(any())).thenReturn(saved);

        GeoAreaDTO result = service.createGeoArea(dto);

        assertNotNull(result);
        assertEquals(10L, result.getId());
        assertEquals("Europa", result.getGeoArea());
        verify(geoAreaRepository).save(any());
    }

    @Test
    void createGeoArea_duplicate_throwsException() {

        GeoAreaDTO dto = GeoAreaDTO.builder()
                .geoArea("Asia")
                .build();

        when(geoAreaRepository.existsByGeoAreaIgnoreCase("Asia"))
                .thenReturn(true);

        assertThrows(DuplicateResourceException.class,
                () -> service.createGeoArea(dto));

        verify(geoAreaRepository, never()).save(any());
    }


    // ------------------------------------------------------------
    //                     GET ALL GEO AREAS
    // ------------------------------------------------------------

    @Test
    void getAllGeoAreas_success() {

        GeoArea g1 = new GeoArea(1L, "Europa");
        GeoArea g2 = new GeoArea(2L, "Asia");

        when(geoAreaRepository.findAll())
                .thenReturn(List.of(g1, g2));

        List<GeoAreaDTO> result = service.getAllGeoAreas();

        assertEquals(2, result.size());
        assertEquals("Europa", result.get(0).getGeoArea());
        assertEquals("Asia", result.get(1).getGeoArea());
    }


    // ------------------------------------------------------------
    //                       GET BY ID
    // ------------------------------------------------------------

    @Test
    void getGeoAreaById_success() {

        GeoArea g = new GeoArea(5L, "Oceania");

        when(geoAreaRepository.findById(5L))
                .thenReturn(Optional.of(g));

        GeoAreaDTO result = service.getGeoAreaById(5L);

        assertEquals(5L, result.getId());
        assertEquals("Oceania", result.getGeoArea());
    }

    @Test
    void getGeoAreaById_notFound_throwsException() {
        when(geoAreaRepository.findById(99L))
                .thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> service.getGeoAreaById(99L));
    }
}
