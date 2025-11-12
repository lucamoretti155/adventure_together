package com.lucamoretti.adventure_together.repository.details;

import com.lucamoretti.adventure_together.model.details.DepartureAirport;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

/* Repository DepartureAirportRepository
   Interfaccia che estende JpaRepository per la gestione delle operazioni CRUD sull'entità DepartureAirport.
*/

public interface DepartureAirportRepository extends JpaRepository<DepartureAirport, Long> {
    Optional<DepartureAirport> findByCode(String code);
}
