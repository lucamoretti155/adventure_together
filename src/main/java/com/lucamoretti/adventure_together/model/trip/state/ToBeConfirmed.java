package com.lucamoretti.adventure_together.model.trip.state;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import lombok.NoArgsConstructor;
import java.time.LocalDate;

// Stato del viaggio To Be Confirmed
// Se raggiunto il numero minimo di partecipanti, passa a ConfirmedOpen
// Se scaduta la data di chiusura iscrizioni, passa a ExpiredClosed (service schedulato)
// Se cancellato, passa a Cancelled (facoltà solo dell'admin)

@Entity
@DiscriminatorValue("TO_BE_CONFIRMED")
public class ToBeConfirmed extends TripState {

    public ToBeConfirmed() {
        this.templateMailPath = "/mail/to-be-confirmed";
    }
    @Override
    public void handle() {
        int participants = trip.getCurrentParticipantsCount();
        LocalDate today = LocalDate.now();

        if (participants >= trip.getTripItinerary().getMinParticipants() &&
                today.isBefore(trip.getDateEndBookings())) {

            TripState confirmed = new ConfirmedOpen();
            confirmed.attachTo(trip);
            trip.setState(confirmed);
            trip.setTemplateMailPath(confirmed.getTemplateMailPath());
            // notifica i partecipanti dell'avvenuta conferma del viaggio
            trip.notifyAllListeners(confirmed.getTemplateMailPath());
        } else if (today.isAfter(trip.getDateEndBookings()) &&
                participants < trip.getTripItinerary().getMinParticipants()) {

            TripState expired = new ExpiredClosed();
            expired.attachTo(trip);
            trip.setState(expired);
            trip.setTemplateMailPath(expired.getTemplateMailPath());
            // notifica i partecipanti dell'avvenuta chiusura del viaggio per mancato raggiungimento del numero minimo
            trip.notifyAllListeners(expired.getTemplateMailPath());
        }
    }

    @Override
    public void cancel() {
        TripState cancelled = new Cancelled();
        cancelled.attachTo(trip);
        trip.setState(cancelled);
        trip.setTemplateMailPath(cancelled.getTemplateMailPath());
        // notifica i partecipanti dell'avvenuta cancellazione del viaggio
        trip.notifyAllListeners(cancelled.getTemplateMailPath());
    }
}

