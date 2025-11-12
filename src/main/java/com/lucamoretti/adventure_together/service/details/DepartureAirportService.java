package com.lucamoretti.adventure_together.service.details;

import com.lucamoretti.adventure_together.dto.details.DepartureAirportDTO;

import java.util.List;
import java.util.Set;

// Interfaccia per il servizio di gestione degli aeroporti di partenza

public interface DepartureAirportService {
    DepartureAirportDTO createDepartureAirport(DepartureAirportDTO dto);
    List<DepartureAirportDTO> getAllDepartureAirports();
    List<DepartureAirportDTO> getDepartureAirportsBySetOfIds(Set<Long> ids);
}
