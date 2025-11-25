package com.lucamoretti.adventure_together.service.trip.impl;

import com.lucamoretti.adventure_together.dto.participant.ParticipantDTO;
import com.lucamoretti.adventure_together.dto.trip.TripDTO;
import com.lucamoretti.adventure_together.dto.trip.TripItineraryDTO;
import com.lucamoretti.adventure_together.model.participant.Participant;
import com.lucamoretti.adventure_together.model.trip.Trip;
import com.lucamoretti.adventure_together.model.trip.TripItinerary;
import com.lucamoretti.adventure_together.model.trip.state.ToBeConfirmed;
import com.lucamoretti.adventure_together.model.user.Planner;
import com.lucamoretti.adventure_together.repository.participant.ParticipantRepository;
import com.lucamoretti.adventure_together.repository.trip.TripItineraryRepository;
import com.lucamoretti.adventure_together.repository.trip.TripRepository;
import com.lucamoretti.adventure_together.repository.user.PlannerRepository;
import com.lucamoretti.adventure_together.service.trip.TripItineraryService;
import com.lucamoretti.adventure_together.service.validation.DataValidationService;
import com.lucamoretti.adventure_together.util.exception.DataIntegrityException;
import com.lucamoretti.adventure_together.util.exception.ResourceNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.*;

@ExtendWith(MockitoExtension.class)
class TripServiceImplTest {

    @Mock
    TripRepository tripRepository;
    @Mock
    TripItineraryRepository itineraryRepository;
    @Mock
    PlannerRepository plannerRepository;
    @Mock
    DataValidationService dataValidationService;
    @Mock
    TripItineraryService tripItineraryService;
    @Mock
    ParticipantRepository participantRepository;

    @InjectMocks
    TripServiceImpl tripService;

    private TripDTO buildValidDto() {
        TripDTO dto = new TripDTO();
        dto.setTripItineraryId(1L);
        dto.setPlannerId(2L);
        dto.setDateStartBookings(LocalDate.now().plusDays(1));
        dto.setDateEndBookings(LocalDate.now().plusDays(5));
        dto.setDateDeparture(LocalDate.now().plusDays(10));
        dto.setDateReturn(LocalDate.now().plusDays(15));
        return dto;
    }

    private TripItineraryDTO mockItineraryDTO() {
        TripItineraryDTO d = new TripItineraryDTO();
        d.setDurationInDays(5);
        d.setTitle("Test Itinerary");
        return d;
    }

    // -------------------------------------------------------
    // CREATE TRIP
    // -------------------------------------------------------

    @Test
    void createTrip_success() {

        TripDTO dto = buildValidDto();

        TripItineraryDTO itineraryDTO = mockItineraryDTO();

        Planner planner = new Planner();
        planner.setId(2L);

        TripItinerary itinerary = new TripItinerary();
        itinerary.setId(1L);

        Trip saved = new Trip();
        saved.setId(100L);

        when(tripItineraryService.getById(1L)).thenReturn(itineraryDTO);
        when(itineraryRepository.findById(1L)).thenReturn(Optional.of(itinerary));
        when(plannerRepository.findById(2L)).thenReturn(Optional.of(planner));
        when(tripRepository.save(any())).thenReturn(saved);

        TripDTO result = tripService.createTrip(dto);

        assertNotNull(result);
        assertEquals(100L, result.getId());

        verify(dataValidationService, times(2)).validateTripDates(any(), any());
        verify(dataValidationService).validateTripDatesWithItineraryDuration(
                dto.getDateDeparture(),
                dto.getDateReturn(),
                itineraryDTO.getDurationInDays()
        );
        verify(tripRepository).save(any());
    }

    @Test
    void createTrip_throwsWhenDateInPast() {
        TripDTO dto = buildValidDto();
        dto.setDateDeparture(LocalDate.now().minusDays(1));

        when(tripItineraryService.getById(1L)).thenReturn(mockItineraryDTO());

        assertThrows(DataIntegrityException.class,
                () -> tripService.createTrip(dto));
    }

    @Test
    void createTrip_itineraryNotFound() {
        TripDTO dto = buildValidDto();

        when(tripItineraryService.getById(1L)).thenReturn(mockItineraryDTO());
        when(itineraryRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> tripService.createTrip(dto));
    }

    @Test
    void createTrip_plannerNotFound() {
        TripDTO dto = buildValidDto();

        when(tripItineraryService.getById(1L)).thenReturn(mockItineraryDTO());
        when(itineraryRepository.findById(1L)).thenReturn(Optional.of(new TripItinerary()));
        when(plannerRepository.findById(2L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> tripService.createTrip(dto));
    }

    // -------------------------------------------------------
    // HANDLE TRIP
    // -------------------------------------------------------

    @Test
    void handleTrip_success() {
        Trip trip = new Trip();
        trip.setId(1L);

        when(tripRepository.findById(1L)).thenReturn(Optional.of(trip));

        TripDTO result = tripService.handleTrip(1L);
        assertNotNull(result);

        verify(tripRepository).save(trip);
    }

    @Test
    void handleTrip_notFound() {
        when(tripRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> tripService.handleTrip(1L));
    }

    // -------------------------------------------------------
    // CANCEL TRIP
    // -------------------------------------------------------

    @Test
    void cancelTrip_success() {
        Trip trip = new Trip();
        trip.setId(1L);
        trip.setState(new ToBeConfirmed());

        when(tripRepository.findById(1L)).thenReturn(Optional.of(trip));

        TripDTO result = tripService.cancelTrip(1L);

        assertNotNull(result);
        verify(tripRepository).save(trip);
    }

    @Test
    void cancelTrip_wrongState_throws() {
        Trip trip = new Trip();
        trip.setId(1L);
        trip.setState(new com.lucamoretti.adventure_together.model.trip.state.ConfirmedOpen());

        when(tripRepository.findById(1L)).thenReturn(Optional.of(trip));

        assertThrows(DataIntegrityException.class,
                () -> tripService.cancelTrip(1L));
    }

    // -------------------------------------------------------
    // GENERIC GETTERS
    // -------------------------------------------------------

    @Test
    void getAll_success() {
        Trip t = new Trip();
        t.setId(1L);

        when(tripRepository.findAll()).thenReturn(List.of(t));

        List<TripDTO> result = tripService.getAll();
        assertEquals(1, result.size());
    }

    @Test
    void getById_success() {
        Trip t = new Trip();
        t.setId(1L);

        when(tripRepository.findById(1L)).thenReturn(Optional.of(t));

        TripDTO result = tripService.getById(1L);
        assertEquals(1L, result.getId());
    }

    @Test
    void getById_notFound() {
        when(tripRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> tripService.getById(1L));
    }

    @Test
    void getTripsByPlanner_success() {
        Trip t = new Trip();
        t.setId(1L);

        when(tripRepository.findByPlanner_Id(5L)).thenReturn(List.of(t));

        List<TripDTO> result = tripService.getTripsByPlanner(5L);
        assertEquals(1, result.size());
    }

    // -------------------------------------------------------
    // BOOKING QUERIES
    // -------------------------------------------------------

    @Test
    void getBookableTrips_success() {
        Trip t = new Trip();
        t.setId(1L);
        t.setDateEndBookings(LocalDate.now().plusDays(5));

        when(tripRepository.findOpenForBooking()).thenReturn(List.of(t));

        List<TripDTO> result = tripService.getBookableTrips();
        assertEquals(1, result.size());
    }


    @Test
    void getToBeConfirmedTrips_success() {
        Trip t = new Trip();
        t.setId(1L);

        when(tripRepository.findByState(ToBeConfirmed.class)).thenReturn(List.of(t));

        List<TripDTO> result = tripService.getToBeConfirmedTrips();
        assertEquals(1, result.size());
    }

    @Test
    void getBookableTripsByItinerary_success() {
        Trip t = new Trip();
        t.setId(1L);

        when(tripRepository.findOpenForBookingByItinerary(10L)).thenReturn(List.of(t));

        List<TripDTO> result = tripService.getBookableTripsByItinerary(10L);
        assertEquals(1, result.size());
    }

    @Test
    void getUpcomingBookableTrips_success() {
        Trip t = new Trip();
        t.setId(1L);

        when(tripRepository.findUpcomingBookableTrips(any(), any())).thenReturn(List.of(t));

        List<TripDTO> result = tripService.getUpcomingBookableTrips();
        assertEquals(1, result.size());
    }

    @Test
    void getFutureTrips_success() {
        Trip t = new Trip();
        t.setId(1L);

        when(tripRepository.findFutureTrips(any())).thenReturn(List.of(t));

        List<TripDTO> result = tripService.getFutureTrips();
        assertEquals(1, result.size());
    }

    @Test
    void getTripsByState_success() {
        Trip t = new Trip();
        t.setId(1L);

        when(tripRepository.findByState(ToBeConfirmed.class)).thenReturn(List.of(t));

        List<TripDTO> result = tripService.getTripsByState(ToBeConfirmed.class);
        assertEquals(1, result.size());
    }

    // -------------------------------------------------------
    // DATE RANGE QUERIES
    // -------------------------------------------------------

    @Test
    void getTripsNotCancelledBetween_success() {
        Trip t = new Trip();
        t.setId(1L);

        when(tripRepository.findByDateDepartureBetweenNotCancelled(any(), any()))
                .thenReturn(List.of(t));

        List<TripDTO> result = tripService.getTripsNotCancelledBetween(
                LocalDate.now(), LocalDate.now().plusDays(10));

        assertEquals(1, result.size());
    }

    @Test
    void getTripsNotCancelledBetween_invalidDates() {
        assertThrows(DataIntegrityException.class,
                () -> tripService.getTripsNotCancelledBetween(
                        LocalDate.now().plusDays(5), LocalDate.now()));
    }

    @Test
    void getTripsBetweenDates_success() {
        Trip t = new Trip();
        t.setId(1L);

        when(tripRepository.findByDateDepartureBetween(any(), any()))
                .thenReturn(List.of(t));

        List<TripDTO> result = tripService.getTripsBetweenDates(
                LocalDate.now(), LocalDate.now().plusDays(3));

        assertEquals(1, result.size());
    }

    @Test
    void getTripsBetweenDates_invalidDates() {
        assertThrows(DataIntegrityException.class,
                () -> tripService.getTripsBetweenDates(
                        LocalDate.now().plusDays(5), LocalDate.now()));
    }

    // -------------------------------------------------------
    // PARTICIPANTS
    // -------------------------------------------------------

    @Test
    void getParticipantsByTripId_success() {
        Participant p = new Participant();
        p.setId(1L);

        when(participantRepository.findByBooking_Trip_Id(1L))
                .thenReturn(List.of(p));

        List<ParticipantDTO> result = tripService.getParticipantsByTripId(1L);
        assertEquals(1, result.size());
    }

    @Test
    void countParticipants_success() {
        Trip trip = mock(Trip.class);
        when(trip.getCurrentParticipantsCount()).thenReturn(3);
        when(tripRepository.findById(1L)).thenReturn(Optional.of(trip));

        int count = tripService.countParticipants(1L);
        assertEquals(3, count);
    }

    @Test
    void countParticipants_tripNotFound() {
        when(tripRepository.findById(1L)).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class,
                () -> tripService.countParticipants(1L));
    }
}

