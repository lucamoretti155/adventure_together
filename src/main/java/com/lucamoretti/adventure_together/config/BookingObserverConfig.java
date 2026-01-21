package com.lucamoretti.adventure_together.config;

import com.lucamoretti.adventure_together.model.booking.Booking;
import com.lucamoretti.adventure_together.service.mail.EmailService;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;

// Collega il servizio email alle prenotazioni all'avvio dell'app
// Configurazione per l'Observer delle prenotazioni.

@Configuration
@RequiredArgsConstructor
public class BookingObserverConfig {

    private final EmailService emailService;

    @PostConstruct
    public void init() {
        Booking.setEmailService(emailService);
    }
}
