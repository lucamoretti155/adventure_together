package com.lucamoretti.adventure_together.service.user.impl;

import com.lucamoretti.adventure_together.dto.user.AdminDTO;
import com.lucamoretti.adventure_together.dto.user.PlannerDTO;
import com.lucamoretti.adventure_together.dto.user.TravelerDTO;
import com.lucamoretti.adventure_together.dto.user.UserDTO;
import com.lucamoretti.adventure_together.model.auth.PasswordResetToken;
import com.lucamoretti.adventure_together.model.user.*;
import com.lucamoretti.adventure_together.repository.auth.PasswordResetTokenRepository;
import com.lucamoretti.adventure_together.repository.user.AdminRepository;
import com.lucamoretti.adventure_together.repository.user.PlannerRepository;
import com.lucamoretti.adventure_together.repository.user.TravelerRepository;
import com.lucamoretti.adventure_together.repository.user.UserRepository;
import com.lucamoretti.adventure_together.service.user.UserService;
import com.lucamoretti.adventure_together.util.exception.EmailAlreadyRegisteredException;
import com.lucamoretti.adventure_together.util.exception.ResourceNotFoundException;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import com.lucamoretti.adventure_together.service.mail.EmailService;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

// Implementazione del Service per la gestione degli User
// Fornisce metodi per la registrazione, ricerca e disattivazione degli utenti
// Fornisce anche funzionalità per il reset della password
// Utilizza DTO per il trasferimento dei dati degli utenti
// Interagisce con i repository per la persistenza dei dati

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final TravelerRepository travelerRepository;
    private final PlannerRepository plannerRepository;
    private final AdminRepository adminRepository;
    private final PasswordResetTokenRepository passwordResetTokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final EmailService emailService; // servizio per invio mail

    private static final int EXPIRATION_HOURS = 24; // durata validità token reset password

    @Override
    public List<UserDTO> findAllUsers() {
        return userRepository.findAll().stream()
                .map(UserDTO::fromEntity)
                .toList();
    }

    @Override
    public Optional<UserDTO> findById(Long id) {
        return userRepository.findById(id).map(UserDTO::fromEntity);
    }

    @Override
    public Optional<UserDTO> findByEmail(String email) {
        return userRepository.findByEmail(email).map(UserDTO::fromEntity);
    }

    @Override
    public TravelerDTO registerTraveler(TravelerDTO dto, String rawPassword) {
        if (userRepository.existsByEmail(dto.getEmail()))
            throw new EmailAlreadyRegisteredException("Email già presente");

        Traveler traveler = new Traveler();
        traveler.setFirstName(dto.getFirstName());
        traveler.setLastName(dto.getLastName());
        traveler.setEmail(dto.getEmail());
        traveler.setPassword(passwordEncoder.encode(rawPassword));
        traveler.setActive(true);
        traveler.setRole(Role.TRAVELER.name());
        traveler.setDateOfBirth(dto.getDateOfBirth());
        traveler.setTelephone(dto.getTelephone());
        traveler = travelerRepository.save(traveler);
        return TravelerDTO.fromEntity(traveler);
    }

    // questo metodo registra un planner nel sistema
    // sarà accedibile solo agli admin
    // non uso @PreAuthorize qui perchè la sicurezza è gestita a livello di controller
    @Override
    public PlannerDTO registerPlanner(PlannerDTO dto, String rawPassword) {
        if (userRepository.existsByEmail(dto.getEmail()))
            throw new EmailAlreadyRegisteredException("Email già presente");

        Planner planner = new Planner();
        planner.setFirstName(dto.getFirstName());
        planner.setLastName(dto.getLastName());
        planner.setEmail(dto.getEmail());
        planner.setPassword(passwordEncoder.encode(rawPassword));
        planner.setActive(false); // verrà attivato al primo reset della password
        planner.setRole(Role.PLANNER.name());
        planner.setEmployeeId(dto.getEmployeeId());
        planner = plannerRepository.save(planner);
        return PlannerDTO.fromEntity(planner);
    }

    // questo metodo registra un admin nel sistema
    // sarà accedibile solo ad altri admin
    // non uso @PreAuthorize qui perchè la sicurezza è gestita a livello di controller
    @Override
    public AdminDTO registerAdmin(AdminDTO dto, String rawPassword) {
        if (userRepository.existsByEmail(dto.getEmail()))
            throw new EmailAlreadyRegisteredException("Email già presente");

        Admin admin = new Admin();
        admin.setFirstName(dto.getFirstName());
        admin.setLastName(dto.getLastName());
        admin.setEmail(dto.getEmail());
        admin.setPassword(passwordEncoder.encode(rawPassword));
        admin.setActive(true);
        admin.setRole(Role.ADMIN.name());
        admin.setEmployeeId(dto.getEmployeeId());
        admin = adminRepository.save(admin);
        return AdminDTO.fromEntity(admin);
    }

    // Genera un token per il reset della password e invia una email all'utente
    // Il token ha una validità di EXPIRATION_HOURS ore qui impostata a 24 ore
    // Il link di reset è personalizzabile
    @Override
    public String generatePasswordResetToken(String email) {
        User user = userRepository.findByEmail(email) // trova l'utente per email
                .orElseThrow(() -> new ResourceNotFoundException("User","email", email));

        // Elimina token precedente (se esiste)
        passwordResetTokenRepository.deleteByUser(user);

        String token = UUID.randomUUID().toString(); // genera un token unico
        PasswordResetToken resetToken = PasswordResetToken.builder() // crea l'entity del token tramite builder
                .token(token)
                .user(user)
                .expiryDate(LocalDateTime.now().plusHours(EXPIRATION_HOURS))
                .build();

        passwordResetTokenRepository.save(resetToken); // salva il token nel repository

        // Genera link (personalizzabile)
        String resetLink = "http://localhost:8080/auth/reset-password?token=" + token;

        // Invia email tramite il emailService
        emailService.sendHtmlMessage(
                user.getEmail(),  // destinatario
                "Reset Password - AdventureTogether",  // oggetto
                "mail/reset-password", // path al template Thymeleaf
                Map.of("name", user.getFirstName(), "resetLink", resetLink)  // variabili per il template inserite in una Map
        );
        return token;
    }

    // Resetta la password utilizzando il token fornito
    // Verifica che il token sia valido e non scaduto
    // Aggiorna la password dell'utente e attiva l'account (nel caso di Planner/Admin creati e non ancora attivi)
    // Elimina il token dopo l'uso
    @Override
    public void resetPassword(String token, String newPassword) {
        PasswordResetToken resetToken = passwordResetTokenRepository.findByToken(token) // trova il token nel repository
                .orElseThrow(() -> new IllegalArgumentException("Invalid or expired token"));

        if (resetToken.isExpired()) {   // verifica se il token è scaduto
            passwordResetTokenRepository.delete(resetToken);  // elimina il token scaduto
            throw new IllegalArgumentException("Reset token expired");  // lancia eccezione
        }

        User user = resetToken.getUser(); // ottiene l'utente associato al token
        user.setPassword(passwordEncoder.encode(newPassword));      // aggiorna la password con quella nuova dell'utente
        user.setActive(true); // attiva l'account, rilevante solo per Planner/Admin creati e non ancora attivi
        userRepository.save(user);

        // elimina il token dopo l'uso
        passwordResetTokenRepository.delete(resetToken);
    }


    @Override
    public void deactivateUser(Long id) {
        userRepository.findById(id).ifPresent(user -> {
            user.setActive(false);
            userRepository.save(user);
        });
    }
}
