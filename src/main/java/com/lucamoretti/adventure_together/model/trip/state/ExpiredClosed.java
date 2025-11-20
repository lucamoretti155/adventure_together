package com.lucamoretti.adventure_together.model.trip.state;

import com.lucamoretti.adventure_together.model.trip.Trip;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import lombok.NoArgsConstructor;

// Stato del viaggio Expired Closed
// Stato finale, nessuna azione ulteriore possibile se non la cancellazione
// Se cancellato, passa a Cancelled (facoltà solo dell'admin)

@Entity
@DiscriminatorValue("EXPIRED_CLOSED")
public class ExpiredClosed extends TripState {

    public ExpiredClosed() { this.templateMailPath = "mail/expired-closed"; }

    @Override
    public void handle(Trip trip) {
        // Nessuna azione, già chiuso
    }


    @Override
    public void cancel(Trip trip) {
        //Trip ormai scaduto e chiuso, non si può più cancellare
    }

    // In questo stato non è permesso accettare nuove prenotazioni
    @Override
    public boolean canAcceptBooking() {
        return false;
    }
}
