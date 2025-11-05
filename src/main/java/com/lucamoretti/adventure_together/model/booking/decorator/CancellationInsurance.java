package com.lucamoretti.adventure_together.model.booking.decorator;

import com.lucamoretti.adventure_together.model.booking.IBooking;

// Questo decorator si occupa della gestione dell'assicurazione cancellazione
// aggiunta percentuale basata sul costo del Trip
// andando a modificare il costo dell'assicurazione della prenotazione
// usa un metodo protetto getExtraCost() per calcolare il costo extra specifico
// che viene sommato al costo dell'assicurazione di base definita nella classe Booking

public class CancellationInsurance extends BookingDecorated {
    // percentuale sul costo del viaggio
    private final double percentage;

    // Costruttore che accetta un'istanza di IBooking da decorare
    public CancellationInsurance(IBooking inner) {
        super(inner);
        this.percentage = 0.05; // esempio: 5% aggiuntivo del costo del viaggio
    }
    // Override del metodo per calcolare il costo totale dell'assicurazione
    @Override
    public double getInsuranceCost() {
        return inner.getInsuranceCost() + this.getExtraCost(); // di fatto si arriva al 15% del costo del viaggio
    }
    // Calcolo del costo extra basato sulla percentuale del costo del viaggio
    @Override
    protected double getExtraCost() {
        return (inner.getTripCost() * percentage);
    }
}
