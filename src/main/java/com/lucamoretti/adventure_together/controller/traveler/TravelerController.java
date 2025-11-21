package com.lucamoretti.adventure_together.controller.traveler;

import com.lucamoretti.adventure_together.dto.booking.BookingDTO;
import com.lucamoretti.adventure_together.dto.participant.ParticipantDTO;
import com.lucamoretti.adventure_together.dto.payment.PaymentDTO;
import com.lucamoretti.adventure_together.dto.review.ReviewDTO;
import com.lucamoretti.adventure_together.dto.trip.TripDTO;
import com.lucamoretti.adventure_together.dto.trip.TripItineraryDTO;
import com.lucamoretti.adventure_together.dto.user.TravelerDTO;
import com.lucamoretti.adventure_together.model.booking.Booking;
import com.lucamoretti.adventure_together.model.user.Traveler;
import com.lucamoretti.adventure_together.model.user.User;
import com.lucamoretti.adventure_together.service.booking.BookingService;
import com.lucamoretti.adventure_together.service.details.DepartureAirportService;
import com.lucamoretti.adventure_together.service.payment.PaymentService;
import com.lucamoretti.adventure_together.service.review.ReviewService;
import com.lucamoretti.adventure_together.service.trip.TripItineraryService;
import com.lucamoretti.adventure_together.service.trip.TripService;
import com.lucamoretti.adventure_together.service.user.UserService;
import com.lucamoretti.adventure_together.util.exception.ResourceNotFoundException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping("/traveler")
@RequiredArgsConstructor
public class TravelerController {

    private final TripService tripService;
    private final TripItineraryService tripItineraryService;
    private final BookingService bookingService;
    private final DepartureAirportService airportService; // se lo hai
    private final UserService userService;
    private final PaymentService paymentService;
    private final ReviewService reviewService;


    // Mostra la dashboard per Traveler
    @GetMapping("/dashboard")
    public String showTravelerDashboard() {
        return "traveler/dashboard";
    }

    // Visualizza la lista delle prenotazioni del traveler
    @GetMapping("/bookings-list")
    public String viewBookings(@AuthenticationPrincipal User user,
                               Model model) {
        List<BookingDTO> bookings = bookingService.getBookingsByTravelerId(user.getId());
        model.addAttribute("bookings", bookings);
        return "traveler/bookings-list";
    }


    // Visualizza i dettagli di una prenotazione specifica
    @GetMapping("/booking/{bookingId}")
    public String viewBookingDetails(@PathVariable Long bookingId,
                                     @AuthenticationPrincipal User user,
                                     Model model) {
        BookingDTO booking = bookingService.getBookingById(bookingId);
        if (!booking.getTravelerId().equals(user.getId())) {
            throw new ResourceNotFoundException("Booking", "id", bookingId);
        }
        model.addAttribute("booking", booking);
        TripDTO trip = tripService.getById(booking.getTripId());
        model.addAttribute("trip", trip);
        TripItineraryDTO itin = tripItineraryService.getById(trip.getTripItineraryId());
        model.addAttribute("itinerary", itin);
        PaymentDTO payment = paymentService.getPaymentById(booking.getPayment().getId());
        model.addAttribute("payment", payment);
        ReviewDTO review = reviewService.getReviewByTripIdAndTravelerId(trip.getId(), user.getId());
        model.addAttribute("review", review);
        return "traveler/booking-details";
    }


}





