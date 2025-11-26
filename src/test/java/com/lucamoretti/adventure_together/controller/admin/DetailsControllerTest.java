package com.lucamoretti.adventure_together.controller.admin;

import com.lucamoretti.adventure_together.dto.details.*;
import com.lucamoretti.adventure_together.service.details.*;
import com.lucamoretti.adventure_together.util.exception.DuplicateResourceException;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import org.springframework.ui.ConcurrentModel;
import org.springframework.ui.Model;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.mvc.support.RedirectAttributesModelMap;

import org.springframework.validation.BindingResult;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DetailsControllerTest {

    @Mock private GeoAreaService geoAreaService;
    @Mock private CountryService countryService;
    @Mock private CategoryService categoryService;
    @Mock private DepartureAirportService departureAirportService;

    @Mock private BindingResult bindingResult;

    @InjectMocks
    private DetailsController controller;

    /* -------------------------------------------------------
     * GEOAREA
     * ------------------------------------------------------- */

    @Test
    void showCreateGeoAreaForm_addsDto() {
        Model model = new ConcurrentModel();

        String view = controller.showCreateGeoAreaForm(model);

        assertEquals("admin/details/create-geoarea", view);
        assertTrue(model.containsAttribute("geoAreaDTO"));
    }

    @Test
    void createGeoArea_validationErrors_returnForm() {
        GeoAreaDTO dto = new GeoAreaDTO();
        Model model = new ConcurrentModel();
        RedirectAttributes attrs = new RedirectAttributesModelMap();

        when(bindingResult.hasErrors()).thenReturn(true);

        String result = controller.createGeoArea(dto, bindingResult, model, attrs);

        assertEquals("admin/details/create-geoarea", result);
    }

    @Test
    void createGeoArea_success_redirects() {
        GeoAreaDTO dto = new GeoAreaDTO();
        Model model = new ConcurrentModel();
        RedirectAttributes attrs = new RedirectAttributesModelMap();

        when(bindingResult.hasErrors()).thenReturn(false);

        String result = controller.createGeoArea(dto, bindingResult, model, attrs);

        verify(geoAreaService).createGeoArea(dto);
        assertEquals("redirect:/admin/details/list-all", result);
        assertTrue(attrs.getFlashAttributes().containsKey("successMessage"));
    }

    @Test
    void createGeoArea_duplicate_staysInForm() {
        GeoAreaDTO dto = GeoAreaDTO.builder().geoArea("Asia").build();
        Model model = new ConcurrentModel();
        RedirectAttributes attrs = new RedirectAttributesModelMap();

        when(bindingResult.hasErrors()).thenReturn(false);
        doThrow(new DuplicateResourceException("exists"))
                .when(geoAreaService).createGeoArea(dto);

        String result = controller.createGeoArea(dto, bindingResult, model, attrs);

        assertEquals("admin/details/create-geoarea", result);
        assertTrue(model.containsAttribute("errorMessage"));
    }

    /* -------------------------------------------------------
     * COUNTRY
     * ------------------------------------------------------- */

    @Test
    void showCreateCountryForm_addsDtoAndGeoAreas() {
        Model model = new ConcurrentModel();
        when(geoAreaService.getAllGeoAreas()).thenReturn(List.of());

        String view = controller.showCreateCountryForm(model);

        assertEquals("admin/details/create-country", view);
        assertTrue(model.containsAttribute("countryDTO"));
        assertTrue(model.containsAttribute("geoAreas"));
    }

    @Test
    void createCountry_validationErrors_reloadForm() {
        CountryDTO dto = new CountryDTO();
        Model model = new ConcurrentModel();
        RedirectAttributes attrs = new RedirectAttributesModelMap();

        when(bindingResult.hasErrors()).thenReturn(true);
        when(geoAreaService.getAllGeoAreas()).thenReturn(List.of());

        String result = controller.createCountry(dto, bindingResult, model, attrs);

        assertEquals("admin/details/create-country", result);
        assertTrue(model.containsAttribute("geoAreas"));
    }

    @Test
    void createCountry_success_redirects() {
        CountryDTO dto = new CountryDTO();
        Model model = new ConcurrentModel();
        RedirectAttributes attrs = new RedirectAttributesModelMap();

        when(bindingResult.hasErrors()).thenReturn(false);

        String result = controller.createCountry(dto, bindingResult, model, attrs);

        verify(countryService).createCountry(dto);
        assertEquals("redirect:/admin/details/list-all", result);
    }

    @Test
    void createCountry_duplicate_showsError() {
        CountryDTO dto = new CountryDTO();
        Model model = new ConcurrentModel();
        RedirectAttributes attrs = new RedirectAttributesModelMap();

        when(bindingResult.hasErrors()).thenReturn(false);
        when(geoAreaService.getAllGeoAreas()).thenReturn(List.of());
        doThrow(new DuplicateResourceException("exists"))
                .when(countryService).createCountry(dto);

        String result = controller.createCountry(dto, bindingResult, model, attrs);

        assertEquals("admin/details/create-country", result);
        assertTrue(model.containsAttribute("errorMessage"));
    }

    /* -------------------------------------------------------
     * CATEGORY
     * ------------------------------------------------------- */

    @Test
    void showCreateCategoryForm_addsDto() {
        Model model = new ConcurrentModel();

        String view = controller.showCreateCategoryForm(model);

        assertEquals("admin/details/create-category", view);
        assertTrue(model.containsAttribute("categoryDTO"));
    }

    @Test
    void createCategory_validationErrors_reloadForm() {
        CategoryDTO dto = new CategoryDTO();
        Model model = new ConcurrentModel();
        RedirectAttributes attrs = new RedirectAttributesModelMap();

        when(bindingResult.hasErrors()).thenReturn(true);

        String result = controller.createCategory(dto, bindingResult, model, attrs);

        assertEquals("admin/details/create-category", result);
    }

    @Test
    void createCategory_success_redirects() {
        CategoryDTO dto = new CategoryDTO();
        Model model = new ConcurrentModel();
        RedirectAttributes attrs = new RedirectAttributesModelMap();

        when(bindingResult.hasErrors()).thenReturn(false);

        String result = controller.createCategory(dto, bindingResult, model, attrs);

        verify(categoryService).createCategory(dto);
        assertEquals("redirect:/admin/details/list-all", result);
    }

    @Test
    void createCategory_duplicate_showsError() {
        CategoryDTO dto = new CategoryDTO();
        Model model = new ConcurrentModel();
        RedirectAttributes attrs = new RedirectAttributesModelMap();

        when(bindingResult.hasErrors()).thenReturn(false);
        doThrow(new DuplicateResourceException("exists"))
                .when(categoryService).createCategory(dto);

        String result = controller.createCategory(dto, bindingResult, model, attrs);

        assertEquals("admin/details/create-category", result);
        assertTrue(model.containsAttribute("errorMessage"));
    }

    /* -------------------------------------------------------
     * DEPARTURE AIRPORT
     * ------------------------------------------------------- */

    @Test
    void showCreateAirportForm_addsDto() {
        Model model = new ConcurrentModel();

        String view = controller.showCreateDepartureAirportForm(model);

        assertEquals("admin/details/create-departure-airport", view);
        assertTrue(model.containsAttribute("departureAirportDTO"));
    }

    @Test
    void createAirport_validationErrors_reloadForm() {
        DepartureAirportDTO dto = new DepartureAirportDTO();
        Model model = new ConcurrentModel();
        RedirectAttributes attrs = new RedirectAttributesModelMap();

        when(bindingResult.hasErrors()).thenReturn(true);

        String result = controller.createDepartureAirport(dto, bindingResult, model, attrs);

        assertEquals("admin/details/create-departure-airport", result);
    }

    @Test
    void createAirport_success_redirects() {
        DepartureAirportDTO dto = new DepartureAirportDTO();
        Model model = new ConcurrentModel();
        RedirectAttributes attrs = new RedirectAttributesModelMap();

        when(bindingResult.hasErrors()).thenReturn(false);

        String result = controller.createDepartureAirport(dto, bindingResult, model, attrs);

        verify(departureAirportService).createDepartureAirport(dto);
        assertEquals("redirect:/admin/details/list-all", result);
    }

    @Test
    void createAirport_duplicate_showsError() {
        DepartureAirportDTO dto = new DepartureAirportDTO();
        Model model = new ConcurrentModel();
        RedirectAttributes attrs = new RedirectAttributesModelMap();

        when(bindingResult.hasErrors()).thenReturn(false);
        doThrow(new DuplicateResourceException("exists"))
                .when(departureAirportService).createDepartureAirport(dto);

        String result = controller.createDepartureAirport(dto, bindingResult, model, attrs);

        assertEquals("admin/details/create-departure-airport", result);
        assertTrue(model.containsAttribute("errorMessage"));
    }

    /* -------------------------------------------------------
     * LIST ALL
     * ------------------------------------------------------- */

    @Test
    void listAllDetails_addsAllAttributes() {
        Model model = new ConcurrentModel();

        when(geoAreaService.getAllGeoAreas()).thenReturn(List.of());
        when(countryService.getAllCountries()).thenReturn(List.of());
        when(categoryService.getAllCategories()).thenReturn(List.of());
        when(departureAirportService.getAllDepartureAirports()).thenReturn(List.of());

        String view = controller.listAllDetails(model);

        assertEquals("admin/details/details-list", view);
        assertTrue(model.containsAttribute("geoAreas"));
        assertTrue(model.containsAttribute("countries"));
        assertTrue(model.containsAttribute("categories"));
        assertTrue(model.containsAttribute("airports"));
    }
}
