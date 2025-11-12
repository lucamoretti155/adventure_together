package com.lucamoretti.adventure_together.repository.payment;

import com.lucamoretti.adventure_together.model.payment.Payment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PaymentRepository extends JpaRepository<Payment, Long> {
}
