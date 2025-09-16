package com.artrithm.backendapi.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class SaleHistoryDto {
    private Long artworkId;
    private String artworkTitle;
    private String artworkImageUrl;
    private Integer price;
    private String buyerNickname;
    private LocalDateTime soldAt; // ISO or "yyyy-MM-dd HH:mm" 등
    private String status; // ✅ "판매중", "결제대기", "판매완료" 등 상태 추가
    private String paymentId;  // ✅ 결제 상세 페이지로 이동하기 위한 ID
}
