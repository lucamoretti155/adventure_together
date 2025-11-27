package com.lucamoretti.adventure_together.controller.traveler;

import com.lucamoretti.adventure_together.dto.booking.BookingDTO;
import com.lucamoretti.adventure_together.dto.details.DepartureAirportDTO;
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
import com.lucamoretti.adventure_together.util.exception.UnauthorizedActionException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import java.util.ArrayList;
import java.util.List;

/*
  Controller per le funzionalità riservate ai Traveler.
  Gestisce la visualizzazione della dashboard, delle prenotazioni,
  dei dettagli delle prenotazioni e la creazione di recensioni.
 */

@Controller
@RequestMapping("/traveler")
@RequiredArgsConstructor
public class TravelerController {

    private final TripService tripService;
    private final TripItineraryService tripItineraryService;
    private final BookingService bookingService;
    private final DepartureAirportService airportService; // se lo hai
    private final PaymentService paymentService;
    private final ReviewService reviewService;
    private final UserService userService;


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
        DepartureAirportDTO airport = airportService.getDepartureAirportById(booking.getDepartureAirportId());
        model.addAttribute("airport", airport);
        PaymentDTO payment = paymentService.getPaymentById(booking.getPayment().getId());
        model.addAttribute("payment", payment);

        ReviewDTO review = null;
        try {
            review = reviewService.getReviewByTripIdAndTravelerId(trip.getId(), user.getId());
        } catch (ResourceNotFoundException e) {
            // Nessuna review → review rimane null
        }
        model.addAttribute("review", review);
        return "traveler/booking-details";
    }

    // Mostra il form per scrivere una recensione per un trip
    @GetMapping("/review/{bookingId}")
    public String showReviewForm(@PathVariable Long bookingId,
                                 Model model) {
        ReviewDTO dto;
        if (model.containsAttribute("reviewDTO")) {
            dto = (ReviewDTO) model.getAttribute("reviewDTO");
        } else {
            dto = new ReviewDTO();
        }
        BookingDTO booking = bookingService.getBookingById(bookingId);
        Long travelerId = userService.getCurrentUserId();
        if (!booking.getTravelerId().equals(travelerId)) {
            throw new UnauthorizedActionException("Non sei autorizzato a recensire questa prenotazione.");
        }
        TripDTO trip = tripService.getById(booking.getTripId());

        // precompila i campi nascosti
        dto.setTripId(booking.getTripId());
        dto.setTravelerId(travelerId);
        model.addAttribute("booking", booking);
        model.addAttribute("trip", trip);
        model.addAttribute("reviewDTO", dto);
        return "traveler/review-form";
    }

    // Gestisce la creazione di una recensione per un trip
    @PostMapping("/review/{bookingId}")
    public String submitReview(@PathVariable Long bookingId,
                               @Valid @ModelAttribute("reviewDTO") ReviewDTO reviewDTO,
                               BindingResult bindingResult,
                               RedirectAttributes redirectAttributes) {
        BookingDTO booking = bookingService.getBookingById(bookingId);
        Long travelerId = userService.getCurrentUserId();
        if (!booking.getTravelerId().equals(travelerId)) {
            throw new ResourceNotFoundException("Booking", "id", bookingId);
        }
        if (bindingResult.hasErrors()) {
            redirectAttributes.addFlashAttribute("org.springframework.validation.BindingResult.reviewDTO", bindingResult);
            redirectAttributes.addFlashAttribute("reviewDTO", reviewDTO);
            return "redirect:/traveler/review/" + bookingId;
        }
        try {
            reviewService.createReview(booking.getTripId(),travelerId, reviewDTO);
            redirectAttributes.addFlashAttribute("successMessage", "Recensione inviata con successo!");
            return "redirect:/traveler/booking-details/" + bookingId;
        } catch (ResourceNotFoundException | IllegalStateException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
            redirectAttributes.addFlashAttribute("reviewDTO", reviewDTO);
            return "redirect:/traveler/review/" + bookingId;
        }
    }

}





