package com.lucamoretti.adventure_together.service.booking.impl;

import com.lucamoretti.adventure_together.dto.booking.BookingDTO;
import com.lucamoretti.adventure_together.model.booking.Booking;
import com.lucamoretti.adventure_together.model.details.DepartureAirport;
import com.lucamoretti.adventure_together.model.payment.Payment;
import com.lucamoretti.adventure_together.model.trip.Trip;
import com.lucamoretti.adventure_together.model.trip.TripItinerary;
import com.lucamoretti.adventure_together.model.user.Traveler;
import com.lucamoretti.adventure_together.repository.booking.BookingRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class BookingServiceImplTest {

    // metodo helper per creare Booking di test
    private Booking buildBooking(
            Long bookingId,
            Long tripId,
            Long travelerId,
            Long airportId,
            Long paymentId,
            double amount
    ) {
        Booking booking = new Booking();
        booking.setId(bookingId);

        // Traveler
        Traveler traveler = new Traveler();
        traveler.setId(travelerId);
        booking.setTraveler(traveler);

        // Trip
        Trip trip = new Trip();
        trip.setId(tripId);

        // TripItinerary
        TripItinerary itin = new TripItinerary();
        itin.setId(999L);
        itin.setTitle("Titolo di test");
        trip.setTripItinerary(itin);

        booking.setTrip(trip);

        // Departure Airport
        DepartureAirport airport = new DepartureAirport();
        airport.setId(airportId);
        airport.setCode("MIL");
        airport.setName("Milano Test Airport");
        booking.setDepartureAirport(airport);

        // Payment
        Payment payment = new Payment();
        payment.setId(paymentId);
        payment.setAmountPaid(amount);
        payment.setStatus("SUCCEEDED");
        booking.setPayment(payment);

        return booking;
    }




    @Mock
    private BookingRepository bookingRepository;

    @InjectMocks
    private BookingServiceImpl bookingService;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
    }

    // ---------- TEST getBookingById ----------

    @Test
    void getBookingById_returnsBookingDTO_whenExists() {
        Booking booking = buildBooking(1L, 100L, 200L, 300L, 400L, 150.0);

        when(bookingRepository.findById(1L))
                .thenReturn(Optional.of(booking));

        BookingDTO result = bookingService.getBookingById(1L);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals(100L, result.getTripId());
        assertEquals(200L, result.getTravelerId());

        verify(bookingRepository).findById(1L);
    }

    @Test
    void getBookingById_throwsException_whenNotFound() {
        when(bookingRepository.findById(1L)).thenReturn(Optional.empty());

        Exception ex = assertThrows(IllegalArgumentException.class,
                () -> bookingService.getBookingById(1L));

        assertEquals("Booking non trovato", ex.getMessage());
        verify(bookingRepository).findById(1L);
    }

    // ---------- TEST getBookingsByTravelerId ----------

    @Test
    void getBookingsByTravelerId_returnsList() {
        Booking b1 = buildBooking(10L, 100L, 5L, 50L, 400L, 120.0);
        Booking b2 = buildBooking(20L, 200L, 5L, 60L, 500L, 220.0);

        when(bookingRepository.findByTraveler_Id(5L))
                .thenReturn(List.of(b1, b2));

        List<BookingDTO> result = bookingService.getBookingsByTravelerId(5L);

        assertEquals(2, result.size());
        assertEquals(10L, result.get(0).getId());
        assertEquals(20L, result.get(1).getId());

        verify(bookingRepository).findByTraveler_Id(5L);
    }

    @Test
    void getBookingsByTravelerId_returnsEmptyList_whenNoBookings() {
        when(bookingRepository.findByTraveler_Id(5L))
                .thenReturn(List.of());

        List<BookingDTO> result = bookingService.getBookingsByTravelerId(5L);

        assertTrue(result.isEmpty());
        verify(bookingRepository).findByTraveler_Id(5L);
    }
}
