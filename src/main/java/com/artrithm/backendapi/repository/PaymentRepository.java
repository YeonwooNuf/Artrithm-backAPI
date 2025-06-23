package com.artrithm.backendapi.repository;

import com.artrithm.backendapi.model.Payment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PaymentRepository extends JpaRepository<Payment, Long> {
    boolean existsByCartOrderId(Long cartOrderId);
}
