package com.lucamoretti.adventure_together.service.user.impl;

import com.lucamoretti.adventure_together.dto.user.AdminDTO;
import com.lucamoretti.adventure_together.dto.user.PlannerDTO;
import com.lucamoretti.adventure_together.dto.user.TravelerDTO;
import com.lucamoretti.adventure_together.dto.user.UserDTO;
import com.lucamoretti.adventure_together.model.auth.PasswordResetToken;
import com.lucamoretti.adventure_together.model.user.Admin;
import com.lucamoretti.adventure_together.model.user.Planner;
import com.lucamoretti.adventure_together.model.user.Traveler;
import com.lucamoretti.adventure_together.model.user.User;
import com.lucamoretti.adventure_together.repository.auth.PasswordResetTokenRepository;
import com.lucamoretti.adventure_together.repository.user.AdminRepository;
import com.lucamoretti.adventure_together.repository.user.PlannerRepository;
import com.lucamoretti.adventure_together.repository.user.TravelerRepository;
import com.lucamoretti.adventure_together.repository.user.UserRepository;
import com.lucamoretti.adventure_together.service.mail.EmailService;
import com.lucamoretti.adventure_together.service.validation.DataValidationService;
import com.lucamoretti.adventure_together.util.exception.DataIntegrityException;
import com.lucamoretti.adventure_together.util.exception.DuplicateResourceException;
import com.lucamoretti.adventure_together.util.exception.ResourceNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.test.util.ReflectionTestUtils;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;


import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    @Mock private UserRepository userRepository;
    @Mock private TravelerRepository travelerRepository;
    @Mock private PlannerRepository plannerRepository;
    @Mock private AdminRepository adminRepository;
    @Mock private PasswordResetTokenRepository passwordResetTokenRepository;
    @Mock private PasswordEncoder passwordEncoder;
    @Mock private EmailService emailService;
    @Mock private DataValidationService dataValidationService;

    @InjectMocks private UserServiceImpl userService;

    @BeforeEach
    void setup() {
        ReflectionTestUtils.setField(
                userService,
                "baseUrl",
                "http://localhost:8080"
        );
    }


    // ------------------------------------------------------------
    // GETTERS BASE
    // ------------------------------------------------------------

    @Test
    void getAllUsers_success() {
        User u = new Traveler();
        u.setId(1L);
        when(userRepository.findAll()).thenReturn(List.of(u));

        List<UserDTO> result = userService.getAllUsers();
        assertEquals(1, result.size());
        assertEquals(1L, result.get(0).getId());
    }

    @Test
    void getAllPlanners_success() {
        Planner p = new Planner();
        p.setId(2L);
        when(plannerRepository.findAllPlannersNoAdmin()).thenReturn(List.of(p));

        List<PlannerDTO> result = userService.getAllPlanners();
        assertEquals(1, result.size());
        assertEquals(2L, result.get(0).getId());
    }

    @Test
    void getAllAdmins_success() {
        Admin a = new Admin();
        a.setId(3L);

        when(adminRepository.findAll()).thenReturn(List.of(a));

        List<AdminDTO> result = userService.getAllAdmins();
        assertEquals(1, result.size());
        assertEquals(3L, result.get(0).getId());
    }

    @Test
    void getTravelerById_success() {
        Traveler t = new Traveler();
        t.setId(44L);
        when(travelerRepository.findById(44L)).thenReturn(Optional.of(t));

        Optional<TravelerDTO> result = userService.getTravelerById(44L);
        assertTrue(result.isPresent());
        assertEquals(44L, result.get().getId());
    }

    @Test
    void getByEmail_success() {
        User u = new Traveler();
        u.setId(9L);

        when(userRepository.findByEmail("test")).thenReturn(Optional.of(u));

        Optional<UserDTO> result = userService.getByEmail("test");
        assertTrue(result.isPresent());
        assertEquals(9L, result.get().getId());
    }

    // ------------------------------------------------------------
    // REGISTER TRAVELER
    // ------------------------------------------------------------

    @Test
    void registerTraveler_success() {

        TravelerDTO dto = TravelerDTO.travelerBuilder()
                .firstName("Mario")
                .lastName("Rossi")
                .email("mail@test.com")
                .dateOfBirth(LocalDate.of(1990, 5, 5))
                .telephone("123")
                .build();

        Traveler saved = new Traveler();
        saved.setId(5L);
        saved.setFirstName("Mario");

        when(userRepository.existsByEmail("mail@test.com")).thenReturn(false);
        when(passwordEncoder.encode("pwd")).thenReturn("ENCODED");
        when(travelerRepository.save(any())).thenReturn(saved);

        TravelerDTO result = userService.registerTraveler(dto, "pwd");

        assertEquals(5L, result.getId());
        verify(emailService, times(1)).sendHtmlMessage(any(), any(), any(), any());
        verify(dataValidationService).validateTraveler(dto, "pwd");
    }

    @Test
    void registerTraveler_duplicateEmail() {
        TravelerDTO dto = TravelerDTO.travelerBuilder()
                .email("mail@test.com")
                .build();

        when(userRepository.existsByEmail("mail@test.com")).thenReturn(true);

        assertThrows(DuplicateResourceException.class,
                () -> userService.registerTraveler(dto, "pwd"));
    }

    // ------------------------------------------------------------
    // REGISTER PLANNER
    // ------------------------------------------------------------

    @Test
    void registerPlanner_success() {
        PlannerDTO dto = PlannerDTO.plannerBuilder()
                .id(null)
                .firstName("A")
                .lastName("B")
                .employeeId("E01")
                .email("mail@test.com")
                .build();

        Planner saved = new Planner();
        saved.setId(10L);
        saved.setFirstName("A");

        when(userRepository.existsByEmail("mail@test.com")).thenReturn(false);
        when(passwordEncoder.encode("pwd")).thenReturn("ENC");
        when(plannerRepository.save(any())).thenReturn(saved);

        PlannerDTO result = userService.registerPlanner(dto, "pwd");

        assertEquals(10L, result.getId());
        verify(emailService, times(1)).sendHtmlMessage(any(), any(), any(), any());
    }

    @Test
    void registerPlanner_duplicateEmail() {
        PlannerDTO dto = new PlannerDTO();
        dto.setEmail("mail@test.com");

        when(userRepository.existsByEmail("mail@test.com")).thenReturn(true);
        assertThrows(DuplicateResourceException.class,
                () -> userService.registerPlanner(dto, "pwd"));
    }

    // ------------------------------------------------------------
    // REGISTER ADMIN
    // ------------------------------------------------------------

    @Test
    void registerAdmin_success() {
        AdminDTO dto = AdminDTO.adminBuilder()
                .id(null)
                .email("admin@test.com")
                .firstName("Admin")
                .lastName("Test")
                .employeeId("X1")
                .build();

        Admin saved = new Admin();
        saved.setId(11L);
        saved.setFirstName("Admin");

        when(userRepository.existsByEmail("admin@test.com")).thenReturn(false);
        when(passwordEncoder.encode("pwd")).thenReturn("ENC");
        when(adminRepository.save(any())).thenReturn(saved);

        AdminDTO result = userService.registerAdmin(dto, "pwd");
        assertEquals(11L, result.getId());
        verify(emailService).sendHtmlMessage(any(), any(), any(), any());
    }

    @Test
    void registerAdmin_duplicateEmail() {
        AdminDTO dto = new AdminDTO();
        dto.setEmail("admin@test.com");

        when(userRepository.existsByEmail("admin@test.com")).thenReturn(true);
        assertThrows(DuplicateResourceException.class,
                () -> userService.registerAdmin(dto, "pwd"));
    }

    // ------------------------------------------------------------
    // GENERATE PASSWORD RESET TOKEN
    // ------------------------------------------------------------

    @Test
    void generatePasswordResetToken_success() {
        User u = new Traveler();
        u.setEmail("mail@test.com");
        u.setFirstName("Mario");

        when(userRepository.findByEmail("mail@test.com")).thenReturn(Optional.of(u));

        String token = userService.generatePasswordResetToken("mail@test.com");

        assertNotNull(token);
        verify(passwordResetTokenRepository).deleteByUser(u);
        verify(passwordResetTokenRepository).save(any());
        verify(emailService).sendHtmlMessage(any(), any(), any(), any());
    }

    @Test
    void generatePasswordResetToken_userNotFound() {
        when(userRepository.findByEmail("mail@test.com")).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class,
                () -> userService.generatePasswordResetToken("mail@test.com"));
    }

    // ------------------------------------------------------------
    // RESET PASSWORD
    // ------------------------------------------------------------

    @Test
    void resetPassword_success() {
        User u = new Traveler();
        PasswordResetToken token = PasswordResetToken.builder()
                .token("abc")
                .expiryDate(LocalDateTime.now().plusHours(1))
                .user(u)
                .build();

        when(passwordResetTokenRepository.findByToken("abc"))
                .thenReturn(Optional.of(token));

        userService.resetPassword("abc", "newPwd");

        verify(dataValidationService).validatePassword("newPwd");
        verify(passwordEncoder).encode("newPwd");
        verify(userRepository).save(u);
        verify(passwordResetTokenRepository).delete(token);
    }

    @Test
    void resetPassword_tokenExpired() {
        PasswordResetToken token = PasswordResetToken.builder()
                .token("abc")
                .expiryDate(LocalDateTime.now().minusHours(1))
                .user(new Traveler())
                .build();

        when(passwordResetTokenRepository.findByToken("abc"))
                .thenReturn(Optional.of(token));

        assertThrows(IllegalArgumentException.class,
                () -> userService.resetPassword("abc", "pwd"));

        verify(passwordResetTokenRepository).delete(token);
    }

    @Test
    void resetPassword_tokenNotFound() {
        when(passwordResetTokenRepository.findByToken("abc"))
                .thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class,
                () -> userService.resetPassword("abc", "pwd"));
    }

    // ------------------------------------------------------------
    // ACTIVATE / DEACTIVATE
    // ------------------------------------------------------------

    @Test
    void deactivateUser_success() {
        User u = new Planner();
        u.setId(1L);

        when(userRepository.findById(1L)).thenReturn(Optional.of(u));

        userService.deactivateUser(1L);

        assertFalse(u.isActive());
        verify(userRepository).save(u);
    }

    @Test
    void activateUser_success() {
        User u = new Planner();
        u.setId(1L);

        when(userRepository.findById(1L)).thenReturn(Optional.of(u));

        userService.activateUser(1L);

        assertTrue(u.isActive());
        verify(userRepository).save(u);
    }

    // ------------------------------------------------------------
    // GET CURRENT USER ID
    // ------------------------------------------------------------

    @Test
    void getCurrentUserId_success() {
        User u = new Planner();
        u.setId(99L);

        UsernamePasswordAuthenticationToken auth =
                new UsernamePasswordAuthenticationToken(u, null);

        SecurityContextHolder.getContext().setAuthentication(auth);

        Long result = userService.getCurrentUserId();
        assertEquals(99L, result);
    }

    @Test
    void getCurrentUserId_failure() {
        UsernamePasswordAuthenticationToken auth =
                new UsernamePasswordAuthenticationToken("anonymous", null);

        SecurityContextHolder.getContext().setAuthentication(auth);

        assertThrows(DataIntegrityException.class,
                () -> userService.getCurrentUserId());
    }
}