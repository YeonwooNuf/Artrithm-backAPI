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
    private Long top1UserId;

    public static CartOrderItemDto fromEntity(CartOrderItem item, Long top1UserId) {
        return CartOrderItemDto.builder()
                .id(item.getId())
                .artworkId(item.getArtwork().getId())
                .artworkTitle(item.getArtwork().getTitle())
                .artworkImageUrl(item.getArtwork().getImageUrl())
                .type(item.getType().name())
                .price(item.getPrice())
                .top1UserId(top1UserId)
                .build();
    }
}
