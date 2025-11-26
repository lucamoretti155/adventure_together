package com.lucamoretti.adventure_together.service.validation;

import com.lucamoretti.adventure_together.dto.user.TravelerDTO;
import com.lucamoretti.adventure_together.util.exception.DataIntegrityException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(MockitoExtension.class)
class DataValidationServiceTest {

    private DataValidationService service;

    @BeforeEach
    void setup() {
        service = new DataValidationService();
    }

    // ------------------------------------------------------
    // validateTraveler
    // ------------------------------------------------------

    @Test
    void validateTraveler_success() {
        TravelerDTO dto = new TravelerDTO();
        dto.setDateOfBirth(LocalDate.now().minusYears(20));

        assertDoesNotThrow(() -> service.validateTraveler(dto, "Password1!"));
    }

    @Test
    void validateTraveler_underage_throws() {
        TravelerDTO dto = new TravelerDTO();
        dto.setDateOfBirth(LocalDate.now().minusYears(16));

        assertThrows(DataIntegrityException.class,
                () -> service.validateTraveler(dto, "Password1!"));
    }

    @Test
    void validateTraveler_weakPassword_throws() {
        TravelerDTO dto = new TravelerDTO();
        dto.setDateOfBirth(LocalDate.now().minusYears(20));

        assertThrows(DataIntegrityException.class,
                () -> service.validateTraveler(dto, "weak"));
    }

    // ------------------------------------------------------
    // validateAdultAge
    // ------------------------------------------------------

    @Test
    void validateAdultAge_success() {
        assertDoesNotThrow(() ->
                service.validateAdultAge(LocalDate.now().minusYears(30)));
    }

    @Test
    void validateAdultAge_under18_throws() {
        assertThrows(DataIntegrityException.class,
                () -> service.validateAdultAge(LocalDate.now().minusYears(10)));
    }

    // ------------------------------------------------------
    // validatePassword
    // ------------------------------------------------------

    @Test
    void validatePassword_success() {
        assertDoesNotThrow(() -> service.validatePassword("TestPass1!"));
    }

    @Test
    void validatePassword_invalid_throws() {
        assertThrows(DataIntegrityException.class,
                () -> service.validatePassword("abc"));
    }

    // ------------------------------------------------------
    // validateTripDates
    // ------------------------------------------------------

    @Test
    void validateTripDates_success() {
        assertDoesNotThrow(() ->
                service.validateTripDates(LocalDate.of(2025,1,1),
                        LocalDate.of(2025,1,10)));
    }

    @Test
    void validateTripDates_endBeforeStart_throws() {
        assertThrows(DataIntegrityException.class, () ->
                service.validateTripDates(LocalDate.of(2025,1,10),
                        LocalDate.of(2025,1,1)));
    }

    // ------------------------------------------------------
    // validateTripDatesWithItineraryDuration
    // ------------------------------------------------------

    @Test
    void validateTripDatesWithItineraryDuration_success() {
        LocalDate start = LocalDate.of(2025,1,1);
        LocalDate end = LocalDate.of(2025,1,3); // 3 giorni

        assertDoesNotThrow(() ->
                service.validateTripDatesWithItineraryDuration(start, end, 3));
    }

    @Test
    void validateTripDatesWithItineraryDuration_invalid_throws() {
        LocalDate start = LocalDate.of(2025,1,1);
        LocalDate end = LocalDate.of(2025,1,5); // 5 giorni

        assertThrows(DataIntegrityException.class,
                () -> service.validateTripDatesWithItineraryDuration(start, end, 3));
    }

    // ------------------------------------------------------
    // validateParticipants
    // ------------------------------------------------------

    @Test
    void validateParticipants_success() {
        assertDoesNotThrow(() -> service.validateParticipants(1, 5));
    }

    @Test
    void validateParticipants_invalid_throws() {
        assertThrows(DataIntegrityException.class,
                () -> service.validateParticipants(5, 1));
        assertThrows(DataIntegrityException.class,
                () -> service.validateParticipants(0, 3));
        assertThrows(DataIntegrityException.class,
                () -> service.validateParticipants(1, 0));
    }
}
