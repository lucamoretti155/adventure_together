package com.lucamoretti.adventure_together.service.booking;

import com.lucamoretti.adventure_together.dto.booking.BookingDTO;
import java.util.List;

/*
    Interfaccia per il servizio visualizzazione delle prenotazioni.
    Definisce i metodi per recuperare prenotazioni.
 */

public interface BookingService {

    BookingDTO getBookingById(Long id);
    List<BookingDTO> getBookingsByTravelerId(Long travelerId);
}

