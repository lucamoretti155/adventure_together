package com.lucamoretti.adventure_together.config;

import com.lucamoretti.adventure_together.model.details.DepartureAirport;
import com.lucamoretti.adventure_together.repository.details.DepartureAirportRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;
import java.util.List;

@Component
public class AirportInitializer implements ApplicationRunner {

    @Autowired
    private DepartureAirportRepository departureAirportRepository;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        if (departureAirportRepository.count() > 0) {
            System.out.println("Tabella DepartureAirport già inizializzata.");
            return; // già inizializzato
        }

        List<DepartureAirport> airports = List.of(
                new DepartureAirport(null, "FCO", "Roma-Fiumicino", "Roma"),
                new DepartureAirport(null, "MXP", "Milano-Malpensa", "Milano"),
                new DepartureAirport(null, "LIN", "Milano-Linate", "Milano"),
                new DepartureAirport(null, "BGY", "Bergamo-Orio al Serio", "Bergamo"),
                new DepartureAirport(null, "VCE", "Venezia-Marco Polo", "Venezia"),
                new DepartureAirport(null, "NAP", "Napoli-Capodichino", "Napoli"),
                new DepartureAirport(null, "CTA", "Catania-Fontanarossa", "Catania"),
                new DepartureAirport(null, "BLQ", "Bologna-Borgo Panigale", "Bologna"),
                new DepartureAirport(null, "PMO", "Palermo-Punta Raisi", "Palermo"),
                new DepartureAirport(null, "BRI", "Bari-Palese", "Bari"),
                new DepartureAirport(null, "PSA", "Pisa-San Giusto", "Pisa"),
                new DepartureAirport(null, "FLR", "Firenze-Peretola", "Firenze"),
                new DepartureAirport(null, "CAG", "Cagliari-Elmas", "Cagliari"),
                new DepartureAirport(null, "GOA", "Genova-Sestri", "Genova"),
                new DepartureAirport(null, "TRN", "Torino-Caselle", "Torino"),
                new DepartureAirport(null, "TSF", "Treviso-Sant’Angelo", "Treviso"),
                new DepartureAirport(null, "AHO", "Alghero-Fertilia", "Alghero"),
                new DepartureAirport(null, "REG", "Reggio Calabria", "Reggio Calabria"),
                new DepartureAirport(null, "VRN", "Verona-Villafranca", "Verona"),
                new DepartureAirport(null, "OLB", "Olbia-Costa Smeralda", "Olbia")
        );

        departureAirportRepository.saveAll(airports);
        System.out.println("Initialized " + airports.size() + " DepartureAirport entries.");
    }
}

