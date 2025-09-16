package com.artrithm.backendapi.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SingleOrderRequestDto {
    private Long userId;
    private Long artworkId;
    private Long fixedPriceSaleId;
}
