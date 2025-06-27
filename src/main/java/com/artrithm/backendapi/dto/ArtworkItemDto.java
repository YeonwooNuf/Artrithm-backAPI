package com.artrithm.backendapi.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ArtworkItemDto {

    private Long artworkId;
    private String artworkTitle;
    private String artworkImageUrl;

    private String sellerNickname;
    private String buyerNickname;
    private Long buyerId;

    private Integer price;
    private String purchaseType;  // 예: "즉시 구매", "경매 낙찰"

    private Float commissionRate;
    private Integer commissionAmount;
    private Integer payoutAmount;
}
