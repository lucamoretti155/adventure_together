package com.lucamoretti.adventure_together.repository.details;

import com.lucamoretti.adventure_together.model.details.Country;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CountryRepository extends JpaRepository<Country, Long> {
}
