package com.lucamoretti.adventure_together.service.trip.impl;

import com.lucamoretti.adventure_together.dto.trip.TripItineraryDTO;
import com.lucamoretti.adventure_together.dto.trip.TripItineraryDayDTO;
import com.lucamoretti.adventure_together.model.details.Category;
import com.lucamoretti.adventure_together.model.details.Country;
import com.lucamoretti.adventure_together.model.details.DepartureAirport;
import com.lucamoretti.adventure_together.model.trip.TripItinerary;
import com.lucamoretti.adventure_together.model.trip.TripItineraryDay;
import com.lucamoretti.adventure_together.model.user.Planner;
import com.lucamoretti.adventure_together.repository.details.CategoryRepository;
import com.lucamoretti.adventure_together.repository.details.CountryRepository;
import com.lucamoretti.adventure_together.repository.details.DepartureAirportRepository;
import com.lucamoretti.adventure_together.repository.trip.TripItineraryRepository;
import com.lucamoretti.adventure_together.repository.user.PlannerRepository;
import com.lucamoretti.adventure_together.service.user.UserService;
import com.lucamoretti.adventure_together.util.exception.DuplicateResourceException;
import com.lucamoretti.adventure_together.util.exception.DataIntegrityException;
import com.lucamoretti.adventure_together.util.exception.FileStorageException;
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

import java.io.IOException;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TripItineraryServiceImplTest {

    @Mock private TripItineraryRepository itineraryRepository;
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
        ReflectionTestUtils.setField(service, "uploadDir", "/tmp/uploads/");
        ReflectionTestUtils.setField(service, "publicUrl", "/public/");
        ReflectionTestUtils.setField(service, "defaultImagePath", "/images/default.jpg");
        ReflectionTestUtils.setField(service, "entityManager", entityManager);
    }

    // ---------------------------------------------------------------------
    //  UTILITY
    // ---------------------------------------------------------------------

    /** Crea un DTO valido di base */
    private TripItineraryDTO baseValidDto() {
        TripItineraryDTO dto = new TripItineraryDTO();
        dto.setTitle("Test Trip");
        dto.setDurationInDays(2);
        dto.setMinParticipants(1);
        dto.setMaxParticipants(5);
        dto.setPlannerId(1L);
        dto.setCountryIds(Set.of(10L));
        dto.setCategoryIds(Set.of(20L));
        dto.setDepartureAirportIds(Set.of(30L));
        dto.setDays(List.of(
                TripItineraryDayDTO.builder().dayNumber(1).title("A").description("B").build(),
                TripItineraryDayDTO.builder().dayNumber(2).title("C").description("D").build()
        ));
        return dto;
    }

    private void mockValidRelations() {
        when(plannerRepository.findById(1L)).thenReturn(Optional.of(new Planner()));
        Country c = new Country(); c.setId(10L);
        when(countryRepository.findById(10L)).thenReturn(Optional.of(c));
        Category cat = new Category(); cat.setId(20L);
        when(categoryRepository.findById(20L)).thenReturn(Optional.of(cat));
        DepartureAirport ap = new DepartureAirport(); ap.setId(30L);
        when(airportRepository.findById(30L)).thenReturn(Optional.of(ap));
    }

    // ---------------------------------------------------------------------
    //  CREATE
    // ---------------------------------------------------------------------

    @Test
    void createItinerary_success() {
        TripItineraryDTO dto = baseValidDto();
        mockValidRelations();

        when(itineraryRepository.existsByTitle("Test Trip")).thenReturn(false);
        when(itineraryRepository.save(any())).thenAnswer(i -> {
            TripItinerary t = i.getArgument(0);
            t.setId(99L);
            return t;
        });

        TripItineraryDTO result = service.createItinerary(dto);

        assertNotNull(result);
        assertEquals(99L, result.getId());
        verify(itineraryRepository).save(any());
    }

    @Test
    void createItinerary_duplicateTitle_throws() {
        TripItineraryDTO dto = baseValidDto();
        when(itineraryRepository.existsByTitle(dto.getTitle())).thenReturn(true);

        assertThrows(DuplicateResourceException.class,
                () -> service.createItinerary(dto));
    }

    @Test
    void createItinerary_invalidMinMax_throws() {
        TripItineraryDTO dto = baseValidDto();
        dto.setMinParticipants(10);
        dto.setMaxParticipants(5);

        assertThrows(DataIntegrityException.class,
                () -> service.createItinerary(dto));
    }

    @Test
    void createItinerary_invalidDuration_throws() {
        TripItineraryDTO dto = baseValidDto();
        dto.setDurationInDays(0);

        assertThrows(DataIntegrityException.class,
                () -> service.createItinerary(dto));
    }

    @Test
    void createItinerary_daysMismatch_throws() {
        TripItineraryDTO dto = baseValidDto();
        dto.setDurationInDays(3); // mismatch

        mockValidRelations();
        when(itineraryRepository.existsByTitle(dto.getTitle())).thenReturn(false);

        assertThrows(DataIntegrityException.class,
                () -> service.createItinerary(dto));
    }

    @Test
    void createItinerary_missingCountry_throws() {
        TripItineraryDTO dto = baseValidDto();
        when(plannerRepository.findById(1L)).thenReturn(Optional.of(new Planner()));
        when(countryRepository.findById(10L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> service.createItinerary(dto));
    }

    @Test
    void createItinerary_invalidImageFormat_throws() {
        TripItineraryDTO dto = baseValidDto();
        mockValidRelations();

        MultipartFile gif = new MockMultipartFile("f", "a.gif", "image/gif", "xxx".getBytes());
        dto.setPictureFile(gif);

        when(itineraryRepository.existsByTitle("Test Trip")).thenReturn(false);

        assertThrows(DataIntegrityException.class,
                () -> service.createItinerary(dto));
    }

    @Test
    void createItinerary_imageTooLarge_throws() {
        TripItineraryDTO dto = baseValidDto();
        mockValidRelations();

        byte[] big = new byte[11 * 1024 * 1024];
        dto.setPictureFile(new MockMultipartFile("f","x.jpg","image/jpeg",big));

        when(itineraryRepository.existsByTitle("Test Trip")).thenReturn(false);

        assertThrows(DataIntegrityException.class,
                () -> service.createItinerary(dto));
    }

    @Test
    void createItinerary_imageIOException_throws() throws Exception {
        TripItineraryDTO dto = baseValidDto();
        mockValidRelations();

        MultipartFile image = mock(MultipartFile.class);
        when(image.isEmpty()).thenReturn(false);
        when(image.getContentType()).thenReturn("image/jpeg");
        when(image.getSize()).thenReturn(1000L);
        when(image.getInputStream()).thenThrow(new IOException());

        dto.setPictureFile(image);

        when(itineraryRepository.existsByTitle("Test Trip")).thenReturn(false);

        assertThrows(FileStorageException.class,
                () -> service.createItinerary(dto));
    }

    // ---------------------------------------------------------------------
    //  UPDATE
    // ---------------------------------------------------------------------

    @Test
    void updateItinerary_titleAlreadyExists_throws() {
        TripItinerary existing = new TripItinerary();
        existing.setId(10L);
        existing.setTitle("Old");

        TripItineraryDTO dto = baseValidDto();
        dto.setTitle("New Title");

        when(itineraryRepository.findById(10L)).thenReturn(Optional.of(existing));
        when(itineraryRepository.existsByTitle("New Title")).thenReturn(true);

        assertThrows(DuplicateResourceException.class,
                () -> service.updateItinerary(10L, dto));
    }

    @Test
    void updateItinerary_success_rebuildsDays() {
        TripItinerary existing = new TripItinerary();
        existing.setId(10L);
        existing.setTitle("Old");
        existing.setDays(new ArrayList<>(List.of(new TripItineraryDay())));

        TripItineraryDTO dto = baseValidDto();
        dto.setTitle("Old");

        mockValidRelations();

        when(itineraryRepository.findById(10L)).thenReturn(Optional.of(existing));
        when(userService.getCurrentUserId()).thenReturn(1L);
        when(plannerRepository.findById(1L)).thenReturn(Optional.of(new Planner()));
        when(itineraryRepository.save(any())).thenAnswer(i -> i.getArgument(0));

        TripItineraryDTO result = service.updateItinerary(10L, dto);

        assertNotNull(result);
        verify(entityManager).flush();
        assertEquals(2, existing.getDays().size());
    }


    // ---------------------------------------------------------------------
    //  GET METHODS
    // ---------------------------------------------------------------------

    @Test
    void getById_notFound_throws() {
        when(itineraryRepository.findById(1L)).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class,
                () -> service.getById(1L));
    }

    @Test
    void getByTitle_notFound_throws() {
        when(itineraryRepository.findByTitle("X")).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class,
                () -> service.getByTitle("X"));
    }

    @Test
    void getAll_ok() {
        when(itineraryRepository.findAll()).thenReturn(List.of(new TripItinerary()));
        assertEquals(1, service.getAll().size());
    }

    @Test
    void getByPlannerId_ok() {
        when(itineraryRepository.findByPlannerId(1L)).thenReturn(List.of(new TripItinerary()));
        assertEquals(1, service.getByPlannerId(1L).size());
    }

    @Test
    void getAllByCountry_ok() {
        when(itineraryRepository.findByCountry(10L)).thenReturn(List.of(new TripItinerary()));
        assertEquals(1, service.getAllByCountryId(10L).size());
    }

    @Test
    void getAllByGeoArea_ok() {
        when(itineraryRepository.findByGeoArea(5L)).thenReturn(List.of(new TripItinerary()));
        assertEquals(1, service.getAllByGeoAreaId(5L).size());
    }

    @Test
    void getAllByCategory_ok() {
        when(itineraryRepository.findByCategory(20L)).thenReturn(List.of(new TripItinerary()));
        assertEquals(1, service.getAllByCategoryId(20L).size());
    }

    @Test
    void getAllByCategoryIds_ok() {
        when(itineraryRepository.findByCategories(List.of(1L,2L)))
                .thenReturn(List.of(new TripItinerary()));
        assertEquals(1, service.getAllByCategoryIds(List.of(1L,2L)).size());
    }
}
