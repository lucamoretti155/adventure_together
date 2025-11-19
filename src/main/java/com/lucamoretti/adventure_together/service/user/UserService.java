package com.lucamoretti.adventure_together.service.user;

import com.lucamoretti.adventure_together.dto.user.AdminDTO;
import com.lucamoretti.adventure_together.dto.user.PlannerDTO;
import com.lucamoretti.adventure_together.dto.user.TravelerDTO;
import com.lucamoretti.adventure_together.dto.user.UserDTO;

import java.util.List;
import java.util.Optional;

// Service per la gestione degli utenti
// Fornisce metodi per la registrazione, ricerca e disattivazione degli utenti
// Utilizza DTO per il trasferimento dei dati degli utenti
// Implementato da UserServiceImpl

public interface UserService {
    List<UserDTO> getAllUsers();
    List<PlannerDTO> getAllPlanners();
    List<AdminDTO> getAllAdmins();
    Optional<TravelerDTO> getTravelerById(Long id);
    Optional<UserDTO> getByEmail(String email);

    TravelerDTO registerTraveler(TravelerDTO travelerDTO, String rawPassword);
    PlannerDTO registerPlanner(PlannerDTO plannerDTO, String rawPassword);
    AdminDTO registerAdmin(AdminDTO adminDTO, String rawPassword);

    String generatePasswordResetToken(String email);
    void resetPassword(String token, String newPassword);

    void deactivateUser(Long id);
    void activateUser(Long id);


}

