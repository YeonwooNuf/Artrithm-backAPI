package com.artrithm.backendapi.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class FixedPriceArtworkDto {
    private Long artworkId;
    private String artworkTitle;
    private String artworkImageUrl;
    private String description;
    private Integer price;
    private Long exhibitionId;

    private Long sellerUserId;  //
    private Long buyerUserId;   // (nullable)
}
