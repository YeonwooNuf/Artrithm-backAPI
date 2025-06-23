package com.artrithm.backendapi.dto;

import com.artrithm.backendapi.model.Payment;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class PaymentDto {

    private Long id;
    private Long cartOrderId;      // ✅ cartOrder가 없는 경우 null로 설정
    private Long subscriptionId;   // ✅ 구독 결제 시 subscriptionId 포함
    private Integer totalAmount;
    private String paymentMethod;
    private String paymentId;
    private LocalDateTime paidAt;

    public static PaymentDto fromEntity(Payment payment) {
        return PaymentDto.builder()
                .id(payment.getId())
                .cartOrderId(payment.getCartOrder() != null ? payment.getCartOrder().getId() : null)
                .subscriptionId(payment.getSubscription() != null ? payment.getSubscription().getId() : null)
                .totalAmount(payment.getTotalAmount())
                .paymentMethod(payment.getPaymentMethod())
                .paymentId(payment.getPaymentId())
                .paidAt(payment.getPaidAt())
                .build();
    }
}
