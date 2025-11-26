package com.lucamoretti.adventure_together.service.booking.impl;

import com.lucamoretti.adventure_together.model.booking.Booking;
import com.lucamoretti.adventure_together.model.details.DepartureAirport;
import com.lucamoretti.adventure_together.model.participant.TemporaryParticipant;
import com.lucamoretti.adventure_together.model.participant.TemporaryParticipantList;
import com.lucamoretti.adventure_together.model.trip.Trip;

import com.lucamoretti.adventure_together.model.user.Traveler;
import com.lucamoretti.adventure_together.repository.booking.BookingRepository;
import com.lucamoretti.adventure_together.repository.details.DepartureAirportRepository;
import com.lucamoretti.adventure_together.repository.participant.TemporaryParticipantListRepository;
import com.lucamoretti.adventure_together.repository.trip.TripRepository;
import com.lucamoretti.adventure_together.repository.user.TravelerRepository;
import com.lucamoretti.adventure_together.service.booking.BookingSerializerService;
import com.lucamoretti.adventure_together.service.mail.EmailService;
import com.stripe.model.PaymentIntent;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import java.io.OutputStream;
import java.io.PrintStream;
import java.time.LocalDate;
import java.util.*;

import org.mockito.*;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.*;


import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.Mockito.*;

class BookingFinalizeServiceImplTest {

    @Mock
    private BookingRepository bookingRepository;
    @Mock
    private TravelerRepository travelerRepository;
    @Mock
    private TripRepository tripRepository;
    @Mock
    private DepartureAirportRepository departureAirportRepository;
    @Mock
    private BookingSerializerService bookingSerializerService;
    @Mock
    private EmailService emailService;
    @Mock
    private TemporaryParticipantListRepository temporaryParticipantListRepository;

    @InjectMocks
    private BookingFinalizeServiceImpl service;

    private Traveler traveler;
    private DepartureAirport airport;
    private TemporaryParticipantList tempList;
    private Trip trip;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        // baseUrl usato in baseUrl + "/home" → se null fa NPE
        ReflectionTestUtils.setField(service, "baseUrl", "http://localhost:8080");

        // Traveler
        traveler = new Traveler();
        traveler.setId(2L);
        traveler.setEmail("test@test.com");

        // Airport
        airport = new DepartureAirport();
        airport.setId(3L);

        // Trip (mock, non ci interessa lo stato qui)
        trip = mock(Trip.class);

        // Temporary participants list
        tempList = new TemporaryParticipantList();
        tempList.setId(99L);

        TemporaryParticipant tp = TemporaryParticipant.builder()
                .firstName("Luca")
                .lastName("Moretti")
                .dateOfBirth(LocalDate.of(1990, 1, 1))
                .list(tempList)
                .build();

        tempList.setParticipants(List.of(tp));
    }


    // ----------------------------------------------------------
    // SUCCESS FLOW
    // ----------------------------------------------------------

    @Test
    void finalizeBooking_success() {
        // PaymentIntent mock con metadata "booking"
        PaymentIntent intent = mock(PaymentIntent.class);
        Map<String, String> metadata = new HashMap<>();
        metadata.put("booking", "{json}");
        when(intent.getMetadata()).thenReturn(metadata);
        when(intent.getId()).thenReturn("pi_123");
        when(intent.getPaymentMethod()).thenReturn("pm_card_visa");

        // Deserializzazione metadata JSON → Map
        when(bookingSerializerService.deserializeBookingAsMap("{json}"))
                .thenReturn(Map.of(
                        "tripId", 1,
                        "travelerId", 2,
                        "departureAirportId", 3,
                        "insuranceType", "basic",
                        "numParticipants", 1,
                        "participantsTempListId", 99
                ));

        when(tripRepository.findById(1L)).thenReturn(Optional.of(trip));
        when(travelerRepository.findById(2L)).thenReturn(Optional.of(traveler));
        when(departureAirportRepository.findById(3L)).thenReturn(Optional.of(airport));
        when(temporaryParticipantListRepository.findById(99L)).thenReturn(Optional.of(tempList));

        // Esegui
        assertDoesNotThrow(() -> service.finalizeBooking(intent));

        //  booking salvato
        verify(bookingRepository).save(any(Booking.class));

        //  lista temporanea cancellata
        verify(temporaryParticipantListRepository).delete(tempList);

        // L’email non è obbligatoria per far passare il test
        verify(emailService, atMost(1)).sendHtmlMessage(
                eq("test@test.com"),
                anyString(),
                anyString(),
                anyMap()
        );
    }

    // ----------------------------------------------------------
    // METADATA MANCANTE → NON FA NULLA
    // ----------------------------------------------------------

    @Test
    void finalizeBooking_metadataMissing_noCrash() {
        PaymentIntent intent = mock(PaymentIntent.class);
        when(intent.getMetadata()).thenReturn(null);

        assertDoesNotThrow(() -> service.finalizeBooking(intent));

        verifyNoInteractions(bookingRepository);
        verifyNoInteractions(emailService);
    }

    // ----------------------------------------------------------
    // METADATA booking == null → NON FA NULLA
    // ----------------------------------------------------------

    @Test
    void finalizeBooking_metadataBookingNull_noCrash() {
        PaymentIntent intent = mock(PaymentIntent.class);
        Map<String, String> metadata = new HashMap<>();
        metadata.put("booking", null);
        when(intent.getMetadata()).thenReturn(metadata);

        assertDoesNotThrow(() -> service.finalizeBooking(intent));

        verifyNoInteractions(bookingRepository);
        verifyNoInteractions(emailService);
    }

    // ----------------------------------------------------------
    // TRIP NON TROVATO → ECCEZIONE CATTURATA, NESSUN SAVE
    // ----------------------------------------------------------

    @Test
    void finalizeBooking_tripNotFound_handled() {
        PaymentIntent intent = mock(PaymentIntent.class);
        Map<String, String> metadata = new HashMap<>();
        metadata.put("booking", "{json}");
        when(intent.getMetadata()).thenReturn(metadata);

        when(bookingSerializerService.deserializeBookingAsMap("{json}"))
                .thenReturn(Map.of(
                        "tripId", 1,
                        "travelerId", 2,
                        "departureAirportId", 3,
                        "insuranceType", "basic",
                        "numParticipants", 1,
                        "participantsTempListId", 99
                ));

        when(tripRepository.findById(1L)).thenReturn(Optional.empty());

        // Il servizio cattura l’eccezione internamente → nessun throw
        assertDoesNotThrow(() -> service.finalizeBooking(intent));

        // Non deve salvare booking
        verify(bookingRepository, never()).save(any());
        verify(emailService, never()).sendHtmlMessage(any(), any(), any(), anyMap());
    }
}
