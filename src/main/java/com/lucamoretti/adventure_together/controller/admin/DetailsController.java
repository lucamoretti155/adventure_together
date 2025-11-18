package com.lucamoretti.adventure_together.controller.admin;

import com.lucamoretti.adventure_together.dto.details.CategoryDTO;
import com.lucamoretti.adventure_together.dto.details.CountryDTO;
import com.lucamoretti.adventure_together.dto.details.DepartureAirportDTO;
import com.lucamoretti.adventure_together.dto.details.GeoAreaDTO;
import com.lucamoretti.adventure_together.service.details.CategoryService;
import com.lucamoretti.adventure_together.service.details.CountryService;
import com.lucamoretti.adventure_together.service.details.DepartureAirportService;
import com.lucamoretti.adventure_together.service.details.GeoAreaService;
import com.lucamoretti.adventure_together.util.exception.DuplicateResourceException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

// Controller per la creazione e la visualizzazione degli attributi di dettaglio dei viaggi
// (GeoArea, Country, Category, DepartureAirport)

@Controller
@RequestMapping("/admin/details")
@RequiredArgsConstructor
public class DetailsController {

    private final GeoAreaService geoAreaService;
    private final CountryService countryService;
    private final CategoryService categoryService;
    private final DepartureAirportService departureAirportService;

    /* -------------------------------------------------------
     *                     GEOAREA
     * ------------------------------------------------------- */

    @GetMapping("/create-geoarea")
    public String showCreateGeoAreaForm(Model model) {
        model.addAttribute("geoAreaDTO", new GeoAreaDTO());
        return "admin/details/create-geoarea";
    }

    @PostMapping("/create-geoarea")
    public String createGeoArea(
            @Valid @ModelAttribute("geoAreaDTO") GeoAreaDTO dto,
            BindingResult result,
            Model model, RedirectAttributes redirectAttributes
    ) {
        if (result.hasErrors()) {
            return "admin/details/create-geoarea";
        }

        try {
            geoAreaService.createGeoArea(dto);
        } catch (DuplicateResourceException e) {
            model.addAttribute("errorMessage", e.getMessage());
            return "admin/details/create-geoarea";
        }

        redirectAttributes.addFlashAttribute("successMessage",
                "GeoArea creata con successo!");

        return "redirect:/admin/details/list-all";
    }

    /* -------------------------------------------------------
     *                     COUNTRY
     * ------------------------------------------------------- */

    @GetMapping("/create-country")
    public String showCreateCountryForm(Model model) {
        model.addAttribute("countryDTO", new CountryDTO());
        model.addAttribute("geoAreas", geoAreaService.getAllGeoAreas()); // select nel form
        return "admin/details/create-country";
    }

    @PostMapping("/create-country")
    public String createCountry(
            @Valid @ModelAttribute("countryDTO") CountryDTO dto,
            BindingResult result,
            Model model, RedirectAttributes redirectAttributes
    ) {
        if (result.hasErrors()) {
            model.addAttribute("geoAreas", geoAreaService.getAllGeoAreas());
            return "admin/details/create-country";
        }

        try {
            countryService.createCountry(dto);
        } catch (DuplicateResourceException e) {
            model.addAttribute("geoAreas", geoAreaService.getAllGeoAreas());
            model.addAttribute("errorMessage", e.getMessage());
            return "admin/details/create-country";
        }

        redirectAttributes.addFlashAttribute("successMessage",
                "Paese creato con successo!");

        return "redirect:/admin/details/list-all";
    }

    /* -------------------------------------------------------
     *                     CATEGORY
     * ------------------------------------------------------- */

    @GetMapping("/create-category")
    public String showCreateCategoryForm(Model model) {
        model.addAttribute("categoryDTO", new CategoryDTO());
        return "admin/details/create-category";
    }

    @PostMapping("/create-category")
    public String createCategory(
            @Valid @ModelAttribute("categoryDTO") CategoryDTO dto,
            BindingResult result,
            Model model, RedirectAttributes redirectAttributes
    ) {
        if (result.hasErrors()) {
            return "admin/details/create-category";
        }

        try {
            categoryService.createCategory(dto);
        } catch (DuplicateResourceException e) {
            model.addAttribute("errorMessage", e.getMessage());
            return "admin/details/create-category";
        }

        redirectAttributes.addFlashAttribute("successMessage",
                "Categoria creata con successo!");

        return "redirect:/admin/details/list-all";
    }

    /* -------------------------------------------------------
     *                     DEPARTURE AIRPORT
     * ------------------------------------------------------- */

    @GetMapping("/create-departure-airport")
    public String showCreateDepartureAirportForm(Model model) {
        model.addAttribute("departureAirportDTO", new DepartureAirportDTO());
        return "admin/details/create-departure-airport";
    }

    @PostMapping("/create-departure-airport")
    public String createDepartureAirport(
            @Valid @ModelAttribute("departureAirportDTO") DepartureAirportDTO dto,
            BindingResult result,
            Model model, RedirectAttributes redirectAttributes
    ) {
        if (result.hasErrors()) {
            return "admin/details/create-departure-airport";
        }

        try {
            departureAirportService.createDepartureAirport(dto);
        } catch (DuplicateResourceException e) {
            model.addAttribute("errorMessage", e.getMessage());
            return "admin/details/create-departure-airport";
        }

        redirectAttributes.addFlashAttribute("successMessage",
                "Aeroporto di partenza creato con successo!");

        return "redirect:/admin/details/list-all";
    }

    /* -------------------------------------------------------
     *                     LIST ALL
     * ------------------------------------------------------- */

    @GetMapping("/list-all")
    public String listAllDetails(Model model) {

        model.addAttribute("geoAreas", geoAreaService.getAllGeoAreas());
        model.addAttribute("countries", countryService.getAllCountries());
        model.addAttribute("categories", categoryService.getAllCategories());
        model.addAttribute("airports", departureAirportService.getAllDepartureAirports());

        return "admin/details/details-list";
    }
}
