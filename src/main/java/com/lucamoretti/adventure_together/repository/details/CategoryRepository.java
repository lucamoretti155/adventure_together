package com.lucamoretti.adventure_together.repository.details;

import com.lucamoretti.adventure_together.model.details.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

/* Repository CategoryRepository
   Interfaccia che estende JpaRepository per la gestione delle operazioni CRUD sull'entità Category.
*/

public interface CategoryRepository extends JpaRepository<Category, Long> {
    Optional<Category> findByName(String name);
    boolean existsByNameIgnoreCase(String name);
}
