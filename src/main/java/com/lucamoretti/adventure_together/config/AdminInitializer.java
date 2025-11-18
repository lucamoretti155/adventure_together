package com.lucamoretti.adventure_together.config;

import com.lucamoretti.adventure_together.model.user.Admin;
import com.lucamoretti.adventure_together.model.user.Role;
import com.lucamoretti.adventure_together.repository.user.AdminRepository;
import com.lucamoretti.adventure_together.repository.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

// Classe che inizializza un admin di default all'avvio dell'applicazione
// Se non sono presenti utenti nel database, viene creato un admin con email "demo.mail.app.java.project@gmail.com" e password "admin123"

@Order(1)
@Component
@RequiredArgsConstructor
public class AdminInitializer implements CommandLineRunner {

    private final AdminRepository adminRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {
        if (adminRepository.count() == 0) {
            Admin admin = new Admin();
            admin.setFirstName("System");
            admin.setLastName("Administrator");
            admin.setEmail("demo.mail.app.java.project@gmail.com");
            admin.setPassword(passwordEncoder.encode("admin123"));
            admin.setActive(true);
            admin.setRole(Role.ADMIN.name());
            admin.setEmployeeId("241414");

            adminRepository.save(admin);
            System.out.println("***   Default Admin created: demo.mail.app.java.project@gmail.com / admin123   ***" );
        }
    }
}

