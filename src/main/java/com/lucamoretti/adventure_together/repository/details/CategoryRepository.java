package com.lucamoretti.adventure_together.repository.details;

import com.lucamoretti.adventure_together.model.details.Category;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CategoryRepository extends JpaRepository<Category, Long> {
}
