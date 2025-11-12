package com.lucamoretti.adventure_together.repository.details;

import com.lucamoretti.adventure_together.dto.details.CountryDTO;
import com.lucamoretti.adventure_together.model.details.Country;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.Set;

/* Repository CountryRepository
   Interfaccia che estende JpaRepository per la gestione delle operazioni CRUD sull'entità Country.
*/

public interface CountryRepository extends JpaRepository<Country, Long> {
    Optional<Country> findByCountry(String country);
    boolean existsByCountryIgnoreCase(String name);
    List<Country> findAllByGeoAreaId(Long geoAreaId);
    List<Country> findAllByIdIn(Set<Long> ids);
}
