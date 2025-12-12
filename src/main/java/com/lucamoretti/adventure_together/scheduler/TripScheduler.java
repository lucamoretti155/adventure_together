package com.lucamoretti.adventure_together.scheduler;

import com.lucamoretti.adventure_together.model.trip.Trip;
import com.lucamoretti.adventure_together.model.trip.state.TripState;
import com.lucamoretti.adventure_together.repository.trip.TripRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import java.util.List;

/*
 Pianificatore per l'aggiornamento automatico dello stato dei viaggi.
 Ogni giorno alle 2:00 controlla i viaggi aperti per prenotazioni
 e aggiorna quelli scaduti o confermati.
 */

@Slf4j
@Component
@RequiredArgsConstructor
public class TripScheduler {

    private final TripRepository tripRepository;

    @Scheduled(cron = "0 0  2 * * *")
    @Transactional
    public void updateTripStates() {
        log.info("Avvio schedulazione aggiornamento TripState...");
        List<Trip> trips = tripRepository.findOpenForBooking();

        for (Trip trip : trips) {
            try {
                TripState before = trip.getState();
                trip.handle();
                TripState after = trip.getState();
                if (!after.getClass().equals(before.getClass())) {
                    tripRepository.save(trip);
                }
            } catch (Exception e) {
                log.error("Errore aggiornando stato Trip {}: {}", trip.getId(), e.getMessage());
            }
        }
    }
}

