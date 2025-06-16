package com.artrithm.backendapi.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class FixedPriceArtworkDto {
    private Long artworkId;
    private String artworkTitle;
    private String artworkImageUrl;
    private String description;
    private BigDecimal price;
    private Long exhibitionId; // 추가

}
