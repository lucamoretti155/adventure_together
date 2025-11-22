package com.lucamoretti.adventure_together.scheduler;

import com.lucamoretti.adventure_together.model.trip.Trip;
import com.lucamoretti.adventure_together.repository.trip.TripRepository;
import com.lucamoretti.adventure_together.service.mail.EmailService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

/*
  Scheduler per inviare promemoria via email ai viaggiatori
  con viaggi in partenza tra 7 giorni.
  Lo scheduler viene eseguito ogni giorno alle 9:00.
 */

@Slf4j
@Component
@RequiredArgsConstructor
public class UpcomingTripReminderScheduler {

    private final TripRepository tripRepository;
    private final EmailService emailService;

    @Scheduled(cron = "0 0 9 * * *") // ogni giorno alle 9:00
    @Transactional
    public void sendUpcomingTripReminders() {
        log.info("Avvio schedulazione upcoming Trip...");
        LocalDate targetDate = LocalDate.now().plusDays(7);
        List<Trip> trips = tripRepository.findByDateDepartureEquals(targetDate);

        for (Trip trip : trips) {
            trip.getBookings().forEach(booking -> {
                var traveler = booking.getTraveler();
                try {
                    emailService.sendHtmlMessage(
                            traveler.getEmail(),
                            "Il tuo viaggio è vicino!",
                            "mail/upcoming-trip-reminder",
                            Map.of("traveler", traveler, "trip", trip)
                    );
                } catch (Exception e) {
                    log.error("Errore invio reminder partenza a {}: {}", traveler.getEmail(), e.getMessage());
                }
            });
        }
    }
}

