package com.lucamoretti.adventure_together.controller.admin;

import com.lucamoretti.adventure_together.dto.user.AdminDTO;
import com.lucamoretti.adventure_together.dto.user.PlannerDTO;
import com.lucamoretti.adventure_together.service.user.UserService;
import com.lucamoretti.adventure_together.util.exception.DuplicateResourceException;

import com.lucamoretti.adventure_together.util.passwordGenerator.PasswordGeneratorService;
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
class AdminControllerTest {

    @Mock private UserService userService;
    @Mock private PasswordGeneratorService passwordGeneratorService;
    @Mock private BindingResult bindingResult;

    @InjectMocks
    private AdminController controller;

    // --------------------------------------------------------------
    // GET /create-planner
    // --------------------------------------------------------------
    @Test
    void showCreatePlannerForm_addsEmptyDtoAndReturnsView() {
        Model model = new ConcurrentModel();

        String view = controller.showCreatePlannerForm(model);

        assertEquals("admin/create-planner", view);
        assertTrue(model.containsAttribute("plannerDTO"));
    }

    // --------------------------------------------------------------
    // POST /create-planner (validation errors)
    // --------------------------------------------------------------
    @Test
    void createPlanner_whenValidationErrors_returnForm() {
        PlannerDTO dto = new PlannerDTO();
        RedirectAttributes attrs = new RedirectAttributesModelMap();

        when(bindingResult.hasErrors()).thenReturn(true);

        String result = controller.createPlanner(dto, bindingResult, attrs);

        assertEquals("admin/create-planner", result);
        assertTrue(attrs.getFlashAttributes().containsKey("plannerDTO"));
    }

    // --------------------------------------------------------------
    // POST /create-planner (success)
    // --------------------------------------------------------------
    @Test
    void createPlanner_success_redirectsToList() {
        PlannerDTO dto = new PlannerDTO();
        RedirectAttributes attrs = new RedirectAttributesModelMap();

        when(bindingResult.hasErrors()).thenReturn(false);
        when(passwordGeneratorService.generateSecurePassword()).thenReturn("TEMP123");

        String result = controller.createPlanner(dto, bindingResult, attrs);

        verify(userService).registerPlanner(dto, "TEMP123");

        assertEquals("redirect:/admin/planner-list", result);
        assertTrue(attrs.getFlashAttributes().containsKey("successMessage"));
    }

    // --------------------------------------------------------------
    // POST /create-planner (duplicate)
    // --------------------------------------------------------------
    @Test
    void createPlanner_duplicate_redirectsBack() {
        PlannerDTO dto = new PlannerDTO();
        RedirectAttributes attrs = new RedirectAttributesModelMap();

        when(bindingResult.hasErrors()).thenReturn(false);
        when(passwordGeneratorService.generateSecurePassword()).thenReturn("TEMP123");
        doThrow(new DuplicateResourceException("exists"))
                .when(userService).registerPlanner(any(), anyString());

        String result = controller.createPlanner(dto, bindingResult, attrs);

        assertEquals("redirect:/admin/create-planner", result);
        assertTrue(attrs.getFlashAttributes().containsKey("errorMessage"));
    }

    // --------------------------------------------------------------
    // GET /create-admin
    // --------------------------------------------------------------
    @Test
    void showCreateAdminForm_addsEmptyDtoAndReturnsView() {
        Model model = new ConcurrentModel();

        String view = controller.showCreateAdminForm(model);

        assertEquals("admin/create-admin", view);
        assertTrue(model.containsAttribute("adminDTO"));
    }

    // --------------------------------------------------------------
    // POST /create-admin (validation errors)
    // --------------------------------------------------------------
    @Test
    void createAdmin_whenValidationErrors_returnForm() {
        AdminDTO dto = new AdminDTO();
        RedirectAttributes attrs = new RedirectAttributesModelMap();

        when(bindingResult.hasErrors()).thenReturn(true);

        String result = controller.createAdmin(dto, bindingResult, attrs);

        assertEquals("admin/create-admin", result);
        assertTrue(attrs.getFlashAttributes().containsKey("adminDTO"));
    }

    // --------------------------------------------------------------
    // POST /create-admin (success)
    // --------------------------------------------------------------
    @Test
    void createAdmin_success_redirectsToList() {
        AdminDTO dto = new AdminDTO();
        RedirectAttributes attrs = new RedirectAttributesModelMap();

        when(bindingResult.hasErrors()).thenReturn(false);
        when(passwordGeneratorService.generateSecurePassword()).thenReturn("TEMP123");

        String result = controller.createAdmin(dto, bindingResult, attrs);

        verify(userService).registerAdmin(dto, "TEMP123");

        assertEquals("redirect:/admin/admin-list", result);
        assertTrue(attrs.getFlashAttributes().containsKey("successMessage"));
    }

    // --------------------------------------------------------------
    // POST /create-admin (duplicate)
    // --------------------------------------------------------------
    @Test
    void createAdmin_duplicate_redirectsBack() {
        AdminDTO dto = new AdminDTO();
        RedirectAttributes attrs = new RedirectAttributesModelMap();

        when(bindingResult.hasErrors()).thenReturn(false);
        when(passwordGeneratorService.generateSecurePassword()).thenReturn("TEMP123");
        doThrow(new DuplicateResourceException("exists"))
                .when(userService).registerAdmin(any(), anyString());

        String result = controller.createAdmin(dto, bindingResult, attrs);

        assertEquals("redirect:/admin/create-admin", result);
        assertTrue(attrs.getFlashAttributes().containsKey("errorMessage"));
    }

    // --------------------------------------------------------------
    // GET /planner-list
    // --------------------------------------------------------------
    @Test
    void listPlanners_addsListToModel() {
        Model model = new ConcurrentModel();

        when(userService.getAllPlanners()).thenReturn(List.of());

        String view = controller.listPlanners(model);

        assertEquals("/admin/planner-list", view);
        assertTrue(model.containsAttribute("planners"));
    }

    // --------------------------------------------------------------
    // GET /admin-list
    // --------------------------------------------------------------
    @Test
    void listAdmins_addsListToModel() {
        Model model = new ConcurrentModel();

        when(userService.getAllAdmins()).thenReturn(List.of());

        String view = controller.listAdmins(model);

        assertEquals("/admin/admin-list", view);
        assertTrue(model.containsAttribute("admins"));
    }

    // --------------------------------------------------------------
    // POST /deactivate-user
    // --------------------------------------------------------------
    @Test
    void deactivateUser_success() {
        RedirectAttributes attrs = new RedirectAttributesModelMap();

        String result = controller.deactivateUser(5L, attrs);

        verify(userService).deactivateUser(5L);
        assertEquals("redirect:/admin/dashboard", result);
        assertTrue(attrs.getFlashAttributes().containsKey("successMessage"));
    }

    @Test
    void deactivateUser_exception_setsErrorMessage() {
        RedirectAttributes attrs = new RedirectAttributesModelMap();

        doThrow(new RuntimeException("fail"))
                .when(userService).deactivateUser(5L);

        controller.deactivateUser(5L, attrs);

        assertTrue(attrs.getFlashAttributes().containsKey("errorMessage"));
    }

    // --------------------------------------------------------------
    // POST /activate-user
    // --------------------------------------------------------------
    @Test
    void activateUser_success() {
        RedirectAttributes attrs = new RedirectAttributesModelMap();

        String result = controller.activateUser(5L, attrs);

        verify(userService).activateUser(5L);
        assertEquals("redirect:/admin/dashboard", result);
        assertTrue(attrs.getFlashAttributes().containsKey("successMessage"));
    }

    @Test
    void activateUser_exception_setsErrorMessage() {
        RedirectAttributes attrs = new RedirectAttributesModelMap();

        doThrow(new RuntimeException("fail"))
                .when(userService).activateUser(5L);

        controller.activateUser(5L, attrs);

        assertTrue(attrs.getFlashAttributes().containsKey("errorMessage"));
    }
}
