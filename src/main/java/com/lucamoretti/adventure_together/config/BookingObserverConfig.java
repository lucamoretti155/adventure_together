package com.lucamoretti.adventure_together.config;

import com.lucamoretti.adventure_together.model.booking.Booking;
import com.lucamoretti.adventure_together.service.mail.EmailService;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;

/*
   Configurazione per l'Observer delle prenotazioni.
   Inizializza il servizio email per la classe Booking, permettendo l'invio di notifiche via email
   quando lo stato del Trip associato a una prenotazione cambia.
*/

@Configuration
@RequiredArgsConstructor
public class BookingObserverConfig {

    private final EmailService emailService;

    @PostConstruct
    public void init() {
        Booking.setEmailService(emailService);
    }
}
