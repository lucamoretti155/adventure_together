package com.lucamoretti.adventure_together.repository.user;

import com.lucamoretti.adventure_together.model.user.Planner;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

// Repository per l'entità Planner

@Repository
public interface PlannerRepository extends JpaRepository<Planner, Long> {
    Optional<Planner> findByEmail(String email);
}
