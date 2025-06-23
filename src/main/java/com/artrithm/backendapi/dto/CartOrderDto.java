package com.artrithm.backendapi.dto;

import com.artrithm.backendapi.model.CartOrder;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@Builder
public class CartOrderDto {

    private Long id;
    private Long userId;
    private LocalDateTime orderedAt;
    private List<CartOrderItemDto> items;

    public static CartOrderDto fromEntity(CartOrder order) {
        return CartOrderDto.builder()
                .id(order.getId())
                .userId(order.getUser().getId())
                .orderedAt(order.getOrderedAt())
                .items(order.getItems().stream()
                        .map(CartOrderItemDto::fromEntity)
                        .collect(Collectors.toList()))
                .build();
    }
}
