package com.lucamoretti.adventure_together.controller.advice;

import com.lucamoretti.adventure_together.dto.trip.TripItineraryDTO;
import com.lucamoretti.adventure_together.service.trip.TripItineraryService;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

import java.util.List;

@ControllerAdvice
public class GlobalModelAttributes {

    private final TripItineraryService tripItineraryService;

    public GlobalModelAttributes(TripItineraryService service) {
        this.tripItineraryService = service;
    }

    @ModelAttribute("itineraries")
    public List<TripItineraryDTO> populateItineraries() {
        return tripItineraryService.getAll();
    }
}

