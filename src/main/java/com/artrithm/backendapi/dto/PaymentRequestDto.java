package com.artrithm.backendapi.dto;

import com.artrithm.backendapi.model.PaymentTargetType;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PaymentRequestDto {

    private PaymentTargetType paymentType; // enum 사용으로 변경
    private Long userId;

    // 공통
    private String paymentMethod;
    private String paymentId;
    private Integer totalAmount;

    // 옵션
    private Long cartOrderId;
    // 단일 작품 구매용 (cartOrderId 없이 처리할 경우 필요)
    private SingleArtworkDto singleArtwork;
    private Long tierId;
    private Boolean isYearly;

    // 위약금 결제용
    private Long auctionId;
}
