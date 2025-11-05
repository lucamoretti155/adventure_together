package com.lucamoretti.adventure_together.model.participant;

import com.lucamoretti.adventure_together.model.booking.Booking;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;

/* Rappresenta un partecipante a una prenotazione di viaggio
 Contiene informazioni personali come nome, cognome e data di nascita
 Ogni partecipante è associato a una singola prenotazione (Booking)
 Viene utilizzato per gestire i partecipanti inclusi in una prenotazione di viaggio
 I partecipanti possono essere il viaggiatore stesso o altre persone incluse nella prenotazione
 NB: sono ammessi duplicati
 e.g. se un viaggiatore prenota due viaggi per sè e un amico, si crearanno 4 id diversi, di cui 2 con lo stesso nome e data di nascita
 Serve per tenere traccia di ogni partecipante in ogni prenotazione specifica
*/

@Data @NoArgsConstructor @AllArgsConstructor
@Entity @Table(name = "participants")
public class Participant {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable=false)
    private String firstName;

    @Column(nullable=false)
    private String lastName;

    // Data di nascita del partecipante per controlli di età e validità
    @Column(nullable=false)
    private LocalDate dateOfBirth;

    // Relazione ManyToOne con Booking
    // Ogni partecipante è associato a una singola prenotazione
    // Un booking può avere molti partecipanti
    // La relazione è obbligatoria (optional = false) per garantire che ogni partecipante appartenga a una prenotazione valida
    // Il proprietario della relazione è Participant dato che contiene la foreign key
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "booking_id", nullable = false)
    private Booking booking;
}

