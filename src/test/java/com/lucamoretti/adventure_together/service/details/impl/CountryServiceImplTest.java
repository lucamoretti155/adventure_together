package com.lucamoretti.adventure_together.service.details.impl;

import com.lucamoretti.adventure_together.dto.details.CountryDTO;
import com.lucamoretti.adventure_together.model.details.Country;
import com.lucamoretti.adventure_together.model.details.GeoArea;
import com.lucamoretti.adventure_together.repository.details.CountryRepository;
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
class CountryServiceImplTest {

    @Mock private CountryRepository countryRepository;
    @Mock private GeoAreaRepository geoAreaRepository;

    @InjectMocks
    private CountryServiceImpl service;

    // ----------------------------------------------------------------------
    //                              CREATE
    // ----------------------------------------------------------------------

    @Test
    void createCountry_success() {

        CountryDTO dto = CountryDTO.builder()
                .country("Islanda")
                .geoAreaId(5L)
                .build(
        );

        GeoArea geo = new GeoArea();
        geo.setId(5L);
        geo.setGeoArea("Europa");

        Country saved = new Country();
        saved.setId(100L);
        saved.setCountry("Islanda");
        saved.setGeoArea(geo);

        when(countryRepository.existsByCountryIgnoreCase("Islanda"))
                .thenReturn(false);

        when(geoAreaRepository.findById(5L))
                .thenReturn(Optional.of(geo));

        when(countryRepository.save(any()))
                .thenReturn(saved);

        CountryDTO result = service.createCountry(dto);

        assertNotNull(result);
        assertEquals(100L, result.getId());
        assertEquals("Islanda", result.getCountry());
        assertEquals(5L, result.getGeoAreaId());

        verify(countryRepository).save(any());
    }

    @Test
    void createCountry_duplicate_throwsException() {
        CountryDTO dto = CountryDTO.builder()
                .country("Italia")
                .geoAreaId(1L)
                .build();;

        when(countryRepository.existsByCountryIgnoreCase("Italia"))
                .thenReturn(true);

        assertThrows(DuplicateResourceException.class,
                () -> service.createCountry(dto));

        verify(countryRepository, never()).save(any());
    }

    @Test
    void createCountry_geoAreaNotFound_throwsException() {
        CountryDTO dto = CountryDTO.builder()
                .country("Giappone")
                .geoAreaId(99L)
                .build();;;

        when(countryRepository.existsByCountryIgnoreCase("Giappone"))
                .thenReturn(false);

        when(geoAreaRepository.findById(99L))
                .thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> service.createCountry(dto));
    }


    // ----------------------------------------------------------------------
    //                          GET ALL COUNTRIES
    // ----------------------------------------------------------------------

    @Test
    void getAllCountries_sortedAlphabetically() {

        GeoArea g = new GeoArea();
        g.setId(1L);

        Country c1 = new Country(1L, "zeta", g);
        Country c2 = new Country(2L, "Alfa", g);
        Country c3 = new Country(3L, "beta", g);

        when(countryRepository.findAll())
                .thenReturn(List.of(c1, c2, c3));

        List<CountryDTO> result = service.getAllCountries();

        assertEquals(3, result.size());
        assertEquals("Alfa", result.get(0).getCountry());
        assertEquals("beta", result.get(1).getCountry());
        assertEquals("zeta", result.get(2).getCountry());
    }


    // ----------------------------------------------------------------------
    //                  GET ALL BY GEOAREA
    // ----------------------------------------------------------------------

    @Test
    void getAllCountriesByGeoAreaId_success() {
        GeoArea g = new GeoArea();
        g.setId(10L);

        Country c1 = new Country(1L, "Italia", g);
        Country c2 = new Country(2L, "Francia", g);

        when(countryRepository.findAllByGeoAreaId(10L))
                .thenReturn(List.of(c1, c2));

        List<CountryDTO> result = service.getAllCountriesByGeoAreaId(10L);

        assertEquals(2, result.size());
        assertEquals("Italia", result.get(0).getCountry());
        assertEquals("Francia", result.get(1).getCountry());
    }


    // ----------------------------------------------------------------------
    //                          GET BY ID
    // ----------------------------------------------------------------------

    @Test
    void getCountryById_success() {
        GeoArea g = new GeoArea(1L, "Europa");
        Country c = new Country(50L, "Norvegia", g);

        when(countryRepository.findById(50L))
                .thenReturn(Optional.of(c));

        CountryDTO dto = service.getCountryById(50L);

        assertEquals(50L, dto.getId());
        assertEquals("Norvegia", dto.getCountry());
    }

    @Test
    void getCountryById_notFound_throwsException() {
        when(countryRepository.findById(999L))
                .thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> service.getCountryById(999L));
    }


    // ----------------------------------------------------------------------
    //                         GET BY SET OF IDS
    // ----------------------------------------------------------------------

    @Test
    void getCountryBySetOfId_success() {
        GeoArea g = new GeoArea(1L, "Europa");

        Country c1 = new Country(1L, "Italia", g);
        Country c2 = new Country(2L, "Spagna", g);

        when(countryRepository.findAllByIdIn(Set.of(1L, 2L)))
                .thenReturn(List.of(c1, c2));

        List<CountryDTO> result = service.getCountryBySetOfId(Set.of(1L, 2L));

        assertEquals(2, result.size());
        assertTrue(result.stream().anyMatch(x -> x.getCountry().equals("Italia")));
        assertTrue(result.stream().anyMatch(x -> x.getCountry().equals("Spagna")));
    }
}
