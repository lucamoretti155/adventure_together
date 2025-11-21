package com.lucamoretti.adventure_together;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling // Abilita il supporto per le attività pianificate
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

    /*
    Test per integrazione Stripe all'avvio dell'applicazione
    @Bean
    CommandLineRunner testStripe(StripeClient stripeClient) {
        return args -> {
            var intent = stripeClient.createPaymentIntent(10.0, "eur");
            System.out.println("PaymentIntent ID: " + intent.getPaymentIntentId());
        };
    }
    */

}
