package com.lucamoretti.adventure_together.model.trip.state;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import lombok.NoArgsConstructor;

// Stato del viaggio Expired Closed
// Stato finale, nessuna azione ulteriore possibile se non la cancellazione
// Se cancellato, passa a Cancelled (facoltà solo dell'admin)

@Entity @DiscriminatorValue("EXPIRED_CLOSED")
@NoArgsConstructor
public class ExpiredClosed extends TripState {

    public ExpiredClosed(String templateMailPath) { this.templateMailPath = templateMailPath; }

    @Override
    public void handle() {
        // Nessuna azione, già chiuso
    }


    @Override
    public void cancel() {
        if (trip != null) {
            trip.setState(new Cancelled("/mail/cancelled"));
        }
    }
}
