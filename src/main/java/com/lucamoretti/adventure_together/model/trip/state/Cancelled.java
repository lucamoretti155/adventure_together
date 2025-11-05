package com.lucamoretti.adventure_together.model.trip.state;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import lombok.NoArgsConstructor;

// Stato del viaggio Cancelled
// Stato finale, nessuna azione ulteriore possibile
// Se cancellato, rimane Cancelled

@Entity @DiscriminatorValue("CANCELLED")
@NoArgsConstructor
public class Cancelled extends TripState {

    public Cancelled(String templateMailPath) { this.templateMailPath = templateMailPath; }

    @Override
    public void handle() {
        // Nessuna azione, già cancellato
    }

    @Override
    public void cancel() {
        // Nessuna azione, già cancellato
    }
}
