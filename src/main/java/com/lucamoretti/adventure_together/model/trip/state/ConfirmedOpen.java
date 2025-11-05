package com.lucamoretti.adventure_together.model.trip.state;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import lombok.NoArgsConstructor;

// Stato del viaggio Confirmed Open
// Se la capacità massima è raggiunta, passa a Confirmed Closed
// Se cancellato, passa a Cancelled (facoltà solo dell'admin)

@Entity @DiscriminatorValue("CONFIRMED_OPEN")
@NoArgsConstructor
public class ConfirmedOpen extends TripState {

    public ConfirmedOpen(String templateMailPath) { this.templateMailPath = templateMailPath; }

    @Override
    public void handle() {
        if (trip == null) return;
        boolean capacityFull = trip.getCurrentParticipantsCount() >= trip.getTripItinerary().getMaxParticipants();
        if (capacityFull) {
            trip.setState(new ConfirmedClosed("/mail/confirmed-closed"));
        }
    }

    @Override
    public void cancel() {
        if (trip != null) {
            trip.setState(new Cancelled("/mail/cancelled"));
        }
    }
}
