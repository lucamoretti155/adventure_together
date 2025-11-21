package com.lucamoretti.adventure_together.repository.review;

import com.lucamoretti.adventure_together.model.review.Review;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

/*
 Interfaccia repository per l'entità Review.
 Estende JpaRepository per fornire operazioni CRUD e query personalizzate
 per gestire le recensioni lasciate dai viaggiatori sui viaggi.
*/

@Repository
public interface ReviewRepository extends JpaRepository<Review, Long> {

    boolean existsByTrip_IdAndTraveler_Id(Long tripId, Long travelerId);

    List<Review> findByTraveler_Id(Long travelerId);

    List<Review> findByTrip_Id(Long tripId);

    // Filtro le recensioni in base all'id dell'itinerario del viaggio che è associato al Trip della recensione
    // devo fare una join fra Review e Trip per accedere all'attributo tripItinerary di Trip
    @Query
            ("SELECT r FROM Review r JOIN r.trip t WHERE t.tripItinerary.id = :tripItineraryId")
    List<Review> findAllByTripItinerary_Id(Long tripItineraryId);

    // Trova una recensione in base all'id del viaggio e all'id del viaggiatore
    @Query
            ("SELECT r FROM Review r JOIN r.trip t JOIN r.traveler u WHERE r.trip.id = :tripId AND r.traveler.id = :travelerId")
    Optional<Review> findByTripIdAndTravelerId(Long tripId, Long travelerId);
}
