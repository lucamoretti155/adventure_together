package com.lucamoretti.adventure_together.controller.auth;

import com.lucamoretti.adventure_together.dto.user.TravelerDTO;
import com.lucamoretti.adventure_together.service.user.UserService;
import com.lucamoretti.adventure_together.util.exception.DataIntegrityException;
import com.lucamoretti.adventure_together.util.exception.DuplicateResourceException;
import com.lucamoretti.adventure_together.util.exception.ResourceNotFoundException;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import org.springframework.ui.ExtendedModelMap;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthControllerTest {

    @Mock private UserService userService;
    @Mock private BindingResult bindingResult;
    @Mock private RedirectAttributes redirectAttributes;

    @InjectMocks
    private AuthController controller;

    private TravelerDTO travelerDTO;

    @BeforeEach
    void setup() {
        travelerDTO = TravelerDTO.travelerBuilder()
                .email("test@mail.com")
                .firstName("Luca")
                .lastName("Moretti")
                .role("TRAVELER")
                .active(true)
                .dateOfBirth(LocalDate.of(1990, 5, 15))
                .build();
    }

    /* -------------------------------------------------------
     * GET /auth/login
     * ------------------------------------------------------- */
    @Test
    void login_noParams_ok() {
        Model model = new ExtendedModelMap();

        String view = controller.showLoginPage(null, null, null, model);

        assertEquals("auth/login", view);
        assertFalse(model.containsAttribute("errorMessage"));
        assertFalse(model.containsAttribute("logout"));
        assertFalse(model.containsAttribute("registered"));
    }

    @Test
    void login_withMessages_ok() {
        Model model = new ExtendedModelMap();

        String view = controller.showLoginPage("1", "1", "1", model);

        assertEquals("auth/login", view);
        assertEquals("Credenziali non valide", model.getAttribute("errorMessage"));
        assertEquals("Logout effettuato con successo", model.getAttribute("logout"));
        assertEquals("Registrazione completata, effettua il login", model.getAttribute("registered"));
    }

    /* -------------------------------------------------------
     * GET /auth/register
     * ------------------------------------------------------- */
    @Test
    void showRegisterForm_freshModel_ok() {
        Model model = new ExtendedModelMap();

        String view = controller.showRegisterForm(model);

        assertEquals("auth/register", view);
        assertTrue(model.containsAttribute("travelerDTO"));
    }

    @Test
    void showRegisterForm_preservesExistingDTO() {
        Model model = new ExtendedModelMap();
        model.addAttribute("travelerDTO", travelerDTO);

        String view = controller.showRegisterForm(model);

        assertEquals("auth/register", view);
        assertEquals(travelerDTO, model.getAttribute("travelerDTO"));
    }

    /* -------------------------------------------------------
     * POST /auth/register
     * ------------------------------------------------------- */
    @Test
    void registerTraveler_validationErrors_redirectsBack() {
        when(bindingResult.hasErrors()).thenReturn(true);

        String view = controller.registerTraveler(
                "pass123",
                travelerDTO,
                bindingResult,
                redirectAttributes
        );

        assertEquals("redirect:/auth/register", view);
        verify(redirectAttributes).addFlashAttribute("travelerDTO", travelerDTO);
        verify(redirectAttributes).addFlashAttribute(
                eq("org.springframework.validation.BindingResult.travelerDTO"),
                eq(bindingResult)
        );
    }

    @Test
    void registerTraveler_ok_redirectsToLogin() {
        when(bindingResult.hasErrors()).thenReturn(false);

        String view = controller.registerTraveler(
                "pass123",
                travelerDTO,
                bindingResult,
                redirectAttributes
        );

        assertEquals("redirect:/auth/login", view);
        verify(userService).registerTraveler(travelerDTO, "pass123");
        verify(redirectAttributes).addFlashAttribute(
                eq("registered"),
                anyString()
        );
    }

    @Test
    void registerTraveler_duplicateEmail_redirectsBack() {
        when(bindingResult.hasErrors()).thenReturn(false);
        doThrow(new DuplicateResourceException("Email già esistente"))
                .when(userService).registerTraveler(travelerDTO, "pass123");

        String view = controller.registerTraveler(
                "pass123", travelerDTO, bindingResult, redirectAttributes
        );

        assertEquals("redirect:/auth/register", view);
        verify(redirectAttributes).addFlashAttribute("errorMessage", "Email già esistente");
    }

    @Test
    void registerTraveler_invalidData_redirectsBack() {
        when(bindingResult.hasErrors()).thenReturn(false);
        doThrow(new DataIntegrityException("Età non valida"))
                .when(userService).registerTraveler(travelerDTO, "pass123");

        String view = controller.registerTraveler(
                "pass123", travelerDTO, bindingResult, redirectAttributes
        );

        assertEquals("redirect:/auth/register", view);
        verify(redirectAttributes).addFlashAttribute("errorMessage", "Età non valida");
    }

    /* -------------------------------------------------------
     * GET /auth/forgot-password
     * ------------------------------------------------------- */
    @Test
    void showForgotPasswordForm_ok() {
        Model model = new ExtendedModelMap();

        String view = controller.showForgotPasswordForm(model);

        assertEquals("auth/forgot-password", view);
        assertEquals("", model.getAttribute("email"));
    }

    /* -------------------------------------------------------
     * POST /auth/forgot-password
     * ------------------------------------------------------- */
    @Test
    void processForgotPassword_ok() {
        String view = controller.processForgotPassword("mail@test.com", redirectAttributes);

        assertEquals("redirect:/auth/forgot-password", view);
        verify(userService).generatePasswordResetToken("mail@test.com");
        verify(redirectAttributes).addFlashAttribute("successMessage",
                "Email inviata! Controlla la tua casella di posta.");
    }

    @Test
    void processForgotPassword_notFound() {
        doThrow(new ResourceNotFoundException("Utente", "email", "x"))
                .when(userService).generatePasswordResetToken("x@mail.com");

        String view = controller.processForgotPassword("x@mail.com", redirectAttributes);

        assertEquals("redirect:/auth/forgot-password", view);
        verify(redirectAttributes).addFlashAttribute(
                "errorMessage", "Nessun account trovato con questa email."
        );
    }

    /* -------------------------------------------------------
     * GET /auth/reset-password
     * ------------------------------------------------------- */
    @Test
    void showResetPasswordForm_ok() {
        Model model = new ExtendedModelMap();

        String view = controller.showResetPasswordForm("TOKEN123", model);

        assertEquals("auth/reset-password", view);
        assertEquals("TOKEN123", model.getAttribute("token"));
    }

    /* -------------------------------------------------------
     * POST /auth/reset-password
     * ------------------------------------------------------- */
    @Test
    void processResetPassword_ok() {
        String view = controller.processResetPassword(
                "AAA", "newPass", redirectAttributes
        );

        assertEquals("redirect:/auth/login", view);
        verify(userService).resetPassword("AAA", "newPass");
    }

    @Test
    void processResetPassword_invalidToken_redirectsBack() {
        doThrow(new IllegalArgumentException("Token non valido"))
                .when(userService).resetPassword("BAD", "123");

        String view = controller.processResetPassword(
                "BAD", "123", redirectAttributes
        );

        assertEquals("redirect:/auth/reset-password?token=BAD", view);
        verify(redirectAttributes).addFlashAttribute("errorMessage", "Token non valido");
    }

    @Test
    void processResetPassword_invalidPassword_redirectsBack() {
        doThrow(new DataIntegrityException("Password debole"))
                .when(userService).resetPassword("A1", "weak");

        String view = controller.processResetPassword(
                "A1", "weak", redirectAttributes
        );

        assertEquals("redirect:/auth/reset-password?token=A1", view);
        verify(redirectAttributes).addFlashAttribute("errorMessage", "Password debole");
    }
}
