package com.lucamoretti.adventure_together.service.details;

import com.lucamoretti.adventure_together.dto.details.DepartureAirportDTO;

// Interfaccia per il servizio di gestione degli aeroporti di partenza

public interface DepartureAirportService {
    DepartureAirportDTO createDepartureAirport(DepartureAirportDTO dto);
}
