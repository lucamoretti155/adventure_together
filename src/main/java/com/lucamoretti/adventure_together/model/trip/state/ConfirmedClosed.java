package com.lucamoretti.adventure_together.model.trip.state;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import lombok.NoArgsConstructor;

// Stato del viaggio Confirmed Closed
// Stato finale, nessuna azione ulteriore possibile se non la cancellazione
// Se cancellato, passa a Cancelled (facoltà solo dell'admin)

@Entity @DiscriminatorValue("CONFIRMED_CLOSED")
@NoArgsConstructor
public class ConfirmedClosed extends TripState {

    public ConfirmedClosed(String templateMailPath) { this.templateMailPath = templateMailPath; }

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
