package com.lucamoretti.adventure_together.model.trip.state;

import com.lucamoretti.adventure_together.model.trip.Trip;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import lombok.NoArgsConstructor;

// Stato del viaggio Confirmed Closed
// Stato finale, nessuna azione ulteriore possibile se non la cancellazione
// Se cancellato, passa a Cancelled (facoltà solo dell'admin)

@Entity @DiscriminatorValue("CONFIRMED_CLOSED")
public class ConfirmedClosed extends TripState {

    public ConfirmedClosed() { this.templateMailPath = "/mail/confirmed-closed"; }

    @Override
    public void handle(Trip trip) {
        // Nessuna azione, già chiuso
    }

    @Override
    public void cancel(Trip trip) {
        //Trip ormai confermato e chiuso, non si può più cancellare
    }

    // In questo stato non è permesso accettare nuove prenotazioni
    @Override
    public boolean canAcceptBooking() {
        return false;
    }
}
