package com.artrithm.backendapi.repository;

import com.artrithm.backendapi.model.CartOrder;
import com.artrithm.backendapi.model.CartOrderItem;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CartOrderItemRepository extends JpaRepository<CartOrderItem, Long> {
    List<CartOrderItem> findByCartOrderId(Long cartOrderId);
    void deleteByCartOrder(CartOrder cartOrder);
}
