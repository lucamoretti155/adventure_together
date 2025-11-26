package com.lucamoretti.adventure_together.service.booking.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.lucamoretti.adventure_together.dto.booking.BookingDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class BookingSerializerServiceImplTest {

    private BookingSerializerServiceImpl service;

    @BeforeEach
    void setup() {
        service = new BookingSerializerServiceImpl(new ObjectMapper());
    }

    // ---------- serializeBooking ----------

    @Test
    void serializeBooking_success() {
        Map<String, Object> data = Map.of(
                "tripId", 10L,
                "travelerId", 20L,
                "participants", 3
        );

        String json = service.serializeBooking(data);

        assertNotNull(json);
        assertTrue(json.contains("\"tripId\":10"));
        assertTrue(json.contains("\"travelerId\":20"));
        assertTrue(json.contains("\"participants\":3"));
    }

    @Test
    void serializeBooking_throwsRuntimeException_onError() {
        Map<String, Object> badData = Map.of(
                "invalid", new Object()   // Jackson non può serializzarlo → errore
        );

        assertThrows(RuntimeException.class,
                () -> service.serializeBooking(badData));
    }

    // ---------- deserializeBooking ----------

    @Test
    void deserializeBooking_success() {
        String json = """
            {
              "tripId": 100,
              "travelerId": 200
            }
        """;

        BookingDTO dto = service.deserializeBooking(json);

        assertNotNull(dto);
        assertEquals(100L, dto.getTripId());
        assertEquals(200L, dto.getTravelerId());
    }

    @Test
    void deserializeBooking_throwsRuntimeException_onError() {
        String invalidJson = "{ not-valid-json ";

        assertThrows(RuntimeException.class,
                () -> service.deserializeBooking(invalidJson));
    }

    // ---------- deserializeBookingAsMap ----------

    @Test
    void deserializeBookingAsMap_success() {
        String json = """
            {
              "a": 1,
              "b": "hello"
            }
        """;

        Map<String, Object> map = service.deserializeBookingAsMap(json);

        assertNotNull(map);
        assertEquals(1, map.get("a"));
        assertEquals("hello", map.get("b"));
    }

    @Test
    void deserializeBookingAsMap_throwsRuntimeException_onError() {
        String invalidJson = "{ invalid-json ";

        assertThrows(RuntimeException.class,
                () -> service.deserializeBookingAsMap(invalidJson));
    }
}
