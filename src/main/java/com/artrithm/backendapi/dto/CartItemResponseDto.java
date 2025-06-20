package com.artrithm.backendapi.dto;

import com.artrithm.backendapi.model.Artwork;
import com.artrithm.backendapi.model.CartItem;
import com.artrithm.backendapi.model.CartItemType;
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
        Integer price = null;
        if (cartItem.getType() == CartItemType.FIXED_PRICE && cartItem.getFixedPriceSale() != null) {
            price = cartItem.getFixedPriceSale().getPrice().intValue();
        } else if (cartItem.getType() == CartItemType.AUCTION && cartItem.getAuction() != null) {
            price = cartItem.getAuction().getFinalPrice().intValue();
        }


        return CartItemResponseDto.builder()
                .cartItemId(cartItem.getId())
                .type(cartItem.getType().name())
                .artworkId(artwork.getId())
                .artworkTitle(artwork.getTitle())
                .artworkImageUrl(artwork.getImageUrl()) // 필요한 필드만
                .price(price)
                .build();
    }
}
