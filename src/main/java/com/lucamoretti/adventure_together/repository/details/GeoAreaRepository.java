package com.lucamoretti.adventure_together.repository.details;

import com.lucamoretti.adventure_together.model.details.GeoArea;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

/* Repository GeoAreaRepository
   Interfaccia che estende JpaRepository per la gestione delle operazioni CRUD sull'entità GeoArea.
*/

public interface GeoAreaRepository extends JpaRepository<GeoArea, Long> {
    Optional<GeoArea> findByGeoArea(String geoArea);
}
