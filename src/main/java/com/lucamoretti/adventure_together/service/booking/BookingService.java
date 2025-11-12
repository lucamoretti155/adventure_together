package com.lucamoretti.adventure_together.service.booking;

import com.lucamoretti.adventure_together.dto.booking.BookingDTO;
import java.util.List;

/*
    Interfaccia per il servizio di gestione delle prenotazioni.
    Definisce i metodi per creare e recuperare prenotazioni.
 */

public interface BookingService {

    BookingDTO createBooking(
            BookingDTO req,
            String insuranceType // es. "basic", "cancellation", "baggage"
    );

    BookingDTO getBookingById(Long id);

    List<BookingDTO> getBookingsByTraveler(Long travelerId);
}

