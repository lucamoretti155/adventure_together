package com.lucamoretti.adventure_together.service.details.impl;

import com.lucamoretti.adventure_together.dto.details.CountryDTO;
import com.lucamoretti.adventure_together.model.details.Country;
import com.lucamoretti.adventure_together.model.details.GeoArea;
import com.lucamoretti.adventure_together.repository.details.CountryRepository;
import com.lucamoretti.adventure_together.repository.details.GeoAreaRepository;
import com.lucamoretti.adventure_together.service.details.CountryService;
import com.lucamoretti.adventure_together.util.exception.DuplicateResourceException;
import com.lucamoretti.adventure_together.util.exception.ResourceNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;

// Implementazione del servizio per la gestione delle country.

@Service
@RequiredArgsConstructor
public class CountryServiceImpl implements CountryService {

    private final CountryRepository countryRepository;
    private final GeoAreaRepository geoAreaRepository;

    // Creazione di una nuova country con controllo dei duplicati e associazione alla geoArea.
    @Transactional
    @Override
    public CountryDTO createCountry(CountryDTO dto) {
        // controllo duplicati (case-insensitive)
        if (countryRepository.existsByCountryIgnoreCase(dto.getCountry())) {
            throw new DuplicateResourceException("La Country esiste già: " + dto.getCountry());
        }

        // recupera la GeoArea associata
        GeoArea geoArea = geoAreaRepository.findById(dto.getGeoAreaId())
                .orElseThrow(() -> new ResourceNotFoundException("GeoArea","id", dto.getGeoAreaId()));

        // conversione tramite metodo del DTO
        Country country = dto.toEntity(geoArea);

        Country saved = countryRepository.save(country);
        return CountryDTO.fromEntity(saved);
    }

    // Recupero di tutte le country o di quelle appartenenti a una specifica geoArea.

    @Override
    public List<CountryDTO> getAllCountries() {
        List<Country> countries = countryRepository.findAll();
        return countries.stream()
                .map(CountryDTO::fromEntity)
                .toList();
    }

    @Override
    public List<CountryDTO> getAllCountriesByGeoAreaId(Long geoAreaId) {
        List<Country> countries = countryRepository.findAllByGeoAreaId(geoAreaId);
        return countries.stream()
                .map(CountryDTO::fromEntity)
                .toList();
    }

    // Recuper del dettagli di una country tramite ID o un insieme di ID.

    @Override
    public CountryDTO getCountryById(Long id) {
        Country country = countryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Country","id", id));
        return CountryDTO.fromEntity(country);
    }

    @Override
    public List<CountryDTO> getCountryBySetOfId(Set<Long> ids) {
        List<Country> countries = countryRepository.findAllByIdIn(ids);
        return countries.stream()
                .map(CountryDTO::fromEntity)
                .toList();
    }
}
