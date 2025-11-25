package com.lucamoretti.adventure_together.service.details.impl;

import com.lucamoretti.adventure_together.dto.details.DepartureAirportDTO;
import com.lucamoretti.adventure_together.model.details.DepartureAirport;
import com.lucamoretti.adventure_together.repository.details.DepartureAirportRepository;
import com.lucamoretti.adventure_together.util.exception.DuplicateResourceException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DepartureAirportServiceImplTest {

    @Mock
    private DepartureAirportRepository departureAirportRepository;

    @InjectMocks
    private DepartureAirportServiceImpl service;

    // --------------------------------------------------------------------
    //                           CREATE
    // --------------------------------------------------------------------

    @Test
    void createDepartureAirport_success() {
        DepartureAirportDTO dto = DepartureAirportDTO.builder()
                .code("MXP")
                .name("Malpensa")
                .city("Milano")
                .build();

        DepartureAirport saved = new DepartureAirport();
        saved.setId(10L);
        saved.setCode("MXP");
        saved.setName("Malpensa");
        saved.setCity("Milano");

        when(departureAirportRepository.existsByCodeIgnoreCase("MXP"))
                .thenReturn(false);

        when(departureAirportRepository.save(any()))
                .thenReturn(saved);

        DepartureAirportDTO result = service.createDepartureAirport(dto);

        assertNotNull(result);
        assertEquals(10L, result.getId());
        assertEquals("MXP", result.getCode());
        assertEquals("Malpensa", result.getName());
        assertEquals("Milano", result.getCity());

        verify(departureAirportRepository).save(any());
    }

    @Test
    void createDepartureAirport_duplicate_throwsException() {
        DepartureAirportDTO dto = DepartureAirportDTO.builder()
                .code("MXP")
                .name("Malpensa")
                .city("Milano")
                .build();

        when(departureAirportRepository.existsByCodeIgnoreCase("MXP"))
                .thenReturn(true);

        assertThrows(DuplicateResourceException.class,
                () -> service.createDepartureAirport(dto));

        verify(departureAirportRepository, never()).save(any());
    }


    // --------------------------------------------------------------------
    //                GET ALL AIRPORTS (sorted)
    // --------------------------------------------------------------------

    @Test
    void getAllDepartureAirports_sortedAlphabetically() {
        DepartureAirport a1 = new DepartureAirport(1L, "zrh", "Zurich Airport", "Zurigo");
        DepartureAirport a2 = new DepartureAirport(2L, "AMS", "Amsterdam Schiphol", "Amsterdam");
        DepartureAirport a3 = new DepartureAirport(3L, "bcn", "Barcelona El Prat", "Barcellona");

        when(departureAirportRepository.findAll())
                .thenReturn(List.of(a1, a2, a3));

        List<DepartureAirportDTO> result = service.getAllDepartureAirports();

        assertEquals(3, result.size());
        assertEquals("AMS", result.get(0).getCode());
        assertEquals("bcn", result.get(1).getCode());
        assertEquals("zrh", result.get(2).getCode());
    }


    // --------------------------------------------------------------------
    //         GET AIRPORTS BY SET OF IDS
    // --------------------------------------------------------------------

    @Test
    void getDepartureAirportsBySetOfIds_success() {

        DepartureAirport a1 = new DepartureAirport(1L, "MXP", "Malpensa", "Milano");
        DepartureAirport a2 = new DepartureAirport(2L, "LIN", "Linate", "Milano");

        when(departureAirportRepository.findAllByIdIn(Set.of(1L, 2L)))
                .thenReturn(List.of(a1, a2));

        List<DepartureAirportDTO> result =
                service.getDepartureAirportsBySetOfIds(Set.of(1L, 2L));

        assertEquals(2, result.size());
        assertTrue(result.stream().anyMatch(a -> a.getCode().equals("MXP")));
        assertTrue(result.stream().anyMatch(a -> a.getCode().equals("LIN")));
    }


    // --------------------------------------------------------------------
    //                     GET BY ID
    // --------------------------------------------------------------------

    @Test
    void getDepartureAirportById_success() {
        DepartureAirport a = new DepartureAirport(50L, "FCO", "Fiumicino", "Roma");

        when(departureAirportRepository.findById(50L))
                .thenReturn(Optional.of(a));

        DepartureAirportDTO dto = service.getDepartureAirportById(50L);

        assertEquals(50L, dto.getId());
        assertEquals("FCO", dto.getCode());
        assertEquals("Fiumicino", dto.getName());
    }

    @Test
    void getDepartureAirportById_notFound_throwsException() {
        when(departureAirportRepository.findById(999L))
                .thenReturn(Optional.empty());

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> service.getDepartureAirportById(999L));

        assertTrue(ex.getMessage().contains("Aeroporto di partenza non trovato"));
    }
}

