package com.lucamoretti.adventure_together.model.trip.state;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import lombok.NoArgsConstructor;
import java.time.LocalDate;

// Stato del viaggio To Be Confirmed
// Se raggiunto il numero minimo di partecipanti, passa a ConfirmedOpen
// Se scaduta la data di chiusura iscrizioni, passa a ExpiredClosed (service schedulato)
// Se cancellato, passa a Cancelled (facoltà solo dell'admin)

@Entity @DiscriminatorValue("TO_BE_CONFIRMED")
@NoArgsConstructor
public class ToBeConfirmed extends TripState {

    public ToBeConfirmed(String templateMailPath) { this.templateMailPath = templateMailPath; }

    @Override
    public void handle() {
        //se raggiunto min participants -> passa a ConfirmedOpen
        if (trip != null && trip.getCurrentParticipantsCount() >= trip.getTripItinerary().getMinParticipants()) {
            trip.setState(new ConfirmedOpen("/mail/confirmed-open"));
        }
        //controllare data di chiusura iscrizioni e passare a ExpiredClosed se scaduta
        //questo controllo verrà affidato ad un service schedulato
        LocalDate today = LocalDate.now();
        if (trip != null &&(trip.getDateEndBookings().isBefore(today))) { //se la data di fine iscrizioni è passata
            trip.setState(new ExpiredClosed("/mail/expired-closed"));
        }
    }

    @Override
    public void cancel() {
        if (trip != null) {
            trip.setState(new Cancelled("/mail/cancelled"));
        }
    }
}

