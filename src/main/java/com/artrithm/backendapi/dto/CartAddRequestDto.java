package com.artrithm.backendapi.dto;

import com.artrithm.backendapi.model.CartItemType;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CartAddRequestDto {
    private Long userId;
    private Long artworkId;
    private CartItemType type;
}
