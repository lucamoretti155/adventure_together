package com.lucamoretti.adventure_together.service.booking.impl;

import com.lucamoretti.adventure_together.dto.booking.BookingDTO;
import com.lucamoretti.adventure_together.dto.participant.ParticipantDTO;
import com.lucamoretti.adventure_together.dto.payment.PaymentIntentDTO;
import com.lucamoretti.adventure_together.model.details.DepartureAirport;
import com.lucamoretti.adventure_together.model.participant.TemporaryParticipantList;
import com.lucamoretti.adventure_together.model.trip.Trip;
import com.lucamoretti.adventure_together.model.trip.state.TripState;
import com.lucamoretti.adventure_together.model.user.Traveler;
import com.lucamoretti.adventure_together.repository.details.DepartureAirportRepository;
import com.lucamoretti.adventure_together.repository.participant.TemporaryParticipantListRepository;
import com.lucamoretti.adventure_together.repository.participant.TemporaryParticipantRepository;
import com.lucamoretti.adventure_together.repository.trip.TripRepository;
import com.lucamoretti.adventure_together.repository.user.TravelerRepository;
import com.lucamoretti.adventure_together.service.booking.BookingSerializerService;
import com.lucamoretti.adventure_together.service.payment.StripeClient;
import com.lucamoretti.adventure_together.util.exception.ResourceNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import java.time.LocalDate;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class BookingPreparationServiceImplTest {

    @Mock private TripRepository tripRepository;
    @Mock private TravelerRepository travelerRepository;
    @Mock private DepartureAirportRepository departureAirportRepository;
    @Mock private StripeClient stripeClient;
    @Mock private BookingSerializerService bookingSerializerService;
    @Mock private TemporaryParticipantListRepository temporaryParticipantListRepository;
    @Mock private TemporaryParticipantRepository temporaryParticipantRepository;

    @InjectMocks
    private BookingPreparationServiceImpl service;

    private BookingDTO req;
    private Trip trip;
    private Traveler traveler;
    private DepartureAirport airport;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);

        // ----- Request DTO -----
        req = new BookingDTO();
        req.setTripId(1L);
        req.setTravelerId(2L);
        req.setDepartureAirportId(3L);
        req.setInsuranceType("basic");

        ParticipantDTO p1 = ParticipantDTO.builder()
                .firstName("Mario")
                .lastName("Rossi")
                .dateOfBirth(LocalDate.of(1990, 1, 1))
                .build();
        ParticipantDTO p2 = ParticipantDTO.builder()
                .firstName("Luigi")
                .lastName("Verdi")
                .dateOfBirth(LocalDate.of(1985, 5, 15))
                .build();
        req.setParticipants(List.of(p1, p2));

        // ----- Trip -----
        trip = mock(Trip.class);
        TripState state = mock(TripState.class);

        when(trip.getState()).thenReturn(state);
        when(state.canAcceptBooking()).thenReturn(true);
        when(trip.hasAvailableSpots(2)).thenReturn(true);

        // ----- Traveler -----
        traveler = new Traveler();
        traveler.setId(2L);

        // ----- Airport -----
        airport = new DepartureAirport();
        airport.setId(3L);
    }


    // ------------------------------------------------------
    // SUCCESS FLOW
    // ------------------------------------------------------

    @Test
    void startBookingAndPayment_success() {

        // repository returns
        when(tripRepository.findById(1L)).thenReturn(Optional.of(trip));
        when(travelerRepository.findById(2L)).thenReturn(Optional.of(traveler));
        when(departureAirportRepository.findById(3L)).thenReturn(Optional.of(airport));

        // fake temp list
        TemporaryParticipantList list = new TemporaryParticipantList();
        list.setId(99L);
        when(temporaryParticipantListRepository.save(any())).thenReturn(list);

        // serializer
        when(bookingSerializerService.serializeBooking(any()))
                .thenReturn("{json}");

        // stripe
        PaymentIntentDTO intent = new PaymentIntentDTO();
        intent.setClientSecret("secret_123");
        when(stripeClient.createPaymentIntent(anyDouble(), eq("eur"), eq("{json}")))
                .thenReturn(intent);

        PaymentIntentDTO result = service.startBookingAndPayment(req);

        assertNotNull(result);
        assertEquals("secret_123", result.getClientSecret());

        verify(tripRepository).findById(1L);
        verify(travelerRepository).findById(2L);
        verify(departureAirportRepository).findById(3L);
        verify(bookingSerializerService).serializeBooking(any());
        verify(stripeClient).createPaymentIntent(anyDouble(), eq("eur"), eq("{json}"));
        verify(temporaryParticipantListRepository).save(any());
        verify(temporaryParticipantRepository, times(2)).save(any());
    }


    // ------------------------------------------------------
    // ERROR CASES
    // ------------------------------------------------------

    @Test
    void startBookingAndPayment_tripNotFound() {
        when(tripRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> service.startBookingAndPayment(req));
    }

    @Test
    void startBookingAndPayment_travelerNotFound() {
        when(tripRepository.findById(1L)).thenReturn(Optional.of(trip));
        when(travelerRepository.findById(2L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> service.startBookingAndPayment(req));
    }

    @Test
    void startBookingAndPayment_airportNotFound() {
        when(tripRepository.findById(1L)).thenReturn(Optional.of(trip));
        when(travelerRepository.findById(2L)).thenReturn(Optional.of(traveler));
        when(departureAirportRepository.findById(3L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> service.startBookingAndPayment(req));
    }

    @Test
    void startBookingAndPayment_tripCannotAcceptBooking() {
        when(tripRepository.findById(1L)).thenReturn(Optional.of(trip));
        when(travelerRepository.findById(2L)).thenReturn(Optional.of(traveler));
        when(departureAirportRepository.findById(3L)).thenReturn(Optional.of(airport));

        TripState state = mock(TripState.class);
        when(trip.getState()).thenReturn(state);
        when(state.canAcceptBooking()).thenReturn(false);

        assertThrows(IllegalStateException.class,
                () -> service.startBookingAndPayment(req));
    }

    @Test
    void startBookingAndPayment_notEnoughSpots() {
        when(tripRepository.findById(1L)).thenReturn(Optional.of(trip));
        when(travelerRepository.findById(2L)).thenReturn(Optional.of(traveler));
        when(departureAirportRepository.findById(3L)).thenReturn(Optional.of(airport));

        when(trip.hasAvailableSpots(2)).thenReturn(false);

        assertThrows(IllegalArgumentException.class,
                () -> service.startBookingAndPayment(req));
    }
}

