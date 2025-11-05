package com.lucamoretti.adventure_together.config;

import com.lucamoretti.adventure_together.model.user.Admin;
import com.lucamoretti.adventure_together.model.user.Role;
import com.lucamoretti.adventure_together.repository.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

// Classe che inizializza un admin di default all'avvio dell'applicazione
// Se non sono presenti utenti nel database, viene creato un admin con email "demo.mail.app.java.project@gmail.com" e password "admin123"

@Component
@RequiredArgsConstructor
public class AdminInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {
        if (userRepository.count() == 0) {
            Admin admin = new Admin();
            admin.setFirstName("System");
            admin.setLastName("Administrator");
            admin.setEmail("demo.mail.app.java.project@gmail.com");
            admin.setPassword(passwordEncoder.encode("admin123"));
            admin.setActive(true);
            admin.setRole(Role.ADMIN.name());
            admin.setEmployeeId((long)241414);

            userRepository.save(admin);
            System.out.println("***   Default Admin created: demo.mail.app.java.project@gmail.com / admin123   ***" );
        }
    }
}

