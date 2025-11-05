package com.lucamoretti.adventure_together.model.trip.state;

import com.lucamoretti.adventure_together.model.trip.Trip;
import jakarta.persistence.*;
import lombok.*;

/* Stato del viaggio (State Pattern)
Ogni stato può avere comportamenti diversi per handle() e cancel()
Viene usato Single Table Inheritance per mappare i diversi stati in una singola tabella
con una colonna discriminator_type per identificare il tipo di stato
Ogni stato ha un templateMailPath associato per le email da inviare in quello stato

lo stato può passare a ConfirmedOpen, ConfirmedClosed, ExpiredClosed, Cancelled
in qualunque stato, l'admin (solamente) può cancellare il viaggio, passando a Cancelled
ogni stato implementa handle() per gestire le transizioni automatiche e cancel() per gestire la cancellazione del viaggio
- ToBeConfirmed è il primo stato del viaggio
- ConfirmedOpen significa che il viaggio è confermato (i.e. ha raggiunto il numero minimo di partecipanti)
    ma è ancora aperto per nuove iscrizioni (fino al raggiungimento del numero massimo di partecipanti o alla data di chiusura)
- ConfirmedClosed significa che il viaggio è confermato ma non accetta più iscrizioni perchè ha raggiunto il numero massimo di partecipanti o la data di chiusura
- ExpiredClosed significa che il viaggio è terminato (data di fine superata) e non accetta più iscrizioni ma non è stato raggiunto il numero minimo di partecipanti
- Cancelled significa che il viaggio è stato cancellato dall'admin
*/

@Data @NoArgsConstructor
@Entity @Table(name = "trip_states")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "state_type", discriminatorType = DiscriminatorType.STRING, length = 32)
public abstract class TripState {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    protected String templateMailPath;

    // La relazione con Trip è OneToOne bidirezionale, con Trip che possiede la relazione
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "trip_id", unique = true) // unique per garantire la relazione OneToOne
    @Setter(AccessLevel.PROTECTED) // il setter è protected per evitare modifiche esterne
    protected Trip trip;

    public abstract void handle(); // e.g. conferme/transizioni automatiche
    public abstract void cancel();

    // Getter per il templateMailPath
    // viene sovrascritto a lombok per essere pubblico
    public String getTemplateMailPath() { return templateMailPath; }

    // Metodo per collegare lo stato al Trip
    // Usato internamente da Trip.open()
    public void attachTo(Trip trip) {
        this.trip = trip;
    }
}
