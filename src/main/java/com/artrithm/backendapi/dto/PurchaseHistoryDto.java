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
    private LocalDateTime purchasedAt;
    private PaymentTargetType method; // "AUCTION" 또는 "FIXED"
    private String paymentId;         // ✅ 영수증 상세 페이지 이동용
}
