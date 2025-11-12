package com.lucamoretti.adventure_together.service.details;

import com.lucamoretti.adventure_together.dto.details.CategoryDTO;

import java.util.List;

//Interfaccia del servizio per la gestione delle categorie.

public interface CategoryService {
    CategoryDTO createCategory(CategoryDTO dto);
    List<CategoryDTO> getAllCategories();
}
