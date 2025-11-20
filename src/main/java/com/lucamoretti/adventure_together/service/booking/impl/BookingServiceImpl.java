package com.lucamoretti.adventure_together.service.booking.impl;

import com.lucamoretti.adventure_together.dto.booking.BookingDTO;
import com.lucamoretti.adventure_together.dto.participant.ParticipantDTO;
import com.lucamoretti.adventure_together.model.booking.Booking;
import com.lucamoretti.adventure_together.model.booking.IBooking;
import com.lucamoretti.adventure_together.model.participant.Participant;
import com.lucamoretti.adventure_together.model.payment.Payment;
import com.lucamoretti.adventure_together.model.trip.Trip;
import com.lucamoretti.adventure_together.model.trip.state.TripState;
import com.lucamoretti.adventure_together.model.user.Traveler;
import com.lucamoretti.adventure_together.model.details.DepartureAirport;
import com.lucamoretti.adventure_together.repository.booking.BookingRepository;
import com.lucamoretti.adventure_together.repository.details.DepartureAirportRepository;
import com.lucamoretti.adventure_together.repository.trip.TripRepository;
import com.lucamoretti.adventure_together.repository.user.TravelerRepository;
import com.lucamoretti.adventure_together.service.booking.BookingService;
import com.lucamoretti.adventure_together.service.mail.EmailService;
import com.lucamoretti.adventure_together.service.payment.impl.StripeClient;
import com.lucamoretti.adventure_together.util.exception.ResourceNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Map;
import com.lucamoretti.adventure_together.model.booking.decorator.CancellationInsurance;
import com.lucamoretti.adventure_together.model.booking.decorator.LuggageInsurance;
import java.util.*;
import java.util.function.Function;

/*
    Implementazione del servizio di gestione delle prenotazioni (Booking)
    Si occupa della logica di creazione, recupero e gestione delle prenotazioni
    Utilizza il pattern Decorator per calcolare dinamicamente il costo totale della prenotazione
    in base alle assicurazioni scelte dall'utente
    Utilizza il pattern State per gestire lo stato del Trip associato alla prenotazione
    Utilizza Stripe per la gestione dei pagamenti
 */

@Service
@RequiredArgsConstructor
@Transactional
public class BookingServiceImpl implements BookingService {

    private final BookingRepository bookingRepository;
    private final TripRepository tripRepository;
    private final TravelerRepository travelerRepository;
    private final DepartureAirportRepository departureAirportRepository;
    private final StripeClient stripeClient;
    private final EmailService emailService;
    @Value("${app.base-url}")
    private String baseUrl;

    //  Mappa di strategie per l'applicazione dinamica dei Decorator
    //  Permette di aggiungere nuovi tipi di assicurazione senza modificare la logica principale
    private final Map<String, Function<IBooking, IBooking>> bookingDecoration = Map.of(
            "cancellation", CancellationInsurance::new,
            "luggage",      LuggageInsurance::new,
            "full",         b -> new CancellationInsurance(new LuggageInsurance(b)),
            "null",         b -> b // nessuna assicurazione aggiuntiva
    );

    //helper per validare la disponibilità del booking
    //controlla lo stato del trip e il numero di partecipanti richiesti con quelli disponibili
    private void validateBookingAvailability(Trip trip, List<ParticipantDTO> participantsDto) {
        if (trip.getState() == null || !trip.getState().canAcceptBooking()) {
            throw new IllegalStateException("Il viaggio non è prenotabile nello stato attuale.");
        }
        int requested = participantsDto.size();
        int available = trip.getTripItinerary().getMaxParticipants() - trip.getCurrentParticipantsCount();
        if (requested > available) {
            throw new IllegalArgumentException("Posti insufficienti: disponibili " + available);
        }
    }

    @Override
    public BookingDTO createBooking(BookingDTO req,
                                 String insuranceType) {

        // Recupero le entità principali
        Trip trip = tripRepository.findById(req.getTripId())
                .orElseThrow(() -> new ResourceNotFoundException("Trip", "id", req.getTripId()));
        Traveler traveler = travelerRepository.findById(req.getTravelerId())
                .orElseThrow(() -> new ResourceNotFoundException("Traveler", "id", req.getTravelerId()));
        DepartureAirport airport = departureAirportRepository.findById(req.getDepartureAirportId())
                .orElseThrow(() -> new ResourceNotFoundException("Aeroporto", "id", req.getDepartureAirportId()));



        // Verifico che sia possibile effettuare la prenotazione tramite helper
        // se non è possibile, viene lanciata un'eccezione
        validateBookingAvailability(trip, req.getParticipants());

        //converto la lista di ParticipantDTO in Participant
        //Effettuo un controllo per verificare che la lista non sia vuota
        //in teoria da DTO dovrebbe arrivare sempre almeno un partecipante (il Traveler stesso)
        List<ParticipantDTO> participantsDto =
                req.getParticipants() != null ? req.getParticipants() : List.of(); // evito NullPointerException

        if (participantsDto.isEmpty()) {
            throw new IllegalArgumentException("Devi indicare almeno un partecipante.");
        }

        List<Participant> participants = new ArrayList<>();
        for (int i = 0; i < participantsDto.size(); i++) {
            ParticipantDTO pDto = participantsDto.get(i);
            if (pDto.getFirstName() == null || pDto.getFirstName().isBlank() ||
                pDto.getLastName() == null || pDto.getLastName().isBlank() ||
                pDto.getDateOfBirth() == null) {
                throw new IllegalArgumentException("Dati del partecipante non validi alla posizione " + (i + 1));
            }
            participants.add(ParticipantDTO.toEntity(pDto));
        }

        // Creo il Booking base per poi decorarlo con le assicurazioni scelte
        //altrimenti avrei dovuto creare decoratori anche a livello di DTO, complicando inutilmente il codice
        Booking booking = req.toEntity(trip, traveler, airport, participants);

        // Associo la prenotazione al trip
        trip.addBooking(booking);

        /*  Calcolo del costo totale con Decorator
            Se l'utente non sceglie nessuna assicurazione, viene calcolato solo il costo base
            che include l'assicurazione di base (10% del costo del viaggio)
            Uso il pattern Decorator per aggiungere dinamicamente le eventuali assicurazioni scelte (cancellation, luggage, full)
        */

        // Inizializzo la prenotazione decorata con l'istanza base
        IBooking decorated = booking;
        // Applico il decoratore in base al tipo di assicurazione scelto
        if (insuranceType != null && !insuranceType.isBlank()) {
            Function<IBooking, IBooking> decoratorFn = bookingDecoration.get(insuranceType.toLowerCase()); // Recupero la funzione decoratrice dalla mappa
            if (decoratorFn != null) {
                decorated = decoratorFn.apply(decorated); // Applico il decoratore
            }
        }
        // Calcolo il costo totale della prenotazione che tiene conto dei decoratori applicati (eventuali)
        double totalCost = decorated.getTotalCost();

        // Creazione PaymentIntent su Stripe (test mode)
        var paymentIntent = stripeClient.createPaymentIntent(totalCost, "eur");


        Payment payment = new Payment();
        payment.setBooking(booking);
        payment.setPaymentDate(java.time.LocalDate.now());
        payment.setAmountPaid(totalCost);
        payment.setAmountInsurance(decorated.getInsuranceCost());
        payment.setPaymentIntentId(paymentIntent.getPaymentIntentId());
        payment.setClientSecret(paymentIntent.getClientSecret());
        payment.setStatus("PENDING"); // Lo stato verrà aggiornato dopo la conferma del pagamento lato frontend
        payment.setPaymentMethod("card"); // Per ora supportiamo solo pagamenti con carta
        payment.setCurrency("eur"); // imposto euro come valuta standard

        booking.setPayment(payment);

        // salvo la prenotazione (cascade salva anche i partecipanti e il pagamento) nel database
        bookingRepository.save(booking);

        // aggiorno lo stato del trip attravero lo State Pattern
        // delego la gestione dello stato allo State Pattern del Trip che si occuperà di aggiornare lo stato se necessario
        // se viene cambiato lo stato del trip, salvo l'entità aggiornata
        TripState before = trip.getState();
        trip.handle();
        TripState after = trip.getState();
        if (!after.getClass().equals(before.getClass())) {
            tripRepository.save(trip);
        }

        // Invio email di conferma al traveler
        // uso il template standard di conferma prenotazione
        // NB: l'attributo templateMailPath del Trip non viene usato qui ma solo per aggiornamenti di stato
        String templateMailPath = "/mail/booking-confirmation";
        try {
            emailService.sendHtmlMessage(
                    traveler.getEmail(),
                    "Conferma prenotazione viaggio " + trip.getTripItinerary().getTitle(),
                    trip.getTemplateMailPath(),
                    Map.of(
                            "traveler", traveler,
                            "trip", trip,
                            "booking", booking,
                            "totalCost", totalCost,
                            "homepage", baseUrl+"/home"
                    )
            );
        } catch (Exception e) {
            System.err.println("Errore durante l'invio della mail di conferma: " + e.getMessage());
        }
        // Ritorno il DTO della prenotazione creata
        return BookingDTO.fromEntity(booking);
    }

    @Override
    public BookingDTO getBookingById(Long id) {
        return bookingRepository.findById(id)
                .map(BookingDTO::fromEntity)
                .orElseThrow(() -> new IllegalArgumentException("Booking non trovato"));
    }

    @Override
    public List<BookingDTO> getBookingsByTraveler(Long travelerId) {
        return bookingRepository.findByTraveler_Id(travelerId)
                .stream()
                .map(BookingDTO::fromEntity)
                .toList();
    }
}
