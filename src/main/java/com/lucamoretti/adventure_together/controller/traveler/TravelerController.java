package com.lucamoretti.adventure_together.controller.traveler;

import com.lucamoretti.adventure_together.dto.booking.BookingDTO;
import com.lucamoretti.adventure_together.dto.participant.ParticipantDTO;
import com.lucamoretti.adventure_together.dto.trip.TripDTO;
import com.lucamoretti.adventure_together.dto.trip.TripItineraryDTO;
import com.lucamoretti.adventure_together.dto.user.TravelerDTO;
import com.lucamoretti.adventure_together.model.user.Traveler;
import com.lucamoretti.adventure_together.model.user.User;
import com.lucamoretti.adventure_together.service.booking.BookingService;
import com.lucamoretti.adventure_together.service.details.DepartureAirportService;
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

    @GetMapping("/book/{tripId}")
    public String showBookingForm(@PathVariable Long tripId,
                                  @AuthenticationPrincipal User user,
                                  Model model) {



        TripDTO trip = tripService.getById(tripId);
        TravelerDTO traveler = userService.getTravelerById(user.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Traveler","id", user.getId()));
        BookingDTO form = new BookingDTO();
        form.setTripId(tripId);
        form.setTravelerId(user.getId());

        // aggiungo un participant vuoto
        form.getParticipants().add(new ParticipantDTO());

        TripItineraryDTO itin = tripItineraryService.getById(trip.getTripItineraryId());

        model.addAttribute("trip", trip);
        model.addAttribute("booking", form);
        model.addAttribute("airports",
                airportService.getDepartureAirportsBySetOfIds(itin.getDepartureAirportIds()));

        return "traveler/booking-form";
    }

    @PostMapping("/book/{tripId}")
    public String createBooking(@PathVariable Long tripId,
                                @AuthenticationPrincipal User user,
                                @Valid @ModelAttribute("booking") BookingDTO form,
                                BindingResult bindingResult,
                                Model model,
                                RedirectAttributes redirectAttributes) {

        if (bindingResult.hasErrors()) {
            System.out.println(bindingResult);
            return "redirect:traveler/booking/{tripId}";
        }

        try {
            form.setTripId(tripId);
            form.setTravelerId(user.getId());

            BookingDTO created = bookingService.createBooking(
                    form,
                    form.getInsuranceType()
            );

            // Redirect verso la pagina di pagamento con il clientSecret
            redirectAttributes.addAttribute("bookingId", created.getId());
            redirectAttributes.addAttribute("clientSecret", created.getPayment().getClientSecret());

            return "redirect:/traveler/pay";

        } catch (Exception e) {
            bindingResult.reject("booking.error", e.getMessage());
            e.printStackTrace();
            return "redirect:traveler/booking/{tripId}";
        }
    }


    @GetMapping("/pay")
    public String showPaymentPage(@RequestParam String clientSecret,
                                  @RequestParam Long bookingId,
                                  Model model) {

        model.addAttribute("clientSecret", clientSecret);
        model.addAttribute("bookingId", bookingId);

        return "traveler/payment-page";
    }

    @GetMapping("/payment-success")
    public String paymentSuccess(@RequestParam Long bookingId, Model model) {

        BookingDTO booking = bookingService.getBookingById(bookingId);

        model.addAttribute("booking", booking);

        return "traveler/payment-success";
    }
}





