package com.artrithm.backendapi.dto;

import com.artrithm.backendapi.model.CartOrderItem;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CartOrderItemDto {
    private Long id;
    private Long artworkId;
    private String artworkTitle;
    private String artworkImageUrl;
    private String type; // FIXED_PRICE or AUCTION
    private Integer price;

    public static CartOrderItemDto fromEntity(CartOrderItem item) {
        return CartOrderItemDto.builder()
                .id(item.getId())
                .artworkId(item.getArtwork().getId())
                .artworkTitle(item.getArtwork().getTitle())
                .artworkImageUrl(item.getArtwork().getImageUrl())
                .type(item.getType().name()) // enum → 문자열
                .price(item.getPrice())
                .build();
    }
}
