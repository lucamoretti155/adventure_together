package com.lucamoretti.adventure_together.service.review.impl;

import com.lucamoretti.adventure_together.dto.review.ReviewDTO;
import com.lucamoretti.adventure_together.model.booking.Booking;
import com.lucamoretti.adventure_together.model.review.Review;
import com.lucamoretti.adventure_together.model.trip.Trip;
import com.lucamoretti.adventure_together.model.trip.TripItinerary;
import com.lucamoretti.adventure_together.model.user.Traveler;
import com.lucamoretti.adventure_together.repository.review.ReviewRepository;
import com.lucamoretti.adventure_together.repository.trip.TripRepository;
import com.lucamoretti.adventure_together.repository.user.TravelerRepository;
import com.lucamoretti.adventure_together.service.mail.EmailService;
import com.lucamoretti.adventure_together.util.exception.ResourceNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.time.LocalDate;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ReviewServiceImplTest {

    @Mock private ReviewRepository reviewRepository;
    @Mock private TripRepository tripRepository;
    @Mock private TravelerRepository travelerRepository;
    @Mock private EmailService emailService;

    @InjectMocks private ReviewServiceImpl reviewService;

    // ---------------------------------------------------------------------
    // CREATE REVIEW
    // ---------------------------------------------------------------------

    @Test
    void createReview_successful() {

        Long tripId = 1L;
        Long travelerId = 10L;

        // Trip concluso
        Trip trip = new Trip();
        trip.setId(tripId);
        trip.setDateReturn(LocalDate.now().minusDays(1));

        // Traveler
        Traveler traveler = new Traveler();
        traveler.setId(travelerId);

        // Booking → partecipazione confermata
        Booking booking = new Booking();
        booking.setTraveler(traveler);
        trip.setBookings(Set.of(booking));

        ReviewDTO dto = new ReviewDTO();
        dto.setScore(5);
        dto.setTextReview("Ottimo viaggio!");

        Review savedEntity = new Review();
        savedEntity.setId(99L);
        savedEntity.setScore(5);

        when(tripRepository.findById(tripId)).thenReturn(Optional.of(trip));
        when(travelerRepository.findById(travelerId)).thenReturn(Optional.of(traveler));
        when(reviewRepository.existsByTrip_IdAndTraveler_Id(tripId, travelerId)).thenReturn(false);
        when(reviewRepository.save(any(Review.class))).thenReturn(savedEntity);

        ReviewDTO result = reviewService.createReview(tripId, travelerId, dto);

        assertNotNull(result);
        assertEquals(99L, result.getId());
        assertEquals(5, result.getScore());
    }

    @Test
    void createReview_fails_whenTripNotFound() {
        when(tripRepository.findById(1L)).thenReturn(Optional.empty());
        assertThrows(IllegalArgumentException.class,
                () -> reviewService.createReview(1L, 2L, new ReviewDTO()));
    }
    @Test
    void createReview_fails_whenTravelerNotFound() {
        Trip trip = new Trip();
        trip.setDateReturn(LocalDate.now().minusDays(1));

        when(tripRepository.findById(1L)).thenReturn(Optional.of(trip));
        when(travelerRepository.findById(2L)).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class,
                () -> reviewService.createReview(1L, 2L, new ReviewDTO()));
    }

    @Test
    void createReview_fails_whenTripNotFinished() {
        Trip trip = new Trip();
        trip.setDateReturn(LocalDate.now().plusDays(1)); // futuro

        Traveler t = new Traveler();
        t.setId(10L);

        when(tripRepository.findById(1L)).thenReturn(Optional.of(trip));
        when(travelerRepository.findById(10L)).thenReturn(Optional.of(t));

        assertThrows(IllegalStateException.class,
                () -> reviewService.createReview(1L, 10L, new ReviewDTO()));
    }

    @Test
    void createReview_fails_whenTravelerDidNotParticipate() {
        Trip trip = new Trip();
        trip.setDateReturn(LocalDate.now().minusDays(1));
        trip.setBookings(Collections.emptySet());

        Traveler traveler = new Traveler();
        traveler.setId(10L);

        when(tripRepository.findById(1L)).thenReturn(Optional.of(trip));
        when(travelerRepository.findById(10L)).thenReturn(Optional.of(traveler));

        assertThrows(IllegalStateException.class,
                () -> reviewService.createReview(1L, 10L, new ReviewDTO()));
    }

    @Test
    void createReview_fails_whenDuplicate() {
        Trip trip = new Trip();
        trip.setDateReturn(LocalDate.now().minusDays(1));

        Traveler traveler = new Traveler();
        traveler.setId(10L);

        Booking b = new Booking();
        b.setTraveler(traveler);
        trip.setBookings(Set.of(b));

        when(tripRepository.findById(1L)).thenReturn(Optional.of(trip));
        when(travelerRepository.findById(10L)).thenReturn(Optional.of(traveler));
        when(reviewRepository.existsByTrip_IdAndTraveler_Id(1L, 10L)).thenReturn(true);

        assertThrows(IllegalStateException.class,
                () -> reviewService.createReview(1L, 10L, new ReviewDTO()));
    }

    // ---------------------------------------------------------------------
    // GET TRAVELER REVIEWS
    // ---------------------------------------------------------------------
    @Test
    void getTravelerReviews_success() {
        Review r = new Review();
        r.setId(10L);

        when(reviewRepository.findByTraveler_Id(1L)).thenReturn(List.of(r));

        List<ReviewDTO> result = reviewService.getTravelerReviews(1L);

        assertEquals(1, result.size());
        assertEquals(10L, result.get(0).getId());
    }

    // ---------------------------------------------------------------------
    // GET TRIP REVIEWS
    // ---------------------------------------------------------------------
    @Test
    void getTripReviews_success() {
        Review r = new Review();
        r.setId(10L);

        when(reviewRepository.findByTrip_Id(1L)).thenReturn(List.of(r));

        List<ReviewDTO> result = reviewService.getTripReviews(1L);

        assertEquals(1, result.size());
    }

    // ---------------------------------------------------------------------
    // GET PENDING REVIEWS
    // ---------------------------------------------------------------------
    @Test
    void getPendingReviews_success() {

        Traveler t = new Traveler();
        t.setId(10L);

        Trip trip = new Trip();
        trip.setId(1L);
        trip.setDateReturn(LocalDate.now().minusDays(5));

        Booking b = new Booking();
        b.setTraveler(t);
        trip.setBookings(Set.of(b));

        TripItinerary ti = new TripItinerary();
        ti.setTitle("Titolo");
        trip.setTripItinerary(ti);

        when(tripRepository.findAll()).thenReturn(List.of(trip));
        when(reviewRepository.existsByTrip_IdAndTraveler_Id(1L, 10L)).thenReturn(false);

        List<ReviewDTO> result = reviewService.getPendingReviews(10L);

        assertEquals(1, result.size());
        assertEquals("Titolo", result.get(0).getTripTitle());
    }

    // ---------------------------------------------------------------------
    // SEND REMINDERS
    // ---------------------------------------------------------------------
    @Test
    void sendReviewReminderEmails_success() {

        Traveler t = new Traveler();
        t.setId(10L);
        t.setEmail("mail@test.com");

        Booking b = new Booking();
        b.setTraveler(t);

        Trip trip = new Trip();
        trip.setId(99L);
        trip.setDateReturn(LocalDate.now().minusDays(3));
        trip.setBookings(Set.of(b));

        when(tripRepository.findByDateReturnEquals(any())).thenReturn(List.of(trip));
        when(reviewRepository.existsByTrip_IdAndTraveler_Id(99L, 10L)).thenReturn(false);

        assertDoesNotThrow(() -> reviewService.sendReviewReminderEmails());
        verify(emailService, times(1)).sendHtmlMessage(any(), any(), any(), any());
    }

    // ---------------------------------------------------------------------
    // GET ALL REVIEWS BY TRIP ITINERARY
    // ---------------------------------------------------------------------
    @Test
    void getAllReviewsByTripItineraryId_success() {
        Review r = new Review();
        r.setId(1L);

        when(reviewRepository.findAllByTripItinerary_Id(5L))
                .thenReturn(List.of(r));

        List<ReviewDTO> result = reviewService.getAllReviewsByTripItineraryId(5L);

        assertEquals(1, result.size());
        assertEquals(1L, result.get(0).getId());
    }

    // ---------------------------------------------------------------------
    // AVERAGE SCORE
    // ---------------------------------------------------------------------
    @Test
    void getAverageScore_success() {
        Review r1 = new Review();
        r1.setScore(4);
        Review r2 = new Review();
        r2.setScore(2);

        when(reviewRepository.findAllByTripItinerary_Id(3L))
                .thenReturn(List.of(r1, r2));

        Float avg = reviewService.getAverageScoreForTripItinerary(3L);

        assertEquals(3.0f, avg);
    }

    @Test
    void getAverageScore_nullWhenEmpty() {
        when(reviewRepository.findAllByTripItinerary_Id(3L))
                .thenReturn(Collections.emptyList());

        assertNull(reviewService.getAverageScoreForTripItinerary(3L));
    }

    // ---------------------------------------------------------------------
    // GET REVIEW BY TRIP + TRAVELER
    // ---------------------------------------------------------------------
    @Test
    void getReviewByTripIdAndTravelerId_success() {
        Review r = new Review();
        r.setId(10L);

        when(reviewRepository.findByTripIdAndTravelerId(1L, 2L)).thenReturn(Optional.of(r));

        ReviewDTO result = reviewService.getReviewByTripIdAndTravelerId(1L, 2L);
        assertEquals(10L, result.getId());
    }

    @Test
    void getReviewByTripIdAndTravelerId_notFound() {
        when(reviewRepository.findByTripIdAndTravelerId(1L, 2L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> reviewService.getReviewByTripIdAndTravelerId(1L, 2L));
    }
}
