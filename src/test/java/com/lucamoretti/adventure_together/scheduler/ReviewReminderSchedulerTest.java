package com.lucamoretti.adventure_together.scheduler;

import com.lucamoretti.adventure_together.service.review.ReviewService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.Mockito.verify;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

@ExtendWith(MockitoExtension.class)
class ReviewReminderSchedulerTest {

    @Mock
    private ReviewService reviewService;

    @InjectMocks
    private ReviewReminderScheduler scheduler;

    @Test
    void sendReviewReminders_callsReviewService() {
        assertDoesNotThrow(() -> scheduler.sendReviewReminders());
        verify(reviewService).sendReviewReminderEmails();
    }
}


