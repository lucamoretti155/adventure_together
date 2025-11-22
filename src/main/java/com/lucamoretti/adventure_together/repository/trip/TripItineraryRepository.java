package com.lucamoretti.adventure_together.repository.trip;

import com.lucamoretti.adventure_together.model.trip.TripItinerary;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/*
 Interfaccia repository per la gestione delle entità TripItinerary.
 Estende JpaRepository per fornire operazioni CRUD e query personalizzate.
 */

@Repository
public interface TripItineraryRepository extends JpaRepository<TripItinerary, Long> {

    // Trova un itinerario di viaggio in base al titolo. NB: il titolo è unico.
    Optional<TripItinerary> findByTitle(String title);

    boolean existsByTitle(String title);

    // Trova tutti gli itinerari di viaggio che includono una specifica nazione data l'id della nazione.
    @Query("""
           select distinct i from TripItinerary i
           join i.countries c
           where c.id = :countryId
           """)
    List<TripItinerary> findByCountry(Long countryId);

    // Trova tutti gli itinerari di viaggio che includono una specifica area geografica data l'id dell'area geografica.
    @Query("""
           select distinct i from TripItinerary i
           join i.countries c
           where c.geoArea.id = :geoAreaId
           """)
    List<TripItinerary> findByGeoArea(Long geoAreaId);

    // Trova tutti gli itinerari di viaggio che appartengono a una specifica categoria data l'id della categoria.
    @Query("""
           select distinct i from TripItinerary i
           join i.categories cat
           where cat.id = :categoryId
           """)
    List<TripItinerary> findByCategory(Long categoryId);

    // Trova tutti gli itinerari di viaggio che appartengono a una lista di categorie data una lista di id di categorie.
    @Query("""
           select distinct i from TripItinerary i
           join i.categories cat
           where cat.id in :categoryIds
           """)
    List<TripItinerary> findByCategories(List<Long> categoryIds);

    // Trova tutti gli itinerari di viaggio creati da uno specifico planner dato l'id del planner.
    List<TripItinerary> findByPlannerId(Long plannerId);


}

