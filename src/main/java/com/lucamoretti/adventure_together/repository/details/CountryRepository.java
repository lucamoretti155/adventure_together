package com.lucamoretti.adventure_together.repository.details;

import com.lucamoretti.adventure_together.model.details.Country;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

/* Repository CountryRepository
   Interfaccia che estende JpaRepository per la gestione delle operazioni CRUD sull'entità Country.
*/

public interface CountryRepository extends JpaRepository<Country, Long> {
    Optional<Country> findByCountry(String country);
}
