package com.artrithm.backendapi.dto;

import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FixedPriceSaleDto {
    private Long artworkId;
    private BigDecimal price;
    private Long sellerUserId;
}
