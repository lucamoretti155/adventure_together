package com.lucamoretti.adventure_together.model.booking;

import java.time.LocalDate;

/*
    Interfaccia per visualizzare i dettagli di una prenotazione in modo aggregato
    Utilizzata per implementare il design pattern Decorator
    Implementata da Booking e BookingDecorator
 */

public interface IBooking {
    Long getId();
    LocalDate getBookingDate();
    int getNumParticipants();

    double getTripCost();
    double getInsuranceCost();
    double getTotalCost();
}
