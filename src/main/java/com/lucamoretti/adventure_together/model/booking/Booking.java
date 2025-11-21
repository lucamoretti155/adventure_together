package com.lucamoretti.adventure_together.model.booking;

import com.lucamoretti.adventure_together.model.payment.Payment;
import com.lucamoretti.adventure_together.model.participant.Participant;
import com.lucamoretti.adventure_together.model.details.DepartureAirport;
import com.lucamoretti.adventure_together.model.trip.Trip;
import com.lucamoretti.adventure_together.model.user.Traveler;
import com.lucamoretti.adventure_together.service.mail.EmailService;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.cfg.Environment;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import java.time.LocalDate;
import java.util.*;

/*
 Rappresenta una prenotazione per un viaggio.
 Implementa l'interfaccia IBooking per calcolare i costi associati alla prenotazione tramite il pattern Decorator.
 Una prenotazione è associata a un viaggio, un viaggiatore, un aeroporto di partenza, partecipanti e un pagamento.
 I costi della prenotazione includono il costo del viaggio, il costo dell'assicurazione
 (quest'ultimo calcolato come il 10% del costo del viaggio di base).
 Tramite il decorator è possibile modificare il costo dell'assicurazione
 nel caso venga aggiunta l'assicurazione per il bagaglio o/e per la cancellazione.
 */

@Data @NoArgsConstructor @AllArgsConstructor
@Entity @Table(name = "bookings")
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Booking implements IBooking, BookingListener {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    private Long id;

    // Data di prenotazione, impostata automaticamente alla data corrente
    @Column(nullable=false)
    private LocalDate bookingDate = LocalDate.now();

    //Relazioni con altre entità

    // Associazione ManyToOne con Trip
    // Una prenotazione è per un solo viaggio, ma un viaggio può avere molte prenotazioni
    // Il proprietario della relazione è Booking che contiene la foreign key
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "trip_id", nullable = false)
    private Trip trip;

    // Associazione ManyToOne con Traveler
    // Una prenotazione è fatta da un solo viaggiatore, ma un viaggiatore può fare molte prenotazioni
    // Il proprietario della relazione è Booking che contiene la foreign key
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "traveler_id", nullable = false)
    private Traveler traveler;

    // Transient perché non persistiamo il service nel DB
    @Transient
    private static EmailService emailService;

    // Transient perché non persistiamo la proprietà nel DB
    @Transient
    @Value("${app.base-url}")
    private String baseUrl;

    // Metodo statico per iniettare l'EmailService
    public static void setEmailService(EmailService service) {
        emailService = service;
    }

    //Associazione ManyToOne con DepartureAirport
    // Una prenotazione specifica un solo aeroporto di partenza, ma un aeroporto può essere usato in molte prenotazioni
    // Il proprietario della relazione è Booking che contiene la foreign key
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "departure_airport_id", nullable = false)
    private DepartureAirport departureAirport;

    //Associazione OneToMany con Participant
    // Una prenotazione può includere molti partecipanti
    // minimo uno (il viaggiatore stesso o altra persona)
    @OneToMany(mappedBy = "booking", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Participant> participants = new ArrayList<>();

    // Ritorna il numero totale dei partecipanti inclusi nella prenotazione
    public int getNumParticipants() {
        return participants != null ? participants.size() : 0;
    }

    //Associazione OneToOne con Payment
    // Una prenotazione ha un solo pagamento associato
    // il proprietario della relazione è Payment che contiene la foreign key
    // contine diverse informazioni sul pagamento effettuato per la prenotazione, inlcusi le componenti di costo
    @OneToOne(mappedBy = "booking", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    private Payment payment;

    // Costo del viaggio associato alla prenotazione
    // Il calcolo del TripCost è basato sul costo individuale del viaggio moltiplicato per il numero di partecipanti
    @Override
    public double getTripCost() {
        // controllo null per evitare NullPointerException
        if (trip == null) {
            throw new IllegalStateException("Booking.trip non inizializzato per il calcolo del costo.");
        }
        // se trip non è null calcolo il costo del viaggio
        return trip.getTripIndividualCost() * Math.max(1, getNumParticipants());
    }
    // Costo dell'assicurazione base calcolato fisso come il 10% del costo del viaggio di base
    // Tuttavia può essere modificato tramite il pattern Decorator per aggiungere costi extra
    @Override
    public double getInsuranceCost() { return this.getTripCost() * 0.10; }

    // Calcolo del costo totale della prenotazione sommando costo del viaggio + costo dell'assicurazione (eventualemente modificato dai decorator)
    @Override
    public double getTotalCost() {
        return getTripCost() + getInsuranceCost();
    }
    // Implementazione del metodo update dell'interfaccia BookingListener


    /*
     * Metodo invocato automaticamente quando il Trip cambia stato.
     * Invia una notifica email al traveler usando il template associato al nuovo stato del viaggio.
     */

    @Override
    public void update(String mailTemplatePath, String baseUrl) {
        if (emailService == null) {
            System.err.println("[WARN] EmailService non configurato per Booking.update()");
            return;
        }
        emailService.sendHtmlMessage(
                traveler.getEmail(),
                "Aggiornamento sul tuo viaggio " + trip.getTripItinerary().getTitle(),
                mailTemplatePath,
                Map.of("traveler", traveler, "trip", trip, "homepage", baseUrl+"/home")
        );
    }

}

