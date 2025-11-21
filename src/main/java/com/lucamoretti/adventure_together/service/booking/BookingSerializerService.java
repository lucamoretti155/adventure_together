package com.lucamoretti.adventure_together.service.booking;

import com.lucamoretti.adventure_together.dto.booking.BookingDTO;

/*
 Interfaccia per il servizio di serializzazione e deserializzazione delle prenotazioni.
 */

public interface BookingSerializerService {
    String serializeBooking(BookingDTO dto);
    BookingDTO deserializeBooking(String json);
}
