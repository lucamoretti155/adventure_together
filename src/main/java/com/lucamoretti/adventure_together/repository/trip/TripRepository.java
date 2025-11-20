package com.lucamoretti.adventure_together.repository.trip;

import com.lucamoretti.adventure_together.model.trip.Trip;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
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

    // Trova tutti i viaggi con data di ritorno uguale a una data specificata.
    List<Trip> findByDateReturnEquals(LocalDate date);

    // Trova tutti i viaggi con data di partenza uguale a una data specificata.
    List<Trip> findByDateDepartureEquals(LocalDate date);

    // Trova tutti i viaggi con data di partenza compresa tra due date specificate non cancellati.
    @Query("""
           select t from Trip t
           where type(t.state) not in (
                         com.lucamoretti.adventure_together.model.trip.state.Cancelled
                     )
           and t.dateDeparture > :today
           order by t.dateDeparture asc
           """)
    List<Trip> findByDateDepartureBetweenNotCancelled(LocalDate from, LocalDate to);

    // Trova tutti i viaggi futuri (dopo oggi)
    @Query("""
           select t from Trip t
           where t.dateDeparture > :today
           order by t.dateDeparture asc
           """)
    List<Trip> findFutureTrips(LocalDate today);

    // Trova tutti i viaggi che sono ancora aperti per le prenotazioni (stati ToBeConfirmed e ConfirmedOpen)
    @Query("""
           select t from Trip t
           where type(t.state) in (
                         com.lucamoretti.adventure_together.model.trip.state.ToBeConfirmed,
                         com.lucamoretti.adventure_together.model.trip.state.ConfirmedOpen
                     )
           order by t.dateDeparture asc
           """)
    List<Trip> findOpenForBooking();

    // Trova tutti i viaggi che sono ancora ToBeConfirmed
    @Query("""
           select t from Trip t
           where type(t.state) in (
                         com.lucamoretti.adventure_together.model.trip.state.ToBeConfirmed
                     )
           order by t.dateDeparture asc
           """)
    List<Trip> findAllToBeConfirmed();

    // Trova tutti i viaggi che sono ancora aperti per le prenotazioni (stati ToBeConfirmed e ConfirmedOpen) per uno specifico itinerario di viaggio
    @Query("""
           select t from Trip t
           where t.tripItinerary.id = :itineraryId
           and type(t.state) in (
                         com.lucamoretti.adventure_together.model.trip.state.ToBeConfirmed,
                         com.lucamoretti.adventure_together.model.trip.state.ConfirmedOpen
                     )
           order by t.dateDeparture asc
           """)
    List<Trip> findOpenForBookingByItinerary(Long itineraryId);

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

    // Trova tutti i viaggi ancora prenotabili (stato ToBeConfirmed e ConfirmedOpen) che hanno la partenza entro 30 giorni da oggi
    // Per homepage: viaggi prenotabili con partenza a breve (< 30 giorni)
    @Query("""
           select t from Trip t
           where type(t.state) in (
                         com.lucamoretti.adventure_together.model.trip.state.ToBeConfirmed,
                         com.lucamoretti.adventure_together.model.trip.state.ConfirmedOpen
                     )
           and t.dateDeparture between :today and :todayPlus30
           order by t.dateDeparture asc
           """)
    List<Trip> findUpcomingBookableTrips(@Param("today") LocalDate today, @Param("todayPlus30") LocalDate todayPlus30);

    // Trova un viaggio per id e applica un lock pessimista di scrittura
    // Utile per operazioni che modificano lo stato del viaggio in modo concorrente
    // @Lock blocca la riga nel DB durante la transazione, impedendo ad altri thread di leggerla o modificarla fino al commit.
    @Query("select t from Trip t where t.id = :tripId")
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    Optional<Trip> findByIdForUpdate(Long tripId);
}

