package com.artrithm.backendapi.repository;

import com.artrithm.backendapi.model.CartOrder;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CartOrderRepository extends JpaRepository<CartOrder, Long> {
    List<CartOrder> findByUserId(Long userId);
}
