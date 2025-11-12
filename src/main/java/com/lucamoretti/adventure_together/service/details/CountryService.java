package com.lucamoretti.adventure_together.service.details;

import com.lucamoretti.adventure_together.dto.details.CountryDTO;
import com.lucamoretti.adventure_together.model.details.GeoArea;

import java.util.List;
import java.util.Set;

// Interfaccia del servizio per la gestione dei paesi.

public interface CountryService {
    CountryDTO createCountry(CountryDTO dto);
    List<CountryDTO> getAllCountries();
    List<CountryDTO> getAllCountriesByGeoAreaId(Long geoAreaId);
    CountryDTO getCountryById(Long id);
    List<CountryDTO> getCountryBySetOfId(Set<Long> ids);
}
