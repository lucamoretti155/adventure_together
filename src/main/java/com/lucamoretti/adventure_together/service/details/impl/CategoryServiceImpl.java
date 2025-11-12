package com.lucamoretti.adventure_together.service.details.impl;

import com.lucamoretti.adventure_together.dto.details.CategoryDTO;
import com.lucamoretti.adventure_together.model.details.Category;
import com.lucamoretti.adventure_together.repository.details.CategoryRepository;
import com.lucamoretti.adventure_together.service.details.CategoryService;
import com.lucamoretti.adventure_together.util.exception.DuplicateResourceException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

// Implementazione del servizio per la gestione delle categorie.

@Service
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository categoryRepository;

    // Crea una nuova categoria se non esiste già.
    @Override
    @Transactional
    public CategoryDTO createCategory(CategoryDTO dto) {
        if (categoryRepository.existsByNameIgnoreCase(dto.getName())) {
            throw new DuplicateResourceException("La categoria esiste già: " + dto.getName());
        }

        Category category = new Category();
        category.setName(dto.getName());

        Category saved = categoryRepository.save(category);
        return CategoryDTO.fromEntity(saved);
    }

    // Recupera tutte le categorie esistenti.
    @Override
    public List<CategoryDTO> getAllCategories() {
        List<Category> categories = categoryRepository.findAll();
        return categories.stream()
                .map(CategoryDTO::fromEntity)
                .toList();
    }
}

