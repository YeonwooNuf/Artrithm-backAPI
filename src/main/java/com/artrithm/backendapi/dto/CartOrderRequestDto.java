package com.artrithm.backendapi.dto;

import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CartOrderRequestDto {
    private List<Long> cartItemIds; // 장바구니 항목 ID 리스트
}
