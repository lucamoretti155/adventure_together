package com.lucamoretti.adventure_together.service.booking;

import com.lucamoretti.adventure_together.dto.booking.BookingDTO;
import com.lucamoretti.adventure_together.dto.participant.ParticipantDTO;
import java.util.List;

/*
    Interfaccia per il servizio di gestione delle prenotazioni.
    Definisce i metodi per creare, cancellare e recuperare prenotazioni.
 */

public interface BookingService {

    BookingDTO createBooking(Long tripId, Long travelerId, Long departureAirportId, List<ParticipantDTO> companions);

    void cancelBooking(Long bookingId, Long travelerId);

    List<BookingDTO> getBookingsByTraveler(Long travelerId);
    List<BookingDTO> getBookingsByTrip(Long tripId);
}
