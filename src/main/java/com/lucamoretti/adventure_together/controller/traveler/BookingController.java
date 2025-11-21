package com.lucamoretti.adventure_together.controller.traveler;

import com.lucamoretti.adventure_together.dto.booking.BookingDTO;
import com.lucamoretti.adventure_together.dto.details.DepartureAirportDTO;
import com.lucamoretti.adventure_together.dto.participant.ParticipantDTO;
import com.lucamoretti.adventure_together.dto.payment.PaymentIntentDTO;
import com.lucamoretti.adventure_together.dto.trip.TripDTO;
import com.lucamoretti.adventure_together.dto.trip.TripItineraryDTO;
import com.lucamoretti.adventure_together.model.trip.Trip;
import com.lucamoretti.adventure_together.repository.details.DepartureAirportRepository;
import com.lucamoretti.adventure_together.repository.trip.TripRepository;
import com.lucamoretti.adventure_together.service.booking.BookingPreparationService;
import com.lucamoretti.adventure_together.service.details.DepartureAirportService;
import com.lucamoretti.adventure_together.service.trip.TripItineraryService;
import com.lucamoretti.adventure_together.service.trip.TripService;
import com.lucamoretti.adventure_together.service.user.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

/*
    Controller per la gestione delle prenotazioni (booking) da parte dei traveler.
    Si occupa di mostrare il form di prenotazione, avviare il processo di checkout con Stripe (fase 1)
    e mostrare la pagina di successo al completamento della prenotazione.
    Le altre fasi del processo di prenotazione (finalizzazione post-webhook) sono gestite altrove.
 */

@Controller
@RequestMapping("/bookings")
@RequiredArgsConstructor
public class BookingController {

    private final TripService tripService;
    private final BookingPreparationService bookingPreparationService;
    private final DepartureAirportService departureAirportService;
    private final TripItineraryService tripItineraryService;
    private final UserService userService;


    @Value("${STRIPE_PUBLIC_KEY}")
    private String stripePublicKey;

    // Mostra il form di prenotazione per un dato trip
    @GetMapping("/new/{tripId}")
    public String showBookingForm(@PathVariable Long tripId,
                                  Model model) {
        // ottieni i dettagli del trip
        TripDTO trip = tripService.getById(tripId);
        model.addAttribute("trip", trip);
        // ottieni l'ID del traveler autenticato
        Long travelerId = userService.getCurrentUserId();

        if (!model.containsAttribute("bookingDTO")) {
            BookingDTO dto = BookingDTO.builder()
                    .tripId(trip.getId())
                    .travelerId(travelerId)
                    .participants(List.of(new ParticipantDTO())) // almeno una riga
                    .build();
            model.addAttribute("bookingDTO", dto);
        }

        TripItineraryDTO itinerary = tripItineraryService.getById(trip.getTripItineraryId());
        List<DepartureAirportDTO> airports = departureAirportService.getDepartureAirportsBySetOfIds(itinerary.getDepartureAirportIds());
        model.addAttribute("airports", airports);

        return "booking/booking-form";
    }

    // Avvia la FASE 1: prepara booking + PaymentIntent, poi mostra pagina Stripe
    @PostMapping("/checkout")
    public String startCheckout(@Valid @ModelAttribute("bookingDTO") BookingDTO dto,
                                BindingResult result,
                                Model model,
                                RedirectAttributes redirectAttributes) {
        // ottieni l'utente autenticato
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        // verifica errori di validazione
        if (result.hasErrors()) {
            result.getAllErrors().forEach(err -> {
                System.out.println("ERROR: " + err.toString());
            });

            redirectAttributes.addFlashAttribute("org.springframework.validation.BindingResult.bookingDTO", result);
            redirectAttributes.addFlashAttribute("bookingDTO", dto);

            return "redirect:/bookings/new/" + dto.getTripId();
        }
        // avvia preparazione booking + PaymentIntent
        try {
            PaymentIntentDTO intent = bookingPreparationService.startBookingAndPayment(dto);

            model.addAttribute("clientSecret", intent.getClientSecret());
            model.addAttribute("stripePublicKey", stripePublicKey);
            model.addAttribute("tripId", dto.getTripId());

            int numParticipant = dto.getParticipants().size();
            model.addAttribute("numParticipant", numParticipant);
            double base = numParticipant * tripService.getById(dto.getTripId()).getTripIndividualCost();
            double total = intent.getTotal();
            double insurance = total - base;

            model.addAttribute("numParticipant", numParticipant);
            model.addAttribute("base", base);
            model.addAttribute("insurance", insurance);
            model.addAttribute("total", total);

            return "booking/booking-checkout"; // pagina con integrazione Stripe.js

        } catch (Exception ex) {
            ex.printStackTrace();
            redirectAttributes.addFlashAttribute("errorMessage",
                    "Errore nell'avvio del pagamento: " + ex.getMessage());
            redirectAttributes.addFlashAttribute("bookingDTO", dto);
            return "redirect:/bookings/new/" + dto.getTripId();
        }
    }

    // Mostra la pagina di successo dopo il completamento della prenotazione
    // per il failed viene fatto un flash message nella pagina di pagamento
    @GetMapping("/success")
    public String bookingSuccess() {
        return "booking/booking-success";
    }

}