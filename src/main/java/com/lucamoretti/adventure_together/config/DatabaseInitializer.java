package com.lucamoretti.adventure_together.config;

import com.lucamoretti.adventure_together.model.details.Category;
import com.lucamoretti.adventure_together.model.details.Country;
import com.lucamoretti.adventure_together.model.details.DepartureAirport;
import com.lucamoretti.adventure_together.model.details.GeoArea;
import com.lucamoretti.adventure_together.repository.details.CategoryRepository;
import com.lucamoretti.adventure_together.repository.details.CountryRepository;
import com.lucamoretti.adventure_together.repository.details.DepartureAirportRepository;
import com.lucamoretti.adventure_together.repository.details.GeoAreaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import java.util.List;

/*
    * Classe che inizializza il database con dati di riferimento all'avvio dell'applicazione
    * Se le tabelle sono vuote, vengono inseriti dati predefiniti per GeoArea, Country, Category e DepartureAirport
 */

@Component
@RequiredArgsConstructor
public class DatabaseInitializer implements CommandLineRunner {

    private final DepartureAirportRepository departureAirportRepository;
    private final CategoryRepository categoryRepository;
    private final GeoAreaRepository geoAreaRepository;
    private final CountryRepository countryRepository;

    @Override
    public void run(String... args) {

        // GEO AREAS
        if (geoAreaRepository.count() == 0) {
            List<GeoArea> geoAreas = List.of(
                    new GeoArea(null, "Europa"),
                    new GeoArea(null, "Asia"),
                    new GeoArea(null, "Africa"),
                    new GeoArea(null, "America del Nord"),
                    new GeoArea(null, "America del Sud"),
                    new GeoArea(null, "Oceania")
            );
            geoAreaRepository.saveAll(geoAreas);
            System.out.println("***  GeoAreas inizializzate: " + geoAreas.size() + " ***");
        }

        // COUNTRIES
        if (countryRepository.count() == 0) {
            GeoArea europa = geoAreaRepository.findByGeoArea("Europa").orElseThrow();
            GeoArea asia = geoAreaRepository.findByGeoArea("Asia").orElseThrow();
            GeoArea africa = geoAreaRepository.findByGeoArea("Africa").orElseThrow();
            GeoArea nordAmerica = geoAreaRepository.findByGeoArea("America del Nord").orElseThrow();
            GeoArea sudAmerica = geoAreaRepository.findByGeoArea("America del Sud").orElseThrow();

            List<Country> countries = List.of(
                    new Country(null, "Norvegia", europa),
                    new Country(null, "Scozia", europa),
                    new Country(null, "Finlandia", europa),
                    new Country(null, "Giappone", asia),
                    new Country(null, "Thailandia", asia),
                    new Country(null, "Cina", asia),
                    new Country(null, "Kenya", africa),
                    new Country(null, "Marocco", africa),
                    new Country(null, "Sudafrica", africa),
                    new Country(null, "Canada", nordAmerica),
                    new Country(null, "Stati Uniti", nordAmerica),
                    new Country(null, "Messico", nordAmerica),
                    new Country(null, "Perù", sudAmerica),
                    new Country(null, "Brasile", sudAmerica),
                    new Country(null, "Argentina", sudAmerica)
            );
            countryRepository.saveAll(countries);
            System.out.println("***  Countries inizializzate: " + countries.size() + " ***");
        }

        // CATEGORIES
        if (categoryRepository.count() == 0) {
            List<Category> categories = List.of(
                    new Category(null, "Avventura"),
                    new Category(null, "Cultura"),
                    new Category(null, "Natura"),
                    new Category(null, "Relax"),
                    new Category(null, "Meditazione"),
                    new Category(null, "Montagna")
            );
            categoryRepository.saveAll(categories);
            System.out.println("***  Categories inizializzate: " + categories.size() + " ***");
        }

        // DEPARTURE AIRPORTS
        if (departureAirportRepository.count() == 0) {
            List<DepartureAirport> airports = List.of(
                    new DepartureAirport(null, "FCO", "Aeroporto di Roma Fiumicino", "Roma"),
                    new DepartureAirport(null, "CIA", "Aeroporto di Roma Ciampino", "Roma"),
                    new DepartureAirport(null, "MXP", "Aeroporto di Milano Malpensa", "Milano"),
                    new DepartureAirport(null, "LIN", "Aeroporto di Milano Linate", "Milano"),
                    new DepartureAirport(null, "BGY", "Aeroporto di Bergamo Orio al Serio", "Bergamo"),
                    new DepartureAirport(null, "VCE", "Aeroporto di Venezia Marco Polo", "Venezia"),
                    new DepartureAirport(null, "VRN", "Aeroporto di Verona Villafranca", "Verona"),
                    new DepartureAirport(null, "BLQ", "Aeroporto di Bologna Guglielmo Marconi", "Bologna"),
                    new DepartureAirport(null, "NAP", "Aeroporto di Napoli Capodichino", "Napoli"),
                    new DepartureAirport(null, "CTA", "Aeroporto di Catania Fontanarossa", "Catania"),
                    new DepartureAirport(null, "PMO", "Aeroporto di Palermo Punta Raisi", "Palermo"),
                    new DepartureAirport(null, "OLB", "Aeroporto di Olbia Costa Smeralda", "Olbia"),
                    new DepartureAirport(null, "CAG", "Aeroporto di Cagliari Elmas", "Cagliari"),
                    new DepartureAirport(null, "PSA", "Aeroporto di Pisa Galileo Galilei", "Pisa"),
                    new DepartureAirport(null, "TRN", "Aeroporto di Torino Caselle", "Torino")
            );
            departureAirportRepository.saveAll(airports);
            System.out.println("***  DepartureAirports inizializzati: " + airports.size() + " ***");
        }
    }
}
