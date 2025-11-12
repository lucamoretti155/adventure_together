package com.lucamoretti.adventure_together.service.review;

import com.lucamoretti.adventure_together.dto.review.ReviewDTO;
import java.util.List;

/*
 Interfaccia per il servizio di gestione delle recensioni
 Si occupa della creazione, recupero e gestione delle recensioni
 */

public interface ReviewService {
    ReviewDTO createReview(Long tripId, Long travelerId, String textReview, int score);
    List<ReviewDTO> getTravelerReviews(Long travelerId);
    List<ReviewDTO> getTripReviews(Long tripId);
    List<ReviewDTO> getPendingReviews(Long travelerId);
    void sendReviewReminderEmails();
}
