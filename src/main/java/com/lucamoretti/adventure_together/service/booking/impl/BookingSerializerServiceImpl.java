package com.lucamoretti.adventure_together.service.booking.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.lucamoretti.adventure_together.dto.booking.BookingDTO;
import com.lucamoretti.adventure_together.service.booking.BookingSerializerService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Map;

/*
 Implementazione del servizio per la serializzazione e deserializzazione delle prenotazioni.
 */

@Service
@RequiredArgsConstructor
public class BookingSerializerServiceImpl implements BookingSerializerService {

    private final ObjectMapper mapper;

    @Override
    public String serializeBooking(Map<String, Object> data) {
        try {
            return mapper.writeValueAsString(data);
        } catch (Exception e) {
            throw new RuntimeException("Errore serializzazione booking DTO", e);
        }
    }

    @Override
    public BookingDTO deserializeBooking(String json) {
        try {
            return mapper.readValue(json, BookingDTO.class);
        } catch (Exception e) {
            throw new RuntimeException("Errore deserializzazione booking DTO", e);
        }
    }

    @Override
    public Map<String, Object> deserializeBookingAsMap(String json) {
        try {
            return new ObjectMapper().readValue(json, Map.class);
        } catch (Exception e) {
            throw new RuntimeException("Errore deserializzazione metadata JSON", e);
        }
    }
}


