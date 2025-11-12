package com.lucamoretti.adventure_together.service.details;

import com.lucamoretti.adventure_together.dto.details.CategoryDTO;

//Interfaccia del servizio per la gestione delle categorie.

public interface CategoryService {
    CategoryDTO createCategory(CategoryDTO dto);
}
