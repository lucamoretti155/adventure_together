package com.lucamoretti.adventure_together.service.trip.impl;

import com.lucamoretti.adventure_together.dto.trip.TripItineraryDTO;
import com.lucamoretti.adventure_together.dto.trip.TripItineraryDayDTO;
import com.lucamoretti.adventure_together.model.details.Category;
import com.lucamoretti.adventure_together.model.details.Country;
import com.lucamoretti.adventure_together.model.details.DepartureAirport;
import com.lucamoretti.adventure_together.model.trip.TripItinerary;
import com.lucamoretti.adventure_together.model.user.Planner;
import com.lucamoretti.adventure_together.repository.details.CategoryRepository;
import com.lucamoretti.adventure_together.repository.details.CountryRepository;
import com.lucamoretti.adventure_together.repository.details.DepartureAirportRepository;
import com.lucamoretti.adventure_together.repository.trip.TripItineraryRepository;
import com.lucamoretti.adventure_together.repository.user.PlannerRepository;
import com.lucamoretti.adventure_together.service.user.UserService;
import com.lucamoretti.adventure_together.util.exception.DuplicateResourceException;
import com.lucamoretti.adventure_together.util.exception.DataIntegrityException;
import com.lucamoretti.adventure_together.util.exception.ResourceNotFoundException;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.multipart.MultipartFile;

import java.nio.charset.StandardCharsets;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TripItineraryServiceImplTest {

    @Mock
    private TripItineraryRepository itineraryRepository;
    @Mock private PlannerRepository plannerRepository;
    @Mock private CountryRepository countryRepository;
    @Mock private CategoryRepository categoryRepository;
    @Mock private DepartureAirportRepository airportRepository;
    @Mock private UserService userService;
    @Mock private EntityManager entityManager;

    @InjectMocks
    private TripItineraryServiceImpl service;

    @BeforeEach
    void setup() {
        ReflectionTestUtils.setField(
                service,
                "uploadDir", "/tmp/uploads/"
        );
        ReflectionTestUtils.setField(
                service,
                "publicUrl", "/public/"
        );
        ReflectionTestUtils.setField(
                service,
                "defaultImagePath", "/images/default.jpg"
        );
    }


    // ------------------------------------------------------
    //                 CREATE ITINERARY
    // ------------------------------------------------------

    @Test
    void createItinerary_success() {
        TripItineraryDTO dto = new TripItineraryDTO();
        dto.setTitle("Iceland Adventure");
        dto.setDurationInDays(2);
        dto.setMinParticipants(1);
        dto.setMaxParticipants(10);
        dto.setPlannerId(1L);
        dto.setCountryIds(Set.of(10L));
        dto.setCategoryIds(Set.of(20L));
        dto.setDepartureAirportIds(Set.of(30L));

        dto.setDays(List.of(
                TripItineraryDayDTO.builder().dayNumber(1).title("Giorno 1").description("Descrizione 1").build(),
                TripItineraryDayDTO.builder().dayNumber(2).title("Giorno 2").description("Descrizione 2").build()
        ));

        Planner planner = new Planner();
        planner.setId(1L);

        Country c = new Country();
        c.setId(10L);

        Category cat = new Category();
        cat.setId(20L);

        DepartureAirport ap = new DepartureAirport();
        ap.setId(30L);

        when(itineraryRepository.existsByTitle("Iceland Adventure")).thenReturn(false);
        when(plannerRepository.findById(1L)).thenReturn(Optional.of(planner));
        when(countryRepository.findById(10L)).thenReturn(Optional.of(c));
        when(categoryRepository.findById(20L)).thenReturn(Optional.of(cat));
        when(airportRepository.findById(30L)).thenReturn(Optional.of(ap));

        TripItinerary saved = new TripItinerary();
        saved.setId(99L);
        saved.setTitle("Iceland Adventure");

        when(itineraryRepository.save(any())).thenReturn(saved);

        TripItineraryDTO result = service.createItinerary(dto);

        assertNotNull(result);
        assertEquals(99L, result.getId());
        verify(itineraryRepository).save(any());
    }

    @Test
    void createItinerary_duplicateTitle_throwsException() {
        TripItineraryDTO dto = new TripItineraryDTO();
        dto.setTitle("Duplicate Trip");
        dto.setDurationInDays(1);
        dto.setMinParticipants(1);
        dto.setMaxParticipants(2);
        dto.setPlannerId(1L);

        when(itineraryRepository.existsByTitle("Duplicate Trip")).thenReturn(true);

        assertThrows(DuplicateResourceException.class,
                () -> service.createItinerary(dto));
    }

    @Test
    void createItinerary_invalidDuration_throwsException() {
        TripItineraryDTO dto = new TripItineraryDTO();
        dto.setTitle("Test");
        dto.setDurationInDays(0);
        dto.setMinParticipants(1);
        dto.setMaxParticipants(2);
        dto.setPlannerId(1L);

        assertThrows(DataIntegrityException.class,
                () -> service.createItinerary(dto));
    }

    @Test
    void createItinerary_daysMismatch_throwsException() {
        TripItineraryDTO dto = new TripItineraryDTO();
        dto.setTitle("Test");
        dto.setDurationInDays(2);
        dto.setMinParticipants(1);
        dto.setMaxParticipants(2);
        dto.setPlannerId(1L);

        dto.setDays(List.of(TripItineraryDayDTO.builder().dayNumber(1).title("A").description("B").build()));

        Planner p = new Planner();
        p.setId(1L);

        when(itineraryRepository.existsByTitle("Test")).thenReturn(false);
        when(plannerRepository.findById(1L)).thenReturn(Optional.of(p));

        assertThrows(DataIntegrityException.class,
                () -> service.createItinerary(dto));
    }

    @Test
    void createItinerary_saveImage_success() throws Exception {
        TripItineraryDTO dto = new TripItineraryDTO();
        dto.setTitle("Iceland");
        dto.setDurationInDays(1);
        dto.setMinParticipants(1);
        dto.setMaxParticipants(2);
        dto.setPlannerId(1L);

        dto.setDays(List.of(TripItineraryDayDTO.builder().dayNumber(1).title("A").description("B").build()));

        MultipartFile image = new MockMultipartFile(
                "file",
                "photo.jpg",
                "image/jpeg",
                "test data".getBytes(StandardCharsets.UTF_8));

        dto.setPictureFile(image);

        Planner p = new Planner();
        p.setId(1L);

        when(itineraryRepository.existsByTitle("Iceland")).thenReturn(false);
        when(plannerRepository.findById(1L)).thenReturn(Optional.of(p));

        when(itineraryRepository.save(any())).thenAnswer(i -> i.getArgument(0));

        TripItineraryDTO result = service.createItinerary(dto);

        assertNotNull(result);
        verify(itineraryRepository).save(any());
    }

    // ------------------------------------------------------
    //                        UPDATE
    // ------------------------------------------------------

    @Test
    void updateItinerary_titleAlreadyExists_throwsException() {
        TripItinerary existing = new TripItinerary();
        existing.setId(10L);
        existing.setTitle("Old Title");

        TripItineraryDTO dto = new TripItineraryDTO();
        dto.setTitle("New Title");
        dto.setMinParticipants(1);
        dto.setMaxParticipants(2);
        dto.setDurationInDays(1);
        dto.setDays(List.of(TripItineraryDayDTO.builder().dayNumber(1).title("A").description("B").build()));

        when(itineraryRepository.findById(10L)).thenReturn(Optional.of(existing));
        when(itineraryRepository.existsByTitle("New Title")).thenReturn(true);

        assertThrows(DuplicateResourceException.class,
                () -> service.updateItinerary(10L, dto));
    }

    @Test
    void updateItinerary_invalidMinMax_throwsException() {
        TripItinerary existing = new TripItinerary();
        existing.setId(10L);
        existing.setTitle("Trip");

        TripItineraryDTO dto = new TripItineraryDTO();
        dto.setTitle("Trip");
        dto.setMinParticipants(10);
        dto.setMaxParticipants(5);
        dto.setDurationInDays(1);
        dto.setDays(List.of(TripItineraryDayDTO.builder().dayNumber(1).title("A").description("B").build()));

        when(itineraryRepository.findById(10L)).thenReturn(Optional.of(existing));

        assertThrows(DataIntegrityException.class,
                () -> service.updateItinerary(10L, dto));
    }

    // ------------------------------------------------------
    //                 GET BY ID / TITLE
    // ------------------------------------------------------

    @Test
    void getById_notFound() {
        when(itineraryRepository.findById(1L)).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class,
                () -> service.getById(1L));
    }

    @Test
    void getByTitle_notFound() {
        when(itineraryRepository.findByTitle("x")).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class,
                () -> service.getByTitle("x"));
    }
}
