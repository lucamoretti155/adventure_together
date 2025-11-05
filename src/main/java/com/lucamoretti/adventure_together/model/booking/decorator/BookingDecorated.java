package com.lucamoretti.adventure_together.model.booking.decorator;

import com.lucamoretti.adventure_together.model.booking.IBooking;
import lombok.RequiredArgsConstructor;
import java.time.LocalDate;

/* Decorator astratto per le assicurazioni di una prenotazione
 Implementa l'interfaccia IBooking e incapsula un'altra istanza di IBooking
 Fornisce implementazioni di default per i metodi dell'interfaccia, delegando le chiamate all'istanza incapsulata
 Le classi concrete di decoratori estendono questa classe per aggiungere funzionalità specifiche
 Fornisce un metodo protetto getExtraCost() che le sottoclassi possono sovrascrivere per aggiungere costi extra specifici al costo dell'assicurazione
 Di fatto l'assicurazione di base è quella definita nella classe Booking (10% del costo del viaggio)
 Se il traveler aggiunge una o più assicurazioni, il costo dell'assicurazione viene incrementato di conseguenza
 Il costo totale della prenotazione viene calcolato sommando il costo del viaggio e il costo totale dell'assicurazione (incluse le modifiche dei decoratori)
 Per ora, le assicurazioni disponibili sono solo due (cancellazione e bagaglio), ma il design permette di aggiungerne altre in futuro
 La CancellationInsurance aggiunge sostazialmente una percentuale fissa sul costo del viaggio (in questo caso 5%)
 La BaggageInsurance aggiunge un costo fisso per partecipante (in questo caso 20.0)
 E' possibile combinare più decoratori insieme per calcolare il costo totale dell'assicurazione in modo flessibile
*/

@RequiredArgsConstructor
public abstract class BookingDecorated implements IBooking {
    // composizione: incapsula un'istanza di IBooking
    protected final IBooking inner;

    @Override public Long getId() { return inner.getId(); }
    @Override public LocalDate getBookingDate() { return inner.getBookingDate(); }
    @Override public int getNumParticipants() { return inner.getNumParticipants(); }
    @Override public double getTripCost() { return inner.getTripCost(); }

    @Override
    public double getInsuranceCost() {
        return inner.getInsuranceCost();
    }

    protected double getExtraCost() {
        return 0.0;
    }

    @Override
    public double getTotalCost() {
        return getTripCost() + getInsuranceCost();
    }
}
