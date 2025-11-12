package com.lucamoretti.adventure_together.service.review.impl;

import com.lucamoretti.adventure_together.dto.review.ReviewDTO;
import com.lucamoretti.adventure_together.model.review.Review;
import com.lucamoretti.adventure_together.model.trip.Trip;
import com.lucamoretti.adventure_together.model.user.Traveler;
import com.lucamoretti.adventure_together.repository.review.ReviewRepository;
import com.lucamoretti.adventure_together.repository.trip.TripRepository;
import com.lucamoretti.adventure_together.repository.user.TravelerRepository;
import com.lucamoretti.adventure_together.service.mail.EmailService;
import com.lucamoretti.adventure_together.service.review.ReviewService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

/*
    Implementazione del servizio per la gestione delle recensioni
    Consente di creare recensioni, recuperare recensioni di traveler e viaggi,
    ottenere viaggi completati ma non recensiti, inviare email di promemoria
    e calcolare il punteggio medio delle recensioni per un itinerario specifico.
 */


@Service
@RequiredArgsConstructor
@Transactional
public class ReviewServiceImpl implements ReviewService {

    private final ReviewRepository reviewRepository;
    private final TripRepository tripRepository;
    private final TravelerRepository travelerRepository;
    private final EmailService emailService;

    // Crea una nuova recensione per un viaggio da parte di un traveler
    @Override
    public ReviewDTO createReview(Long tripId, Long travelerId, String textReview, int score) {

        // Recupero entità correlate
        Trip trip = tripRepository.findById(tripId)
                .orElseThrow(() -> new IllegalArgumentException("Trip non trovato"));
        Traveler traveler = travelerRepository.findById(travelerId)
                .orElseThrow(() -> new IllegalArgumentException("Traveler non trovato"));

        // Controllo viaggio concluso
        if (trip.getDateReturn().isAfter(LocalDate.now()))
            throw new IllegalStateException("Non è possibile recensire un viaggio non ancora concluso.");

        // Controllo partecipazione del traveler
        // passo per i Booking del Trip  e verifico se il traveler ha prenotato
        boolean participated = trip.getBookings().stream()
                .anyMatch(b -> b.getTraveler().getId().equals(travelerId));
        if (!participated)
            throw new IllegalStateException("Solo i partecipanti possono lasciare una recensione.");

        // Controllo recensione duplicata
        if (reviewRepository.existsByTrip_IdAndTraveler_Id(tripId, travelerId))
            throw new IllegalStateException("Hai già lasciato una recensione per questo viaggio.");

        // Creazione e salvataggio recensione
        Review review = new Review();
        review.setTrip(trip);
        review.setTraveler(traveler);
        review.setTextReview(textReview);
        review.setScore(score);
        reviewRepository.save(review);

        return ReviewDTO.fromEntity(review);
    }

    // Ritorna tutte le recensioni di un traveler
    @Override
    public List<ReviewDTO> getTravelerReviews(Long travelerId) {
        return reviewRepository.findByTraveler_Id(travelerId)
                .stream()
                .map(ReviewDTO::fromEntity)
                .toList();
    }

    // Ritorna tutte le recensioni associate a un Trip
    @Override
    public List<ReviewDTO> getTripReviews(Long tripId) {
        return reviewRepository.findByTrip_Id(tripId)
                .stream()
                .map(ReviewDTO::fromEntity)
                .toList();
    }

    // Ritorna i viaggi completati ma non recensiti da un traveler
    // Scorre tutti i viaggi e filtra quelli con data di ritorno passata
    // e senza recensione del traveler e quindi crea una ReviewDTO per ciascuno e li mette in una lista
    @Override
    public List<ReviewDTO> getPendingReviews(Long travelerId) {
        // Recupera i viaggi completati ma non recensiti
        LocalDate today = LocalDate.now();
        return tripRepository.findAll().stream()
                .filter(trip ->
                        trip.getDateReturn().isBefore(today)
                                && trip.getBookings().stream()
                                .anyMatch(b -> b.getTraveler().getId().equals(travelerId))
                                && !reviewRepository.existsByTrip_IdAndTraveler_Id(trip.getId(), travelerId)
                )
                .map(trip -> ReviewDTO.builder()
                        .tripId(trip.getId())
                        .tripTitle(trip.getTripItinerary().getTitle())
                        .travelerId(travelerId)
                        .alreadyReviewed(false)
                        .build())
                .toList();
    }

    // Invia email di promemoria per lasciare recensioni ai traveler che non hanno recensito
    // i viaggi terminati da 3 giorni
    @Override
    public void sendReviewReminderEmails() {
        LocalDate threeDaysAgo = LocalDate.now().minusDays(3);

        // Recupera viaggi terminati esattamente tre giorni fa
        List<Trip> recentTrips = tripRepository.findByDateReturnEquals(threeDaysAgo);

        for (Trip trip : recentTrips) {
            for (var booking : trip.getBookings()) {
                Traveler traveler = booking.getTraveler();
                boolean reviewed = reviewRepository.existsByTrip_IdAndTraveler_Id(trip.getId(), traveler.getId());
                if (!reviewed) {
                    try {
                        emailService.sendHtmlMessage(
                                traveler.getEmail(),
                                "Lascia una recensione per il tuo viaggio!",
                                "mail/review-reminder",
                                Map.of("traveler", traveler, "trip", trip)
                        );
                    } catch (Exception e) {
                        throw new RuntimeException("Errore invio email a " + traveler.getEmail(), e);
                    }
                }
            }
        }
    }
    // Recupera tutte le recensioni associate ai viaggi di un itinerario specifico
    @Override
    public List<ReviewDTO> getAllReviewsByTripItineraryId(Long tripItineraryId) {
        // Recupera tutte le recensioni associate ai viaggi di un itinerario specifico
        return reviewRepository.findAllByTripItinerary_Id(tripItineraryId)
                .stream()
                .map(ReviewDTO::fromEntity)
                .toList();
    }

    // Calcola il punteggio medio delle recensioni per un itinerario specifico
    @Override
    public Float getAverageScoreForTripItinerary(Long tripItineraryId) {
        List<Review> reviews = reviewRepository.findAllByTripItinerary_Id(tripItineraryId);
        if (reviews.isEmpty()) {
            return null; // Nessuna recensione disponibile

        } else {
            // Calcola la somma dei punteggi
            int totalScore = reviews.stream()
                    .mapToInt(Review::getScore)
                    .sum();
            // Calcola e ritorna la media
            return (float) totalScore / reviews.size();
        }
    }
}
