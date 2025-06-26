package com.artrithm.backendapi.dto;

import com.artrithm.backendapi.model.PaymentTargetType;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class PurchaseHistoryDto {
    private Long artworkId;
    private String artworkTitle;
    private String artworkImageUrl;
    private Integer price;
    private String sellerNickname;
    private LocalDateTime purchasedAt; // ISO or "yyyy-MM-dd HH:mm" 등
    private PaymentTargetType method;
}