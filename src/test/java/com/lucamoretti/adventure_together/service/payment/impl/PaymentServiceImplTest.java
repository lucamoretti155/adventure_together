package com.lucamoretti.adventure_together.service.payment.impl;

import com.lucamoretti.adventure_together.dto.payment.PaymentDTO;
import com.lucamoretti.adventure_together.model.payment.Payment;
import com.lucamoretti.adventure_together.repository.payment.PaymentRepository;
import com.lucamoretti.adventure_together.util.exception.ResourceNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PaymentServiceImplTest {

    @Mock
    private PaymentRepository paymentRepository;

    @InjectMocks
    private PaymentServiceImpl paymentService;

    @Test
    void getPaymentById_success() {
        Payment payment = new Payment();
        payment.setId(1L);
        payment.setAmountPaid(100.0);

        when(paymentRepository.findById(1L)).thenReturn(Optional.of(payment));

        PaymentDTO dto = paymentService.getPaymentById(1L);

        assertNotNull(dto);
        assertEquals(1L, dto.getId());
        assertEquals(100.0, dto.getAmountPaid());
    }

    @Test
    void getPaymentById_notFound_throws() {
        when(paymentRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> paymentService.getPaymentById(99L));
    }
}
