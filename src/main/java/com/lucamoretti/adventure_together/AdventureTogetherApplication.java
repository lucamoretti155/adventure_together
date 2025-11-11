package com.lucamoretti.adventure_together;

import com.lucamoretti.adventure_together.service.mail.EmailService;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class AdventureTogetherApplication {

	public static void main(String[] args) {
		SpringApplication.run(AdventureTogetherApplication.class, args);
	}

    /*
    test per invio email all'avvio dell'applicazione

    @Autowired
    private EmailService mailService;

    @PostConstruct
    public void testMail() {
        mailService.sendTestMail();
    }

    */
}
