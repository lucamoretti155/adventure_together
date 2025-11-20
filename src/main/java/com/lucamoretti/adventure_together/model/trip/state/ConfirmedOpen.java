package com.lucamoretti.adventure_together.model.trip.state;

import com.lucamoretti.adventure_together.model.trip.Trip;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

// Stato del viaggio Confirmed Open
// Se la capacità massima è raggiunta, passa a Confirmed Closed
// Se cancellato, passa a Cancelled (facoltà solo dell'admin)

@Entity @DiscriminatorValue("CONFIRMED_OPEN")
public class ConfirmedOpen extends TripState {

    public ConfirmedOpen() {
        this.templateMailPath = "/mail/confirmed-open";
    }

    @Override
    public void handle(Trip trip) {
        int participants = trip.getCurrentParticipantsCount();
        LocalDate today = LocalDate.now();

        if (participants >= trip.getTripItinerary().getMaxParticipants() ||
                today.isAfter(trip.getDateEndBookings())) {

            TripState closed = new ConfirmedClosed();
            trip.setState(closed);
            trip.setTemplateMailPath(closed.getTemplateMailPath());
            // notifica i partecipanti dell'avvenuta chiusura delle iscrizioni del viaggio e che il viaggio è confermato
            trip.notifyAllListeners(closed.getTemplateMailPath());
        }
    }

    @Override
    public void cancel(Trip trip) {
        // Trip già confermato, non si può più cancellare
    }

    // In questo stato è permesso accettare nuove prenotazioni
    @Override
    public boolean canAcceptBooking() {
        return true;
    }
}
