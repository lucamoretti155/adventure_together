package com.lucamoretti.adventure_together.service.booking.impl;

import com.lucamoretti.adventure_together.dto.booking.BookingDTO;
import com.lucamoretti.adventure_together.dto.participant.ParticipantDTO;
import com.lucamoretti.adventure_together.dto.payment.PaymentIntentDTO;
import com.lucamoretti.adventure_together.model.booking.Booking;
import com.lucamoretti.adventure_together.model.booking.IBooking;
import com.lucamoretti.adventure_together.model.booking.decorator.CancellationInsurance;
import com.lucamoretti.adventure_together.model.booking.decorator.LuggageInsurance;
import com.lucamoretti.adventure_together.model.details.DepartureAirport;
import com.lucamoretti.adventure_together.model.participant.Participant;
import com.lucamoretti.adventure_together.model.trip.Trip;
import com.lucamoretti.adventure_together.model.user.Traveler;
import com.lucamoretti.adventure_together.repository.details.DepartureAirportRepository;
import com.lucamoretti.adventure_together.repository.trip.TripRepository;
import com.lucamoretti.adventure_together.repository.user.TravelerRepository;
import com.lucamoretti.adventure_together.service.booking.BookingPreparationService;
import com.lucamoretti.adventure_together.service.booking.BookingSerializerService;
import com.lucamoretti.adventure_together.service.payment.impl.StripeClient;
import com.lucamoretti.adventure_together.util.exception.ResourceNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.function.Function;

/*
 Implementazione del servizio per la preparazione della prenotazione e del pagamento.
 */

@Service
@RequiredArgsConstructor
@Transactional
public class BookingPreparationServiceImpl implements BookingPreparationService {

    private final TripRepository tripRepository;
    private final TravelerRepository travelerRepository;
    private final DepartureAirportRepository departureAirportRepository;
    private final StripeClient stripeClient;
    private final BookingSerializerService bookingSerializerService;

    // Mappa decorator
    private final Map<String, Function<IBooking, IBooking>> decorations = Map.of(
            "basic", b -> b,
            "cancellation", CancellationInsurance::new,
            "luggage", LuggageInsurance::new,
            "full", b -> new CancellationInsurance(new LuggageInsurance(b))
    );

    @Override
    public PaymentIntentDTO startBookingAndPayment(BookingDTO req) {

        Trip trip = tripRepository.findById(req.getTripId())
                .orElseThrow(() -> new ResourceNotFoundException("Trip", "id", req.getTripId()));

        Traveler traveler = travelerRepository.findById(req.getTravelerId())
                .orElseThrow(() -> new ResourceNotFoundException("Traveler", "id", req.getTravelerId()));

        DepartureAirport airport = departureAirportRepository.findById(req.getDepartureAirportId())
                .orElseThrow(() -> new ResourceNotFoundException("DepartureAirport", "id", req.getDepartureAirportId()));

        // Validazione disponibilità
        if (!trip.getState().canAcceptBooking()) {
            throw new IllegalStateException("Il viaggio non è prenotabile in questo stato.");
        }
        if (!trip.hasAvailableSpots(req.getParticipants().size())) {
            throw new IllegalArgumentException("Posti insufficienti.");
        }

        // Converto i participant DTO in entity (solo per calcolo)
        List<Participant> participants = req.getParticipants().stream()
                .map(ParticipantDTO::toEntity)
                .toList();

        // Creazione booking transitorio
        Booking temp = req.toEntity(trip, traveler, airport, participants);

        // Decorator
        IBooking decorated = decorations
                .getOrDefault(req.getInsuranceType(), b -> b)
                .apply(temp);

        double totalCost = decorated.getTotalCost();

        // Serializzo tutto per Stripe metadata
        String metadataJson = bookingSerializerService.serializeBooking(req);

        // PaymentIntent
        PaymentIntentDTO intent = stripeClient.createPaymentIntent(
                totalCost,
                "eur",
                metadataJson  // aggiungiamo metadata
        );

        return intent;
    }
}

