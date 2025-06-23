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
    private Long tierId;
    private Boolean isYearly;
}
