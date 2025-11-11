package com.lucamoretti.adventure_together.model.trip;

import com.lucamoretti.adventure_together.model.booking.Booking;
import com.lucamoretti.adventure_together.model.trip.state.ToBeConfirmed;
import com.lucamoretti.adventure_together.model.trip.state.TripState;
import com.lucamoretti.adventure_together.model.user.Planner;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;
import java.util.LinkedHashSet;
import java.util.Set;

/* Rappresenta un viaggio organizzato all'interno della piattaforma Adventure Together
 Contiene informazioni sulle date, costi, stato attuale e relazioni con altri modelli
 Utilizza il pattern State per gestire gli stati del viaggio
 Un Trip è associato ad un TripItinerary che definisce il percorso e le attività del viaggio
 Un Trip è associato ad un Planner (utente con ruolo planner o admin) che lo gestisce
 Un Trip può avere molteplici Booking associati, rappresentando le prenotazioni effettuate dagli utenti
 Il Trip gestisce le transizioni di stato tramite il pattern State, delegando i comportamenti specifici agli stati concreti
 come ToBeConfirmed, ConfirmedOpen, ConfirmedClosed, ExpiredClosed, Cancelled
 Le date di inizio e fine delle prenotazioni, la data di partenza e ritorno, e il costo individuale del viaggio sono attributi chiave
 per la gestione e visualizzazione del viaggio all'interno della piattaforma
 la piattaforma esporrà ogni TripItinerary e per ognuno i relativi Trip associati (con date e costi specifici)
*/

@Data @NoArgsConstructor @AllArgsConstructor
@Entity @Table(name = "trips")
public class Trip {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Date di inizio e fine delle prenotazioni
    // La data di inizio prenotazione coincide con la data di creazione del Trip
    @Column (nullable=false)
    private LocalDate dateStartBookings;
    // Date di fine prenotazioni viene usata per chiudere le prenotazioni automaticamente
    @Column (nullable=false)
    private LocalDate dateEndBookings;

    // Date di partenza e ritorno del viaggio devono essere coerenti con la durata dell'itinerario
    @Column(nullable=false)
    private LocalDate dateDeparture;

    @Column(nullable=false)
    private LocalDate dateReturn;

    // Costo individuale del viaggio per un partecipante non compresi di assicurazione
    @Column(nullable=false)
    private double tripIndividualCost;

    // Stato attuale (OneToOne) – State Pattern
    // Il lato proprietario della relazione è TripState
    @OneToOne(mappedBy = "trip", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private TripState state;

    // Itinerario (molti Trip possono condividere lo stesso Itinerary)
    // Le date di partenza/ritorno e costi sono specifici per ogni Trip fanno da variante all'interno dello stesso TripItinerary
    // Il propprietarion della relazione è Trip
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "trip_itinerary_id", nullable = false)
    private TripItinerary tripItinerary;

    // Un planner (utente con ruolo planner o admin) gestisce il Trip
    // Il proprietario della relazione è Trip
    // non è necessario mantenere una lista di Trip nel Planner
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "planner_id", nullable = false)
    private Planner planner;

    // Percorso del template email associato allo stato attuale del viaggio
    // viene aggiornata ad ogni cambio di stato
    // viene usata per inviare email relative al viaggio
    private String templateMailPath;

    //Relazione con Booking
    // Un Trip può avere molteplici Booking associati
    // rappresentando le prenotazioni effettuate dai travellers
    @OneToMany(mappedBy = "trip", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Booking> bookings = new LinkedHashSet<>();

    // Notifica tutti i listener (Booking) associati a questo Trip
    // Viene chiamato ad ogni cambio di stato per notificare le prenotazioni
    public void notifyAllListeners(String mailTemplatePath) {
        for (Booking booking : bookings) {
            booking.update(mailTemplatePath);
        }
    }

    // Metodo per settare inizialmente lo stato del Trip
    // Si occupa di collegare lo stato al Trip e aggiornare il templateMailPath
    // Usato internamente quando si crea un nuovo Trip
    public void open() {
        if (this.state != null) {
            throw new IllegalStateException("Trip è già stato aperto con uno stato iniziale.");
        }
        TripState newState = new ToBeConfirmed();
        newState.attachTo(this);
        this.state = newState;
        this.templateMailPath = newState.getTemplateMailPath();
    }
    // metodo delegato allo State Pattern
    public void handle() {
        if (state != null) state.handle();
    }
    // metodo delegato allo State Pattern
    public void cancel() {
        if (state != null) state.cancel();
    }

    // Ritorna il numero totale dei partecipanti attualmente prenotati per il viaggio
    // Somma i partecipanti di tutte le Booking associate al Trip
    public int getCurrentParticipantsCount() {
        return bookings != null
                ? bookings.stream().mapToInt(Booking::getNumParticipants).sum()
                : 0;
    }





}
