package com.lucamoretti.adventure_together.service.trip.impl;

import com.lucamoretti.adventure_together.model.trip.TripItineraryDay;
import com.lucamoretti.adventure_together.util.exception.ResourceNotFoundException;
import com.lucamoretti.adventure_together.dto.trip.TripItineraryDayDTO;
import com.lucamoretti.adventure_together.model.trip.TripItinerary;

import com.lucamoretti.adventure_together.repository.trip.TripItineraryDayRepository;
import com.lucamoretti.adventure_together.repository.trip.TripItineraryRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TripItineraryDayServiceImplTest {

    @Mock private TripItineraryDayRepository dayRepository;
    @Mock private TripItineraryRepository itineraryRepository;

    @InjectMocks
    private TripItineraryDayServiceImpl service;

    // --------------------------------------------------------------
    //                            CREATE
    // --------------------------------------------------------------

    @Test
    void createDay_success() {
        Long itineraryId = 100L;
        TripItinerary itinerary = new TripItinerary();
        itinerary.setId(itineraryId);

        TripItineraryDayDTO dto = new TripItineraryDayDTO(
                null,
                1,
                "Titolo",
                "Descrizione",
                itineraryId
        );

        when(itineraryRepository.findById(itineraryId)).thenReturn(Optional.of(itinerary));
        when(dayRepository.existsByTripItinerary_IdAndDayNumber(itineraryId, 1)).thenReturn(false);

        TripItineraryDay saved = new TripItineraryDay();
        saved.setId(10L);
        saved.setDayNumber(1);
        saved.setTitle("Titolo");
        saved.setDescription("Desc");
        saved.setTripItinerary(itinerary);

        when(dayRepository.save(any())).thenReturn(saved);

        TripItineraryDayDTO result = service.createDay(itineraryId, dto);

        assertNotNull(result);
        assertEquals(10L, result.getId());
        assertEquals(1, result.getDayNumber());
        verify(dayRepository).save(any());
    }

    @Test
    void createDay_itineraryNotFound_throwsException() {
        when(itineraryRepository.findById(99L)).thenReturn(Optional.empty());

        TripItineraryDayDTO dto = new TripItineraryDayDTO( null, 1, "A", "B", 99L );

        assertThrows(ResourceNotFoundException.class,
                () -> service.createDay(99L, dto));
    }

    @Test
    void createDay_duplicateDayNumber_throwsException() {
        Long itineraryId = 100L;
        TripItinerary itinerary = new TripItinerary();
        itinerary.setId(itineraryId);

        TripItineraryDayDTO dto = new TripItineraryDayDTO(
                null,
                1,
                "Titolo",
                "Descrizione",
                itineraryId
        );

        when(itineraryRepository.findById(itineraryId)).thenReturn(Optional.of(itinerary));
        when(dayRepository.existsByTripItinerary_IdAndDayNumber(itineraryId, 1))
                .thenReturn(true);

        assertThrows(IllegalArgumentException.class,
                () -> service.createDay(itineraryId, dto));
    }


    // --------------------------------------------------------------
    //                            UPDATE
    // --------------------------------------------------------------

    @Test
    void updateDay_success() {
        TripItineraryDay existing = new TripItineraryDay();
        existing.setId(5L);
        existing.setDayNumber(1);
        existing.setTitle("Vecchio titolo");
        existing.setDescription("Vecchia desc");
        existing.setTripItinerary(new TripItinerary());

        TripItineraryDayDTO dto = new TripItineraryDayDTO(
                5L,
                2,
                "Nuovo titolo",
                "Nuova desc",
                10L
        );

        when(dayRepository.findById(5L)).thenReturn(Optional.of(existing));
        when(dayRepository.save(any())).thenAnswer(i -> i.getArgument(0));

        TripItineraryDayDTO result = service.updateDay(5L, dto);

        assertEquals("Nuovo titolo", result.getTitle());
        assertEquals(2, result.getDayNumber());
        verify(dayRepository).save(any());
    }

    @Test
    void updateDay_notFound_throwsException() {
        TripItineraryDayDTO dto = new TripItineraryDayDTO(
                5L, 2, "Titolo", "Desc", 10L
        );

        when(dayRepository.findById(5L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> service.updateDay(5L, dto));
    }


    // --------------------------------------------------------------
    //                            DELETE
    // --------------------------------------------------------------

    @Test
    void deleteDay_success() {
        TripItineraryDay existing = new TripItineraryDay();
        existing.setId(50L);

        when(dayRepository.findById(50L)).thenReturn(Optional.of(existing));

        service.deleteDay(50L);

        verify(dayRepository).delete(existing);
    }

    @Test
    void deleteDay_notFound_throwsException() {
        when(dayRepository.findById(123L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> service.deleteDay(123L));
    }


    // --------------------------------------------------------------
    //                      GET DAYS BY ITINERARY
    // --------------------------------------------------------------

    @Test
    void getDaysByItinerary_success() {

        TripItineraryDay d1 = new TripItineraryDay();
        d1.setId(1L);
        d1.setDayNumber(1);
        d1.setTitle("A");
        d1.setDescription("A");
        TripItinerary itinerary = new TripItinerary();
        itinerary.setId(10L);
        d1.setTripItinerary(itinerary);

        TripItineraryDay d2 = new TripItineraryDay();
        d2.setId(2L);
        d2.setDayNumber(2);
        d2.setTitle("B");
        d2.setDescription("B");
        d2.setTripItinerary(itinerary);

        when(dayRepository.findByTripItinerary_IdOrderByDayNumberAsc(10L))
                .thenReturn(List.of(d1, d2));

        List<TripItineraryDayDTO> result = service.getDaysByItinerary(10L);

        assertEquals(2, result.size());
        assertEquals(1, result.get(0).getDayNumber());
        assertEquals(2, result.get(1).getDayNumber());
    }

}

