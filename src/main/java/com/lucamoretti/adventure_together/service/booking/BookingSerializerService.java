package com.lucamoretti.adventure_together.service.booking;

import com.lucamoretti.adventure_together.dto.booking.BookingDTO;

import java.util.Map;

/*
 Interfaccia per il servizio di serializzazione e deserializzazione delle prenotazioni.
 */

public interface BookingSerializerService {
    String serializeBooking(Map<String, Object> data);
    BookingDTO deserializeBooking(String json);
    Map<String, Object> deserializeBookingAsMap(String json);
}
