package com.lucamoretti.adventure_together.service.details;

import com.lucamoretti.adventure_together.dto.details.CountryDTO;

// Interfaccia del servizio per la gestione dei paesi.

public interface CountryService {
    CountryDTO createCountry(CountryDTO dto);
}
