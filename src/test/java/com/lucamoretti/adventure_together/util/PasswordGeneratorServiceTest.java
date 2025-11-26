package com.lucamoretti.adventure_together.util;

import com.lucamoretti.adventure_together.util.passwordGenerator.PasswordGeneratorService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(MockitoExtension.class)
class PasswordGeneratorServiceTest {

    private PasswordGeneratorService service;

    @BeforeEach
    void setup() {
        service = new PasswordGeneratorService();
    }

    @Test
    void generateSecurePassword_hasValidLength() {
        String pwd = service.generateSecurePassword();
        assertTrue(pwd.length() >= 8 && pwd.length() <= 20);
    }

    @Test
    void generateSecurePassword_containsUppercase() {
        String pwd = service.generateSecurePassword();
        assertTrue(pwd.chars().anyMatch(Character::isUpperCase));
    }

    @Test
    void generateSecurePassword_containsLowercase() {
        String pwd = service.generateSecurePassword();
        assertTrue(pwd.chars().anyMatch(Character::isLowerCase));
    }

    @Test
    void generateSecurePassword_containsDigit() {
        String pwd = service.generateSecurePassword();
        assertTrue(pwd.chars().anyMatch(Character::isDigit));
    }

    @Test
    void generateSecurePassword_containsSpecialCharacter() {
        String pwd = service.generateSecurePassword();
        assertTrue(pwd.chars().anyMatch(c -> "@$!%*?&".indexOf(c) >= 0));
    }

    @Test
    void generateSecurePassword_generatesDifferentValuesMostOfTheTime() {
        String pwd1 = service.generateSecurePassword();
        String pwd2 = service.generateSecurePassword();
        assertNotEquals(pwd1, pwd2); // altamente probabile
    }
}
