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
import com.lucamoretti.adventure_together.service.validation.DataValidationService;
import com.lucamoretti.adventure_together.util.exception.DataIntegrityException;
import com.lucamoretti.adventure_together.util.exception.DuplicateResourceException;
import com.lucamoretti.adventure_together.util.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import com.lucamoretti.adventure_together.service.mail.EmailService;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

/*
  Implementazione del Service per la gestione degli User
  Fornisce metodi per la registrazione(Traveler, Planner e Admin), ricerca e disattivazione degli utenti
  Fornisce anche funzionalità per il reset della password
*/

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
    private final DataValidationService dataValidationService; // servizio per la validazione dei dati
    private static final int EXPIRATION_HOURS = 24; // durata validità token reset password

    @Value("${app.base-url}")
    private String baseUrl;


    // restituisce tutti gli user come lista di UserDTO
    @Override
    public List<UserDTO> getAllUsers() {
        return userRepository.findAll().stream()
                .map(UserDTO::fromEntity)
                .toList();
    }

    // restituisce tutti i planner come lista di PlannerDTO
    @Override
    public List<PlannerDTO> getAllPlanners() {
        return plannerRepository.findAllPlannersNoAdmin().stream()
                .map(PlannerDTO::fromEntity)
                .toList();
    }

    // restituisce tutti gli admin come lista di AdminDTO
    @Override
    public List<AdminDTO> getAllAdmins() {
        return adminRepository.findAll().stream()
                .map(AdminDTO::fromEntity)
                .toList();
    }

    // restituisce tutti i traveler come lista di TravelerDTO
    @Override
    public Optional<TravelerDTO> getTravelerById(Long id) {
        return travelerRepository.findById(id).map(TravelerDTO::fromEntity);
    }

    // restituisce uno User dato l'email
    @Override
    public Optional<UserDTO> getByEmail(String email) {
        return userRepository.findByEmail(email).map(UserDTO::fromEntity);
    }

    // questo metodo registra un viaggiatore nel sistema
    @Override
    public TravelerDTO registerTraveler(TravelerDTO dto, String rawPassword) {
        // valida i dati del viaggiatore (età e password) usando il metodo presente in DataValidationService
        dataValidationService.validateTraveler(dto, rawPassword);

        // controlla se l'email è già registrata
        if (userRepository.existsByEmail(dto.getEmail()))
            throw new DuplicateResourceException("Email già presente");

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

        // manda infine una email di benvenuto al nuovo viaggiatore
        emailService.sendHtmlMessage(
                dto.getEmail(),  // destinatario
                "Benvenuto in AdventureTogether!",  // oggetto
                "mail/welcome-traveler", // path al template Thymeleaf
                Map.of("name", traveler.getFirstName(), "homepage", baseUrl+"/home")  // variabili per il template inserite in una Map
        );

        return TravelerDTO.fromEntity(traveler);
    }

    // questo metodo registra un planner nel sistema
    // sarà accedibile solo agli adminù
    // la password viene creata automaticamente dal servizio passorwordGeneratorService e passata via controller
    // l'utente riceverà una email che lo inviterà a resettare la password al primo accesso
    @Override
    public PlannerDTO registerPlanner(PlannerDTO dto, String rawPassword) {
        if (userRepository.existsByEmail(dto.getEmail()))
            throw new DuplicateResourceException("Email già presente");

        Planner planner = new Planner();
        planner.setFirstName(dto.getFirstName());
        planner.setLastName(dto.getLastName());
        planner.setEmail(dto.getEmail());
        planner.setPassword(passwordEncoder.encode(rawPassword));
        planner.setActive(true);
        planner.setRole(Role.PLANNER.name());
        planner.setEmployeeId(dto.getEmployeeId());
        planner = plannerRepository.save(planner);

        // manda infine una email di benvenuto al nuovo planner
        emailService.sendHtmlMessage(
                dto.getEmail(),  // destinatario
                "Benvenuto in AdventureTogether!",  // oggetto
                "mail/welcome-planner", // path al template Thymeleaf
                Map.of("name", planner.getFirstName(),
                        "resetPassword", baseUrl+ "/auth/forgot-password", "homepage", baseUrl+"/home")  // variabili per il template inserite in una Map
        );


        return PlannerDTO.fromEntity(planner);
    }

    // questo metodo registra un admin nel sistema
    // sarà accedibile solo ad altri admin
    // la password viene creata automaticamente dal servizio passorwordGeneratorService e passata via controller
    // l'utente riceverà una email che lo inviterà a resettare la password al primo accesso
    @Override
    public AdminDTO registerAdmin(AdminDTO dto, String rawPassword) {
        if (userRepository.existsByEmail(dto.getEmail()))
            throw new DuplicateResourceException("Email già presente");

        Admin admin = new Admin();
        admin.setFirstName(dto.getFirstName());
        admin.setLastName(dto.getLastName());
        admin.setEmail(dto.getEmail());
        admin.setPassword(passwordEncoder.encode(rawPassword));
        admin.setActive(true);
        admin.setRole(Role.ADMIN.name());
        admin.setEmployeeId(dto.getEmployeeId());
        admin = adminRepository.save(admin);

        // manda infine una email di benvenuto al nuovo admin

        emailService.sendHtmlMessage(
                dto.getEmail(),  // destinatario
                "Benvenuto in AdventureTogether!",  // oggetto
                "mail/welcome-planner", // path al template Thymeleaf
                Map.of("name", admin.getFirstName(),
                        "resetPassword", baseUrl+ "/auth/forgot-password", "homepage", baseUrl+"/home")  // variabili per il template inserite in una Map
        );

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

        // Genera link con il token
        String resetLink = baseUrl + "auth/reset-password?token=" + token;

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

        dataValidationService.validatePassword(newPassword); // valida la nuova password altrimenti lancia eccezione

        User user = resetToken.getUser(); // ottiene l'utente associato al token
        user.setPassword(passwordEncoder.encode(newPassword));      // aggiorna la password con quella nuova dell'utente
        userRepository.save(user);

        // elimina il token dopo l'uso
        passwordResetTokenRepository.delete(resetToken);
    }

    // metodo per disattivare un utente dato il suo id
    @Override
    public void deactivateUser(Long id) {
        userRepository.findById(id).ifPresent(user -> {
            user.setActive(false);
            userRepository.save(user);
        });
    }

    // metodo per riattivare un utente dato il suo id
    @Override
    public void activateUser(Long id) {
        userRepository.findById(id).ifPresent(user -> {
            user.setActive(true);
            userRepository.save(user);
        });
    }
}
