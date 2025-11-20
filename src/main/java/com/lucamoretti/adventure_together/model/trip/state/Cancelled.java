package com.lucamoretti.adventure_together.model.trip.state;

import com.lucamoretti.adventure_together.model.trip.Trip;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import lombok.NoArgsConstructor;

// Stato del viaggio Cancelled
// Stato finale, nessuna azione ulteriore possibile
// Se cancellato, rimane Cancelled

@Entity @DiscriminatorValue("CANCELLED")
public class Cancelled extends TripState {

    public Cancelled() { this.templateMailPath = "/mail/cancelled"; }

    @Override
    public void handle(Trip trip) {
        // Nessuna azione, già cancellato
    }

    @Override
    public void cancel(Trip trip) {
        // Nessuna azione, già cancellato
    }

    // In questo stato non è permesso accettare nuove prenotazioni
    @Override
    public boolean canAcceptBooking() {
        return false;
    }
}
