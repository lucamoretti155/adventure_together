package com.lucamoretti.adventure_together.service.mail;

import java.util.Map;

// Service per l'invio di email HTML utilizzando template
// Fornisce un metodo per inviare email specificando destinatario, oggetto, nome del template e variabili da sostituire nel template
// Implementato da EmailServiceImpl

public interface EmailService {
    void sendHtmlMessage(String to, String subject, String templateName, Map<String, Object> variables);
    public void sendTestMail(String emailTo);
}

