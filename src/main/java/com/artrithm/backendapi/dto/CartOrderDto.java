package com.artrithm.backendapi.dto;

import com.artrithm.backendapi.model.CartOrder;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Builder
public class CartOrderDto {
    private Long id;
    private Long userId;
    private LocalDateTime orderedAt;
    private List<CartOrderItemDto> items;
    private Integer totalAmount;

    public static CartOrderDto fromEntity(CartOrder order, List<CartOrderItemDto> itemDtos) {
        return CartOrderDto.builder()
                .id(order.getId())
                .userId(order.getUser().getId())
                .orderedAt(order.getOrderedAt())
                .items(itemDtos)
                .totalAmount(order.getTotalAmount())
                .build();
    }
}
