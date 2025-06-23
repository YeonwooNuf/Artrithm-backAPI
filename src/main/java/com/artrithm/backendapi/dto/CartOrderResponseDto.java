package com.artrithm.backendapi.dto;

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
    private Integer totalPrice;
    private LocalDateTime orderedAt;
}
