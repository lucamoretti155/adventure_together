package com.lucamoretti.adventure_together.repository.payment;

import com.lucamoretti.adventure_together.model.payment.Payment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PaymentRepository extends JpaRepository<Payment, Long> {
    Optional<Payment> findById(Long paymentId);
}
