package com.lucamoretti.adventure_together.repository.user;

import com.lucamoretti.adventure_together.model.user.Traveler;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

// Repository per l'entità Traveler

@Repository
public interface TravelerRepository extends JpaRepository<Traveler, Long> {
    Optional<Traveler> findByEmail(String email);
    Optional<Traveler> findById(Long id);
}
