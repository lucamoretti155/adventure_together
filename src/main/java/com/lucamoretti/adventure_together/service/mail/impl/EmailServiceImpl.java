package com.lucamoretti.adventure_together.service.mail.impl;

import com.lucamoretti.adventure_together.service.mail.EmailService;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.spring6.SpringTemplateEngine;
import org.thymeleaf.context.Context;
import java.util.Map;

/*
 Service per l'invio di email HTML utilizzando template Thymeleaf.
 Implementa il metodo per inviare email specificando destinatario, oggetto, nome del template e variabili da sostituire nel template.
 Utilizza JavaMailSender per l'invio delle email e SpringTemplateEngine per il rendering dei template.
*/

@Service
@RequiredArgsConstructor
public class EmailServiceImpl implements EmailService {
    // Dipendenze necessarie per l'invio delle email e il rendering dei template
    private final JavaMailSender mailSender;
    private final SpringTemplateEngine templateEngine;

    // Indirizzo email del mittente, configurato in application.properties dell'applicazione
    @Value("${spring.mail.username}")
    private String from;

    // Metodo per inviare un'email HTML utilizzando un template Thymeleaf
    @Override
    public void sendHtmlMessage(String to, String subject, String templateName, Map<String, Object> variables) {
        try {
            Context context = new Context(); // Contesto per il template Thymeleaf
            context.setVariables(variables); // Imposta le variabili da sostituire nel template
            String htmlContent = templateEngine.process(templateName, context); // Genera il contenuto HTML dell'email

            MimeMessage message = mailSender.createMimeMessage(); // Crea un nuovo messaggio MIME (Multipurpose Internet Mail Extensions)
            MimeMessageHelper helper = new MimeMessageHelper(message, true); // Helper per facilitare la creazione del messaggio
            helper.setFrom(from); // Imposta il mittente
            helper.setTo(to);   // Imposta il destinatario
            helper.setSubject(subject); // Imposta l'oggetto dell'email
            helper.setText(htmlContent, true); // Imposta il contenuto HTML dell'email

            mailSender.send(message);
        } catch (Exception e) {
            throw new RuntimeException("Failed to send email", e);
        }
    }
}

