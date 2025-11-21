package com.lucamoretti.adventure_together.repository.booking;

import com.lucamoretti.adventure_together.model.booking.Booking;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.List;

/*
 Interfaccia di BookingRepository che estende JpaRepository per la gestione delle operazioni sulle entità Booking.
 Fornisce metodi per trovare le prenotazioni in base all'
 */

@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {

    @Query("SELECT b FROM Booking b WHERE b.traveler.id = :travelerId Order BY b.bookingDate DESC")
    List<Booking> findByTraveler_Id(Long travelerId);

    List<Booking> findByTrip_Id(Long tripId);

}

