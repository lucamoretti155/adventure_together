package com.lucamoretti.adventure_together.model.booking.decorator;

import com.lucamoretti.adventure_together.model.booking.IBooking;

// Questo decorator si occupa della gestione dell'assicurazione bagaglio
// aggiunta costo fisso per bagaglio e per partecipante
// andando a modificare il costo dell'assicurazione della prenotazione
// usa un metodo protetto getExtraCost() per calcolare il costo extra specifico
// che viene sommato al costo dell'assicurazione di base definita nella classe Booking

public class LuggageInsurance extends BookingDecorated {

    // extra fisso per bagaglio
    private final double fixedExtraPerPerson;

    // Costruttore che accetta un'istanza di IBooking da decorare
    public LuggageInsurance(IBooking inner) {
        super(inner);
        this.fixedExtraPerPerson = 20.0; // esempio: 20 euro per bagaglio
    }

    // Override del metodo per calcolare il costo totale dell'assicurazione
    @Override
    public double getInsuranceCost() {
        return inner.getInsuranceCost() + this.getExtraCost();
    }

    // Calcolo del costo extra basato sul numero di partecipanti
    @Override
    protected double getExtraCost() {
        return (fixedExtraPerPerson * Math.max(1, inner.getNumParticipants())); // almeno 1 partecipante
    }
}
