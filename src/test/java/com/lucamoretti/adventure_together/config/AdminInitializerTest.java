package com.lucamoretti.adventure_together.config;

import com.lucamoretti.adventure_together.model.user.Admin;
import com.lucamoretti.adventure_together.model.user.Role;
import com.lucamoretti.adventure_together.repository.user.AdminRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AdminInitializerTest {

    @Mock
    private AdminRepository adminRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private AdminInitializer adminInitializer;


    // -------------------------------------------------------------------------
    // TEST 1 - CREA L'ADMIN DI DEFAULT QUANDO IL DATABASE È VUOTO
    // -------------------------------------------------------------------------
    @Test
    void run_createsDefaultAdmin_whenNoAdminsInDatabase() {
        // Arrange
        when(adminRepository.count()).thenReturn(0L);
        when(passwordEncoder.encode("admin123")).thenReturn("encodedPassword");

        // Act
        adminInitializer.run();

        // Assert
        ArgumentCaptor<Admin> captor = ArgumentCaptor.forClass(Admin.class);
        verify(adminRepository, times(1)).save(captor.capture());

        Admin savedAdmin = captor.getValue();

        assertEquals("System", savedAdmin.getFirstName());
        assertEquals("Administrator", savedAdmin.getLastName());
        assertEquals("demo.mail.app.java.project@gmail.com", savedAdmin.getEmail());
        assertEquals("encodedPassword", savedAdmin.getPassword());
        assertTrue(savedAdmin.isActive());
        assertEquals(Role.ADMIN.name(), savedAdmin.getRole());
        assertEquals("241414", savedAdmin.getEmployeeId());

        verify(passwordEncoder, times(1)).encode("admin123");
    }


    // -------------------------------------------------------------------------
    // TEST 2 - NON CREA NESSUN ADMIN SE IL DATABASE NON È VUOTO
    // -------------------------------------------------------------------------
    @Test
    void run_doesNotCreateAdmin_whenAdminsExist() {
        // Arrange
        when(adminRepository.count()).thenReturn(5L);

        // Act
        adminInitializer.run();

        // Assert
        verify(adminRepository, never()).save(any());
        verify(passwordEncoder, never()).encode(anyString());
    }
}

