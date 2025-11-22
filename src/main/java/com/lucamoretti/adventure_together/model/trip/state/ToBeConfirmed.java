package com.lucamoretti.adventure_together.model.trip.state;

import com.lucamoretti.adventure_together.model.trip.Trip;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import lombok.NoArgsConstructor;
import java.time.LocalDate;

import static java.util.function.Predicate.isEqual;

// Stato del viaggio To Be Confirmed
// Se raggiunto il numero minimo di partecipanti, passa a ConfirmedOpen
// Se raggiunta la capacità massima, passa a ConfirmedClosed
// Se scaduta la data di chiusura iscrizioni, passa a ExpiredClosed (service schedulato)
// Se cancellato, passa a Cancelled (facoltà solo dell'admin)

@Entity
@DiscriminatorValue("TO_BE_CONFIRMED")
public class ToBeConfirmed extends TripState {

    public ToBeConfirmed() {
        this.templateMailPath = ""; // percorso vuoto dato che non ci sono mail da inviare in questo stato
    }
    @Override
    public void handle(Trip trip) {
        if (trip == null) {
            throw new IllegalStateException("STATE ERROR: this.trip è null prima della transizione!");
        }
        int participants = trip.getCurrentParticipantsCount();
        LocalDate today = LocalDate.now();

        //logica per passare a ConfirmedOpen
        if (participants >= trip.getTripItinerary().getMinParticipants() && participants < trip.getTripItinerary().getMaxParticipants() &&
                (today.isBefore(trip.getDateEndBookings())) || today.isEqual(trip.getDateEndBookings())) {

            TripState confirmed = new ConfirmedOpen();
            trip.setState(confirmed);
            trip.setTemplateMailPath(confirmed.getTemplateMailPath());
            // notifica i partecipanti dell'avvenuta conferma del viaggio
            trip.notifyAllListeners(confirmed.getTemplateMailPath());
            return;
        }

        //logica per passare a ConfirmedClosed
        if (participants == trip.getTripItinerary().getMaxParticipants() &&
                (today.isBefore(trip.getDateEndBookings())) || today.isEqual(trip.getDateEndBookings())) {
            TripState closed = new ConfirmedClosed();
            trip.setState(closed);
            trip.setTemplateMailPath(closed.getTemplateMailPath());
            // notifica i partecipanti dell'avvenuta chiusura delle iscrizioni del viaggio e che il viaggio è confermato
            trip.notifyAllListeners(closed.getTemplateMailPath());
            return;
        }

        //logica per passare a ExpiredClosed
        if (today.isAfter(trip.getDateEndBookings()) &&
                participants < trip.getTripItinerary().getMinParticipants()) {

            TripState expired = new ExpiredClosed();
            trip.setState(expired);
            trip.setTemplateMailPath(expired.getTemplateMailPath());
            // notifica i partecipanti dell'avvenuta chiusura del viaggio per mancato raggiungimento del numero minimo
            trip.notifyAllListeners(expired.getTemplateMailPath());
            return;
        }
    }

    @Override
    public void cancel(Trip trip) {
        TripState cancelled = new Cancelled();
        trip.setState(cancelled);
        trip.setTemplateMailPath(cancelled.getTemplateMailPath());
        // notifica i partecipanti dell'avvenuta cancellazione del viaggio
        trip.notifyAllListeners(cancelled.getTemplateMailPath());
    }

    // In ToBeConfirmed è permesso accettare prenotazioni
    @Override
    public boolean canAcceptBooking() {
        return true;
    }
}

