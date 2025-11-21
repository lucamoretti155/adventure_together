package com.lucamoretti.adventure_together.service.booking.impl;

import com.lucamoretti.adventure_together.dto.booking.BookingDTO;
import com.lucamoretti.adventure_together.dto.participant.ParticipantDTO;
import com.lucamoretti.adventure_together.model.booking.Booking;
import com.lucamoretti.adventure_together.model.booking.IBooking;
import com.lucamoretti.adventure_together.model.booking.decorator.CancellationInsurance;
import com.lucamoretti.adventure_together.model.booking.decorator.LuggageInsurance;
import com.lucamoretti.adventure_together.model.details.DepartureAirport;
import com.lucamoretti.adventure_together.model.participant.Participant;
import com.lucamoretti.adventure_together.model.payment.Payment;
import com.lucamoretti.adventure_together.model.trip.Trip;
import com.lucamoretti.adventure_together.model.trip.state.TripState;
import com.lucamoretti.adventure_together.model.user.Traveler;
import com.lucamoretti.adventure_together.repository.booking.BookingRepository;
import com.lucamoretti.adventure_together.repository.details.DepartureAirportRepository;
import com.lucamoretti.adventure_together.repository.trip.TripRepository;
import com.lucamoretti.adventure_together.repository.user.TravelerRepository;
import com.lucamoretti.adventure_together.service.booking.BookingFinalizeService;
import com.lucamoretti.adventure_together.service.booking.BookingSerializerService;
import com.lucamoretti.adventure_together.service.mail.EmailService;
import com.stripe.model.PaymentIntent;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

/*
 Implementazione del servizio per la finalizzazione della prenotazione.
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

    private final Map<String, Function<IBooking, IBooking>> decorations = Map.of(
            "basic", b -> b,
            "cancellation", CancellationInsurance::new,
            "luggage", LuggageInsurance::new,
            "full", b -> new CancellationInsurance(new LuggageInsurance(b))
    );

    @Override
    public void finalizeBooking(PaymentIntent intent) {


        System.out.println("=== FINALIZE BOOKING TRIGGERED ===");
        System.out.println("INTENT ID: " + intent.getId());
        System.out.println("INTENT METADATA: " + intent.getMetadata());

        if (intent.getMetadata() == null) {
            System.out.println("❌ PaymentIntent metadata NULL, impossibile finalizzare.");
            return;
        }
        try {
            String metadataJson = intent.getMetadata().get("booking");

            if (metadataJson == null) {
                System.out.println("❌ metadataJson == NULL — Booking NON ricostruibile");
                return;
            }

            System.out.println("metadataJson = " + metadataJson);

            BookingDTO dto = bookingSerializerService.deserializeBooking(metadataJson);

            Trip trip = tripRepository.findById(dto.getTripId()).orElseThrow();
            Traveler traveler = travelerRepository.findById(dto.getTravelerId()).orElseThrow();
            DepartureAirport airport = departureAirportRepository.findById(dto.getDepartureAirportId()).orElseThrow();

            // Ricostruzione participants
            List<Participant> participants = dto.getParticipants().stream()
                    .map(ParticipantDTO::toEntity)
                    .toList();

            // Crea booking completo
            Booking booking = dto.toEntity(trip, traveler, airport, participants);

            // Decorator
            IBooking decorated = decorations
                    .getOrDefault(dto.getInsuranceType(), b -> b)
                    .apply(booking);

            double totalCost = decorated.getTotalCost();

            // Payment entity
            Payment payment = new Payment();
            payment.setBooking(booking);
            payment.setStatus("PAID");
            payment.setPaymentDate(LocalDate.now());
            payment.setPaymentIntentId(intent.getId());
            payment.setPaymentMethod(intent.getPaymentMethod());
            payment.setAmountPaid(totalCost);
            payment.setCurrency("eur");
            payment.setAmountInsurance(decorated.getInsuranceCost());

            booking.setPayment(payment);

            System.out.println(">>> SALVATAGGIO BOOKING IN CORSO...");
            bookingRepository.save(booking);
            System.out.println(">>> BOOKING SALVATO! ID = " + booking.getId());

            // aggiorna trip state
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
                    Map.of("booking", booking, "trip", trip, "traveler", traveler, "airport", airport)
            );

        } catch (Exception e) {

            System.out.println("❌ ERRORE NELLA FINALIZZAZIONE DEL BOOKING:");
            e.printStackTrace();
        }

    }
}

