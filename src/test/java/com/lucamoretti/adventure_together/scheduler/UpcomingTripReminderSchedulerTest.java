package com.lucamoretti.adventure_together.scheduler;

import com.lucamoretti.adventure_together.model.booking.Booking;
import com.lucamoretti.adventure_together.model.trip.Trip;
import com.lucamoretti.adventure_together.model.user.Traveler;
import com.lucamoretti.adventure_together.repository.trip.TripRepository;
import com.lucamoretti.adventure_together.service.mail.EmailService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UpcomingTripReminderSchedulerTest {

    @Mock
    private TripRepository tripRepository;

    @Mock
    private EmailService emailService;

    @InjectMocks
    private UpcomingTripReminderScheduler scheduler;

    private Trip tripWithOneBooking;

    @BeforeEach
    void initData() {
        Traveler traveler = new Traveler();
        traveler.setEmail("test@travel.com");

        Booking booking = new Booking();
        booking.setTraveler(traveler);

        tripWithOneBooking = new Trip();
        tripWithOneBooking.setBookings(Set.of(booking));
    }

    @Test
    void sendUpcomingTripReminders_tripsFound_sendsEmails() {
        when(tripRepository.findByDateDepartureEquals(any(LocalDate.class)))
                .thenReturn(List.of(tripWithOneBooking));

        assertDoesNotThrow(() -> scheduler.sendUpcomingTripReminders());

        verify(emailService).sendHtmlMessage(
                eq("test@travel.com"),
                eq("Il tuo viaggio è vicino!"),
                eq("mail/upcoming-trip-reminder"),
                anyMap()
        );
    }

    @Test
    void sendUpcomingTripReminders_noTrips_noEmailsSent() {
        when(tripRepository.findByDateDepartureEquals(any(LocalDate.class)))
                .thenReturn(List.of());

        scheduler.sendUpcomingTripReminders();

        verifyNoInteractions(emailService);
    }

    @Test
    void sendUpcomingTripReminders_emailError_isCaughtAndDoesNotPropagate() {
        when(tripRepository.findByDateDepartureEquals(any(LocalDate.class)))
                .thenReturn(List.of(tripWithOneBooking));

        doThrow(new RuntimeException("mail error"))
                .when(emailService)
                .sendHtmlMessage(anyString(), anyString(), anyString(), any(Map.class));

        assertDoesNotThrow(() -> scheduler.sendUpcomingTripReminders());

        verify(emailService).sendHtmlMessage(
                anyString(),
                anyString(),
                anyString(),
                anyMap()
        );
    }
}
