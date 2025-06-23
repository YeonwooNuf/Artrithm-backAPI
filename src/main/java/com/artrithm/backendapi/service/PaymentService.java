package com.artrithm.backendapi.service;

import com.artrithm.backendapi.dto.PaymentDto;
import com.artrithm.backendapi.dto.PaymentRequestDto;
import com.artrithm.backendapi.model.CartOrder;
import com.artrithm.backendapi.model.Payment;
import com.artrithm.backendapi.model.PaymentTargetType;
import com.artrithm.backendapi.model.UserSubscription;
import com.artrithm.backendapi.repository.CartOrderRepository;
import com.artrithm.backendapi.repository.PaymentRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final CartOrderRepository cartOrderRepository;
    private final SubscriptionPaymentService subscriptionPaymentService;

    @Transactional
    public PaymentDto processPayment(PaymentRequestDto dto) {
        PaymentTargetType type = dto.getPaymentType();

        return switch (type) {
            case SUBSCRIPTION -> handleSubscriptionPayment(dto);
            case FIXED_ORDER, AUCTION_ORDER -> handleCartOrderPayment(dto);
        };
    }

    private PaymentDto handleSubscriptionPayment(PaymentRequestDto dto) {
        UserSubscription subscription = subscriptionPaymentService.processSubscriptionPayment(
                dto.getUserId(),
                dto.getTierId(),
                dto.getIsYearly() != null && dto.getIsYearly(),
                dto.getPaymentId()
        );

        Payment payment = Payment.builder()
                .cartOrder(null)
                .subscription(subscription) // ✅ 연결
                .targetType(PaymentTargetType.SUBSCRIPTION)
                .totalAmount(dto.getTotalAmount())
                .paymentMethod(dto.getPaymentMethod())
                .paymentId(dto.getPaymentId())
                .paidAt(LocalDateTime.now())
                .build();

        return PaymentDto.fromEntity(paymentRepository.save(payment));
    }

    private PaymentDto handleCartOrderPayment(PaymentRequestDto dto) {
        CartOrder cartOrder = cartOrderRepository.findById(dto.getCartOrderId())
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 주문입니다."));

        // FIXED_ORDER 또는 AUCTION_ORDER로 분기 처리
        PaymentTargetType targetType = dto.getPaymentType().equals("AUCTION")
                ? PaymentTargetType.AUCTION_ORDER
                : PaymentTargetType.FIXED_ORDER;

        Payment payment = Payment.builder()
                .cartOrder(cartOrder)
                .targetType(targetType) // ✅ targetType 설정
                .totalAmount(dto.getTotalAmount())
                .paymentMethod(dto.getPaymentMethod())
                .paymentId(dto.getPaymentId())
                .paidAt(LocalDateTime.now())
                .build();

        return PaymentDto.fromEntity(paymentRepository.save(payment));
    }
}
