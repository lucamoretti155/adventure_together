package com.lucamoretti.adventure_together.controller.advice;

import com.lucamoretti.adventure_together.dto.trip.TripItineraryDTO;
import com.lucamoretti.adventure_together.service.trip.TripItineraryService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GlobalModelAttributesTest {

    @Mock
    private TripItineraryService tripItineraryService;

    @InjectMocks
    private GlobalModelAttributes globalModelAttributes;

    @Test
    void populateItineraries_returnsList() {

        TripItineraryDTO dto = TripItineraryDTO.builder()
                .id(1L)
                .title("Test Itinerary")
                .build();

        when(tripItineraryService.getAll()).thenReturn(List.of(dto));

        List<TripItineraryDTO> result = globalModelAttributes.populateItineraries();

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(dto, result.get(0));

        verify(tripItineraryService, times(1)).getAll();
    }
}
