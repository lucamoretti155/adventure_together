package com.lucamoretti.adventure_together.service.details.impl;

import com.lucamoretti.adventure_together.dto.details.CategoryDTO;
import com.lucamoretti.adventure_together.model.details.Category;
import com.lucamoretti.adventure_together.repository.details.CategoryRepository;
import com.lucamoretti.adventure_together.util.exception.DuplicateResourceException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CategoryServiceImplTest {

    @Mock
    private CategoryRepository categoryRepository;

    @InjectMocks
    private CategoryServiceImpl service;

    // ------------------------------------------------------------
    //                      CREATE CATEGORY
    // ------------------------------------------------------------

    @Test
    void createCategory_success() {
        CategoryDTO dto = new CategoryDTO(null, "Avventura");

        when(categoryRepository.existsByNameIgnoreCase("Avventura"))
                .thenReturn(false);

        Category saved = new Category();
        saved.setId(10L);
        saved.setName("Avventura");

        when(categoryRepository.save(any())).thenReturn(saved);

        CategoryDTO result = service.createCategory(dto);

        assertNotNull(result);
        assertEquals(10L, result.getId());
        assertEquals("Avventura", result.getName());

        verify(categoryRepository).save(any());
    }

    @Test
    void createCategory_alreadyExists_throwsException() {
        CategoryDTO dto = new CategoryDTO(null, "Avventura");

        when(categoryRepository.existsByNameIgnoreCase("Avventura"))
                .thenReturn(true);

        assertThrows(
                DuplicateResourceException.class,
                () -> service.createCategory(dto)
        );

        verify(categoryRepository, never()).save(any());
    }

    // ------------------------------------------------------------
    //                      GET ALL CATEGORIES
    // ------------------------------------------------------------

    @Test
    void getAllCategories_sortedAlphabeticallyIgnoreCase() {

        Category c1 = new Category();
        c1.setId(1L);
        c1.setName("zeta");

        Category c2 = new Category();
        c2.setId(2L);
        c2.setName("Alfa");

        Category c3 = new Category();
        c3.setId(3L);
        c3.setName("beta");

        when(categoryRepository.findAll())
                .thenReturn(List.of(c1, c2, c3));

        List<CategoryDTO> result = service.getAllCategories();

        assertEquals(3, result.size());
        assertEquals("Alfa", result.get(0).getName());
        assertEquals("beta", result.get(1).getName());
        assertEquals("zeta", result.get(2).getName());

        // verifica che findAll sia stato invocato
        verify(categoryRepository).findAll();
    }
}

