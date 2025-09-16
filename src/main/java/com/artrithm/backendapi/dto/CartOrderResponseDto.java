package com.artrithm.backendapi.dto;

import com.artrithm.backendapi.model.CartItemType;
import com.artrithm.backendapi.model.PaymentTargetType;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CartOrderResponseDto {
    private Long orderId;
    private Long userId;
    private List<CartOrderItemDto> items;
    private Integer totalAmount;
    private LocalDateTime orderedAt;
    private CartItemType type; // FIXED_PRICE or AUCTION
}