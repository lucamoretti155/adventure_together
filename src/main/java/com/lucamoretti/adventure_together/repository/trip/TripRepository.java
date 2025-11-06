package com.lucamoretti.adventure_together.repository.trip;

import com.lucamoretti.adventure_together.model.trip.Trip;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/*
 Interfaccia repository per la gestione delle entità Trip.
 Estende JpaRepository per fornire operazioni CRUD e query personalizzate.
*/

@Repository
public interface TripRepository extends JpaRepository<Trip, Long> {
    // Trova tutti i viaggi associati a uno specifico itinerario di viaggio dato l'id dell'itinerario.
    List<Trip> findByTripItinerary_Id(Long itineraryId);

    // Trova tutti i viaggi pianificati da uno specifico planner dato l'id del planner.
    List<Trip> findByPlanner_Id(Long plannerId);

    // Trova tutti i viaggi con data di partenza compresa tra due date specificate.
    List<Trip> findByDateDepartureBetween(LocalDate from, LocalDate to);

    // Trova tutti i viaggi futuri (dopo oggi)
    @Query("""
           select t from Trip t
           where t.dateDeparture > :today
           order by t.dateDeparture asc
           """)
    List<Trip> findFutureTrips(LocalDate today);

    // Trova tutti i viaggi che sono ancora aperti per le prenotazioni (data di fine prenotazioni >= oggi)
    @Query("""
           select t from Trip t
           where t.dateEndBookings >= :today
           order by t.dateDeparture asc
           """)
    List<Trip> findOpenForBooking(LocalDate today);

    // Trova tutti i viaggi il cui stato è ConfirmedOpen
    @Query("select t from Trip t where type(t.state) = com.lucamoretti.adventure_together.model.trip.state.ConfirmedOpen")
    List<Trip> findAllConfirmedOpen();

    // Trova tutti i viaggi per un dato "State" specificato come parametro
    @Query("""
           select t from Trip t
           where type(t.state) = :stateClass
           order by t.dateDeparture asc
           """)
    List<Trip> findByState(Class<?> stateClass);

    // Trova tutti i viaggi ancora prenotabili che hanno la partenza entro 30 giorni da oggi
    // Per homepage: viaggi prenotabili con partenza a breve (< 30 giorni)
    @Query("""
           select t from Trip t
           where t.dateEndBookings >= :today
           and t.dateDeparture between :today and :todayPlus30
           order by t.dateDeparture asc
           """)
    List<Trip> findUpcomingBookableTrips(LocalDate today, LocalDate todayPlus30);

    // Trova un viaggio per id e applica un lock pessimista di scrittura
    // Utile per operazioni che modificano lo stato del viaggio in modo concorrente
    // @Lock blocca la riga nel DB durante la transazione, impedendo ad altri thread di leggerla o modificarla fino al commit.
    @Query("select t from Trip t where t.id = :tripId")
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    Optional<Trip> findByIdForUpdate(Long tripId);



}

