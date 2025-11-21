package com.lucamoretti.adventure_together.service.booking.impl;

import com.lucamoretti.adventure_together.dto.booking.BookingDTO;
import com.lucamoretti.adventure_together.repository.booking.BookingRepository;
import com.lucamoretti.adventure_together.service.booking.BookingService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.*;

/*
    Implementazione del servizio per la visualizzazione delle prenotazioni.
 */

@Service
@RequiredArgsConstructor
@Transactional
public class BookingServiceImpl implements BookingService {

    private final BookingRepository bookingRepository;
    @Value("${app.base-url}")
    private String baseUrl;

    // Recupera una prenotazione per ID
    @Override
    public BookingDTO getBookingById(Long id) {
        return bookingRepository.findById(id)
                .map(BookingDTO::fromEntity)
                .orElseThrow(() -> new IllegalArgumentException("Booking non trovato"));
    }

    // Recupera tutte le prenotazioni di un traveler
    @Override
    public List<BookingDTO> getBookingsByTravelerId(Long travelerId) {
        return bookingRepository.findByTraveler_Id(travelerId)
                .stream()
                .map(BookingDTO::fromEntity)
                .toList();
    }
}
