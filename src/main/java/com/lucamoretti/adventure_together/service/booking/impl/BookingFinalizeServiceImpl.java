package com.lucamoretti.adventure_together.service.booking.impl;

import com.lucamoretti.adventure_together.model.booking.Booking;
import com.lucamoretti.adventure_together.model.booking.IBooking;
import com.lucamoretti.adventure_together.model.booking.decorator.CancellationInsurance;
import com.lucamoretti.adventure_together.model.booking.decorator.LuggageInsurance;
import com.lucamoretti.adventure_together.model.details.DepartureAirport;
import com.lucamoretti.adventure_together.model.participant.Participant;
import com.lucamoretti.adventure_together.model.participant.TemporaryParticipantList;
import com.lucamoretti.adventure_together.model.payment.Payment;
import com.lucamoretti.adventure_together.model.trip.Trip;
import com.lucamoretti.adventure_together.model.trip.state.TripState;
import com.lucamoretti.adventure_together.model.user.Traveler;
import com.lucamoretti.adventure_together.repository.booking.BookingRepository;
import com.lucamoretti.adventure_together.repository.details.DepartureAirportRepository;
import com.lucamoretti.adventure_together.repository.participant.TemporaryParticipantListRepository;
import com.lucamoretti.adventure_together.repository.trip.TripRepository;
import com.lucamoretti.adventure_together.repository.user.TravelerRepository;
import com.lucamoretti.adventure_together.service.booking.BookingFinalizeService;
import com.lucamoretti.adventure_together.service.booking.BookingSerializerService;
import com.lucamoretti.adventure_together.service.mail.EmailService;
import com.stripe.model.PaymentIntent;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/*
 Implementazione del servizio per la finalizzazione della prenotazione.
 Si occupa di ricostruire il booking dal DTO serializzato nei metadata del PaymentIntent,
 salvare il booking nel database, aggiornare lo stato del trip e inviare l'email di conferma.
 */

@Service
@RequiredArgsConstructor
@Transactional
public class BookingFinalizeServiceImpl implements BookingFinalizeService {

    private final BookingRepository bookingRepository;
    private final TravelerRepository travelerRepository;
    private final TripRepository tripRepository;
    private final DepartureAirportRepository departureAirportRepository;
    private final BookingSerializerService bookingSerializerService;
    private final EmailService emailService;
    private final TemporaryParticipantListRepository temporaryParticipantListRepository;

    @Value("${app.base-url}")
    private String baseUrl;

    // Mappa delle decorazioni disponibili per i booking
    private final Map<String, Function<IBooking, IBooking>> decorations = Map.of(
            "basic", b -> b,
            "cancellation", CancellationInsurance::new,
            "luggage", LuggageInsurance::new,
            "full", b -> new CancellationInsurance(new LuggageInsurance(b))
    );

    @Override
    public void finalizeBooking(PaymentIntent intent) {


        //System.out.println("=== FINALIZE BOOKING TRIGGERED ===");
        //System.out.println("INTENT ID: " + intent.getId());
        //System.out.println("INTENT METADATA: " + intent.getMetadata());

        // estrazione metadata booking
        if (intent.getMetadata() == null || !intent.getMetadata().containsKey("booking")) {
            System.out.println("❌ PaymentIntent metadata assente o incompleta — impossibile finalizzare");
            return;
        }
        try {
            String metadataJson = intent.getMetadata().get("booking"); // JSON serializzato

            if (metadataJson == null) {
                System.out.println("❌ metadataJson == NULL — Booking NON ricostruibile");
                return;
            }

            //System.out.println("metadataJson = " + metadataJson);

            // deserializzazione BookingDTO
            //BookingDTO dto = bookingSerializerService.deserializeBooking(metadataJson);
            //devo ricostruire manualmente il booking per via delle entità correlate partendo da una Map
            Map<String, Object> data = bookingSerializerService.deserializeBookingAsMap(metadataJson);

            Long tripId = Long.valueOf(data.get("tripId").toString());
            Long travelerId = Long.valueOf(data.get("travelerId").toString());
            Long departureAirportId = Long.valueOf(data.get("departureAirportId").toString());
            String insuranceType = data.get("insuranceType").toString();
            Integer numParticipants = Integer.valueOf(data.get("numParticipants").toString());
            Long tempListId = Long.valueOf(data.get("participantsTempListId").toString());

            // recupero entità correlate
            Trip trip = tripRepository.findById(tripId)
                    .orElseThrow(() -> new IllegalArgumentException("Trip not found"));

            Traveler traveler = travelerRepository.findById(travelerId)
                    .orElseThrow(() -> new IllegalArgumentException("Traveler not found"));

            DepartureAirport airport = departureAirportRepository.findById(departureAirportId)
                    .orElseThrow(() -> new IllegalArgumentException("Airport not found"));

            TemporaryParticipantList tempList =
                    temporaryParticipantListRepository.findById(tempListId)
                            .orElseThrow(() -> new IllegalArgumentException("Temp participant list not found"));

            // Ricostruzione participants
            List<Participant> participants = tempList.getParticipants().stream()
                    .map(tp -> {
                        Participant p = new Participant();
                        p.setFirstName(tp.getFirstName());
                        p.setLastName(tp.getLastName());
                        p.setDateOfBirth(tp.getDateOfBirth());
                        return p;
                    })
                    .collect(Collectors.toCollection(ArrayList::new));

            Booking booking = new Booking();
            booking.setBookingDate(LocalDate.now());
            booking.setTrip(trip);
            booking.setTraveler(traveler);
            booking.setDepartureAirport(airport);
            booking.setParticipants(participants);
            booking.setInsuranceType(insuranceType);
            participants.forEach(p -> p.setBooking(booking));

            // Decorator
            IBooking decorated = decorations
                    .getOrDefault(insuranceType, b -> b)
                    .apply(booking);

            double totalCost = decorated.getTotalCost();
            double insuranceCost = decorated.getInsuranceCost();



            // Payment entity
            // faccio attenzione a non salvare l'intero PaymentIntent di Stripe, ma solo i dati rilevanti
            Payment payment = new Payment();
            payment.setBooking(booking);
            payment.setStatus("PAID");
            payment.setPaymentDate(LocalDate.now());
            payment.setPaymentIntentId(intent.getId());
            payment.setPaymentMethod(intent.getPaymentMethod());
            payment.setAmountPaid(totalCost);
            payment.setCurrency("eur");
            payment.setAmountInsurance(insuranceCost);

            booking.setPayment(payment);

            //System.out.println(">>> SALVATAGGIO BOOKING IN CORSO...");
            // salva booking (cascade salva anche participants e payment)
            bookingRepository.save(booking);
            //System.out.println(">>> BOOKING SALVATO! ID = " + booking.getId());

            // elimino infine la lista temporanea di partecipanti
            temporaryParticipantListRepository.delete(tempList);

            // aggiorna trip state
            // l'aggiornamento avviene solo se cambia lo stato
            TripState before = trip.getState();
            trip.handle();
            if (!before.getClass().equals(trip.getState().getClass())) {
                tripRepository.save(trip);
            }
            // email conferma
            emailService.sendHtmlMessage(
                    traveler.getEmail(),
                    "Prenotazione confermata",
                    "/mail/booking-confirmation",
                    Map.of("totalCost", totalCost, "booking", booking, "trip", trip, "traveler", traveler, "airport", airport, "homepage", baseUrl+"/home")
            );

        } catch (Exception e) {

            System.out.println("❌ ERRORE NELLA FINALIZZAZIONE DEL BOOKING:");
            e.printStackTrace();
        }

    }
}

