package com.artrithm.backendapi.dto;

import com.artrithm.backendapi.model.Artwork;
import com.artrithm.backendapi.model.CartItem;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class CartItemResponseDto {
    private Long cartItemId;
    private String type;
    private Long artworkId;
    private String artworkTitle;
    private String artworkImageUrl;
    private Integer price;

    public static CartItemResponseDto fromEntity(CartItem cartItem) {
        Artwork artwork = cartItem.getArtwork();

        return CartItemResponseDto.builder()
                .cartItemId(cartItem.getId())
                .type(cartItem.getType().name())
                .artworkId(artwork.getId())
                .artworkTitle(artwork.getTitle())
                .artworkImageUrl(artwork.getImageUrl()) // 필요한 필드만
                .price(cartItem.getPrice())
                .build();
    }
}
