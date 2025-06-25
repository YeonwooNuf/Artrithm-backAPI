package com.artrithm.backendapi.service;

import com.artrithm.backendapi.dto.PaymentDto;
import com.artrithm.backendapi.dto.PaymentRequestDto;
import com.artrithm.backendapi.model.*;
import com.artrithm.backendapi.repository.*;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final CartOrderRepository cartOrderRepository;
    private final CartOrderItemRepository cartOrderItemRepository;
    private final SubscriptionPaymentService subscriptionPaymentService;
    private final CartItemRepository cartItemRepository;
    private final ArtworkRepository artworkRepository;
    private final FixedPriceSaleRepository fixedPriceSaleRepository;

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
                .subscription(subscription)
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

        try {
            // ✅ cartItem 삭제
            List<Long> cartItemIdsToDelete = cartOrder.getItems().stream()
                    .map(CartOrderItem::getCartItemId)
                    .filter(id -> id != null)
                    .toList();

            if (!cartItemIdsToDelete.isEmpty()) {
                cartItemRepository.deleteAllByIdIn(cartItemIdsToDelete);
            }

            // 결제 유형
            PaymentTargetType targetType = dto.getPaymentType().equals("AUCTION")
                    ? PaymentTargetType.AUCTION_ORDER
                    : PaymentTargetType.FIXED_ORDER;

            // 결제 저장
            Payment payment = Payment.builder()
                    .cartOrder(cartOrder)
                    .targetType(targetType)
                    .totalAmount(dto.getTotalAmount())
                    .paymentMethod(dto.getPaymentMethod())
                    .paymentId(dto.getPaymentId())
                    .paidAt(LocalDateTime.now())
                    .build();

            Payment savedPayment = paymentRepository.save(payment);

            for (CartOrderItem item : cartOrder.getItems()) {
                Artwork artwork = artworkRepository.findById(item.getArtwork().getId())
                        .orElseThrow(() -> new IllegalArgumentException("작품을 찾을 수 없습니다."));

                // 작품 상태 변경
                artwork.setSaleStatus(SaleStatus.SOLD);
                artworkRepository.save(artwork);

                // 🔽 구매자 + 결제 정보 모두 저장
                fixedPriceSaleRepository.findByArtworkId(artwork.getId())
                        .ifPresent(sale -> {
                            if (sale.getBuyer() == null) {
                                sale.setBuyer(cartOrder.getUser());
                            }
                            sale.setPayment(savedPayment); // ✅ 추가된 부분
                            fixedPriceSaleRepository.save(sale);
                        });
            }

            return PaymentDto.fromEntity(savedPayment);

        } catch (Exception e) {
            handlePaymentFailure(cartOrder);
            throw new RuntimeException("결제 처리 중 오류가 발생했습니다. 주문이 취소되었습니다.", e);
        }
    }

    private void handlePaymentFailure(CartOrder cartOrder) {
        cartOrderItemRepository.deleteByCartOrder(cartOrder);
        cartOrderRepository.delete(cartOrder);
    }
}
