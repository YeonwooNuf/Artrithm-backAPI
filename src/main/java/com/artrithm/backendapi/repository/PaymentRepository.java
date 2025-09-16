package com.artrithm.backendapi.repository;

import com.artrithm.backendapi.model.Payment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface PaymentRepository extends JpaRepository<Payment, Long> {
    boolean existsByCartOrderId(Long cartOrderId);
    Optional<Payment> findByPaymentId(String paymentId);
    List<Payment> findByPaidAtBetween(LocalDateTime start, LocalDateTime end);

    @Query("SELECT COUNT(DISTINCT ci.artwork.user.id) " +
            "FROM Payment p " +
            "JOIN p.cartOrder co " +
            "JOIN co.items ci " +
            "WHERE p.paidAt BETWEEN :start AND :end")
    long countDistinctSellerIdsByPaidAtBetween(@Param("start") LocalDateTime start,
                                               @Param("end") LocalDateTime end);
}
