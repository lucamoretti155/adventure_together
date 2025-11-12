package com.lucamoretti.adventure_together.scheduler;

import com.lucamoretti.adventure_together.service.review.ReviewService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/*
 Pianificatore per l'invio di email di promemoria per le recensioni.
 Ogni giorno alle 22:00 invia email agli utenti che devono lasciare recensioni sui viaggi completati.
 Viene utilizzato il ReviewService per gestire l'invio delle email.
 Estrae i Trip conclusi (3 giorni prima) e invia email di promemoria agli utenti partecipanti che non hanno ancora lasciato una recensione.
 */

@Slf4j
@Component
@RequiredArgsConstructor
public class ReviewReminderScheduler {

    private final ReviewService reviewService;

    @Scheduled(cron = "0 0 22 * * *") // ogni giorno alle 22:00
    public void sendReviewReminders() {
        log.info("Avvio schedulazione reminder recensioni...");
        reviewService.sendReviewReminderEmails();
    }
}
