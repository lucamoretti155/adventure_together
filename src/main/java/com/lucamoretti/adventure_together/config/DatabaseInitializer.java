package com.lucamoretti.adventure_together.config;

import com.lucamoretti.adventure_together.model.details.Category;
import com.lucamoretti.adventure_together.model.details.Country;
import com.lucamoretti.adventure_together.model.details.DepartureAirport;
import com.lucamoretti.adventure_together.model.details.GeoArea;
import com.lucamoretti.adventure_together.model.trip.Trip;
import com.lucamoretti.adventure_together.model.trip.TripItinerary;
import com.lucamoretti.adventure_together.model.trip.TripItineraryDay;
import com.lucamoretti.adventure_together.model.user.Planner;
import com.lucamoretti.adventure_together.repository.details.CategoryRepository;
import com.lucamoretti.adventure_together.repository.details.CountryRepository;
import com.lucamoretti.adventure_together.repository.details.DepartureAirportRepository;
import com.lucamoretti.adventure_together.repository.details.GeoAreaRepository;
import com.lucamoretti.adventure_together.repository.trip.TripItineraryRepository;
import com.lucamoretti.adventure_together.repository.trip.TripRepository;
import com.lucamoretti.adventure_together.repository.user.PlannerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;
import java.util.Random;
import java.util.Set;

/*
    * Classe che inizializza il database con dati di riferimento all'avvio dell'applicazione
    * Se le tabelle sono vuote, vengono inseriti dati predefiniti per GeoArea, Country, Category e DepartureAirport
 */

@Order(2)
@Component
@RequiredArgsConstructor
public class DatabaseInitializer implements CommandLineRunner {

    private final DepartureAirportRepository departureAirportRepository;
    private final CategoryRepository categoryRepository;
    private final GeoAreaRepository geoAreaRepository;
    private final CountryRepository countryRepository;
    private final TripItineraryRepository itineraryRepository;
    private final TripRepository tripRepository;
    private final PlannerRepository plannerRepository;
    private final Random random = new Random();

    @Override
    public void run(String... args) {

        // GEO AREAS
        if (geoAreaRepository.count() == 0) {
            List<GeoArea> geoAreas = List.of(
                    new GeoArea(null, "Europa"),
                    new GeoArea(null, "Asia"),
                    new GeoArea(null, "Africa"),
                    new GeoArea(null, "America del Nord"),
                    new GeoArea(null, "America del Sud")
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


        if (itineraryRepository.count() > 0) {
            System.out.println(">>> TripData già presenti, nessuna inizializzazione.");
            return;
        }

        // ================================
        //  1) Recupero Planner (Admin inizializzato con mail)
        // ================================
        Planner planner = plannerRepository.findByEmail("demo.mail.app.java.project@gmail.com")
                .orElseThrow(() -> new IllegalStateException("Admin non trovato: assicurarsi che AdminInitializer sia attivo"));

        // ================================
        //  Recupero dati già popolati
        // ================================
        Country giappone = countryRepository.findByCountry("Giappone").orElseThrow();
        Country norvegia = countryRepository.findByCountry("Norvegia").orElseThrow();
        Country kenya = countryRepository.findByCountry("Kenya").orElseThrow();

        Category avventura = categoryRepository.findByName("Avventura").orElseThrow();
        Category cultura = categoryRepository.findByName("Cultura").orElseThrow();
        Category natura = categoryRepository.findByName("Natura").orElseThrow();

        DepartureAirport mxp = departureAirportRepository.findByCode("MXP").orElseThrow();
        DepartureAirport fco = departureAirportRepository.findByCode("FCO").orElseThrow();
        DepartureAirport bgy = departureAirportRepository.findByCode("BGY").orElseThrow();

        // ================================
        //  2) CREAZIONE ITINERARI
        // ================================

        TripItinerary itin1 = new TripItinerary();
        itin1.setTitle("Giappone Classico");
        itin1.setDescription("Viaggio completo tra Tokyo, Kyoto, Nara e Osaka. Cultura, modernità e tradizione.");
        itin1.setPicturePath("/images/giappone.png");
        itin1.setDurationInDays(7);
        itin1.setMinParticipants(5);
        itin1.setMaxParticipants(20);
        itin1.setPlanner(planner);
        itin1.setCountries(Set.of(giappone));
        itin1.setCategories(Set.of(cultura));
        itin1.setDepartureAirports(Set.of(mxp, fco));

        TripItinerary itin2 = new TripItinerary();
        itin2.setTitle("Avventura in Norvegia");
        itin2.setDescription("Un percorso di trekking tra fiordi, cascate e paesaggi mozzafiato.");
        itin2.setPicturePath("/images/norvegia.png");
        itin2.setDurationInDays(5);
        itin2.setMinParticipants(4);
        itin2.setMaxParticipants(15);
        itin2.setPlanner(planner);
        itin2.setCountries(Set.of(norvegia));
        itin2.setCategories(Set.of(avventura, natura));
        itin2.setDepartureAirports(Set.of(mxp, bgy));

        TripItinerary itin3 = new TripItinerary();
        itin3.setTitle("Safari in Kenya");
        itin3.setDescription("Un safari completo tra Masai Mara, Amboseli e cultura Masai.");
        itin3.setPicturePath("/images/kenya.png");
        itin3.setDurationInDays(6);
        itin3.setMinParticipants(6);
        itin3.setMaxParticipants(12);
        itin3.setPlanner(planner);
        itin3.setCountries(Set.of(kenya));
        itin3.setCategories(Set.of(avventura, natura));
        itin3.setDepartureAirports(Set.of(fco));

        itineraryRepository.saveAll(List.of(itin1, itin2, itin3));

        // ================================
        //  3) GIORNI ITINERARIO REALISTICI
        // ================================

        // Giappone 7 giorni
        itin1.setDays(List.of(
                new TripItineraryDay(null, 1, "Arrivo a Tokyo", "Arrivo in aeroporto, transfer in hotel, prima visita della città.", itin1),
                new TripItineraryDay(null, 2, "Tokyo", "Visita di Asakusa, Shibuya e Shinjuku.", itin1),
                new TripItineraryDay(null, 3, "Hakone", "Escursione sul Monte Fuji e relax negli onsen.", itin1),
                new TripItineraryDay(null, 4, "Kyoto", "Templi, torii rossi e foresta di bambù.", itin1),
                new TripItineraryDay(null, 5, "Nara", "Visita al tempio Todai-Ji e ai cervi sacri.", itin1),
                new TripItineraryDay(null, 6, "Osaka", "Castello di Osaka e street food di Dotonbori.", itin1),
                new TripItineraryDay(null, 7, "Rientro", "Trasferimento in aeroporto e volo per l’Italia.", itin1)
        ));

        // Norvegia 5 giorni
        itin2.setDays(List.of(
                new TripItineraryDay(null, 1, "Arrivo a Bergen", "Trasferimento in hotel e visita al porto.", itin2),
                new TripItineraryDay(null, 2, "Fiordi", "Crociera nel fiordo di Nærøyfjord.", itin2),
                new TripItineraryDay(null, 3, "Flåm", "Treno panoramico più famoso d’Europa.", itin2),
                new TripItineraryDay(null, 4, "Trekking", "Escursione nella valle di Aurland.", itin2),
                new TripItineraryDay(null, 5, "Rientro", "Ritorno in Italia.", itin2)
        ));

        // Kenya 6 giorni
        itin3.setDays(List.of(
                new TripItineraryDay(null, 1, "Arrivo a Nairobi", "Accoglienza, hotel e visita museo nazionale.", itin3),
                new TripItineraryDay(null, 2, "Masai Mara", "Safari mattutino e pomeridiano.", itin3),
                new TripItineraryDay(null, 3, "Villaggio Masai", "Incontro con la tribù locale.", itin3),
                new TripItineraryDay(null, 4, "Amboseli", "Safari e vista sul Kilimangiaro.", itin3),
                new TripItineraryDay(null, 5, "Amboseli", "Secondo safari, paesaggi e fotografia naturalistica.", itin3),
                new TripItineraryDay(null, 6, "Rientro", "Trasferimento in Italia.", itin3)
        ));

        itineraryRepository.saveAll(List.of(itin1, itin2, itin3));

        // ================================
        // 📌 4) CREAZIONE TRIP RANDOM
        // ================================
        Trip trip1a = createRandomTrip(itin1, planner, 1890);
        Trip trip1b = createRandomTrip(itin1, planner, 1990);

        Trip trip2a = createRandomTrip(itin2, planner, 1490);
        Trip trip2b = createRandomTrip(itin2, planner, 1590);

        Trip trip3a = createRandomTrip(itin3, planner, 2390);
        Trip trip3b = createRandomTrip(itin3, planner, 2490);

        tripRepository.saveAll(List.of(trip1a, trip1b, trip2a, trip2b, trip3a, trip3b));

        System.out.println("*** TripItineraries + Trips + Days inizializzati (realistici) ***");
    }


    // ==============================================================================================
    //  FUNZIONE PER GENERARE TRIP RANDOM CON DATE REALISTICHE
    // ==============================================================================================
    private Trip createRandomTrip(TripItinerary itinerary, Planner planner, double price) {

        int daysAhead = 30 + random.nextInt(540); // 1–18 mesi

        LocalDate departure = LocalDate.now().plusDays(daysAhead);
        LocalDate returnDate = departure.plusDays(itinerary.getDurationInDays());

        Trip t = new Trip();
        t.setTripItinerary(itinerary);
        t.setPlanner(planner);
        t.setTripIndividualCost(price);

        t.setDateStartBookings(LocalDate.now());
        t.setDateEndBookings(departure.minusDays(20));

        t.setDateDeparture(departure);
        t.setDateReturn(returnDate);

        // Stato iniziale
        t.open();

        return t;
    }
}
