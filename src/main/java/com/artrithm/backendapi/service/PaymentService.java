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
    private final AuctionRepository auctionRepository;
    private final UserRepository userRepository;

    @Transactional
    public PaymentDto processPayment(PaymentRequestDto dto) {
        PaymentTargetType type = dto.getPaymentType();

        return switch (type) {
            case SUBSCRIPTION -> handleSubscriptionPayment(dto);
            case FIXED_ORDER, AUCTION_ORDER -> handleCartOrderPayment(dto);
            case PENALTY -> handlePenaltyPayment(dto);
        };
    }

    private PaymentDto handleSubscriptionPayment(PaymentRequestDto dto) {
        User user = userRepository.findById(dto.getUserId())
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        UserSubscription subscription = subscriptionPaymentService.processSubscriptionPayment(
                dto.getUserId(),
                dto.getTierId(),
                dto.getIsYearly() != null && dto.getIsYearly(),
                dto.getPaymentId()
        );

        Payment payment = Payment.builder()
                .user(user)
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

            PaymentTargetType targetType = dto.getPaymentType();

            // ✅ 반드시 user 설정
            User user = cartOrder.getUser();

            Payment payment = Payment.builder()
                    .user(user)
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

                // 👇 고정가 구매 처리
                fixedPriceSaleRepository.findByArtworkId(artwork.getId())
                        .ifPresent(sale -> {
                            if (sale.getBuyer() == null) {
                                sale.setBuyer(user);
                            }
                            sale.setPayment(savedPayment);
                            fixedPriceSaleRepository.save(sale);
                        });

                // ✅ 경매 낙찰 작품이면 auction도 업데이트
                if (item.getAuction() != null) {
                    Auction auction = item.getAuction();
                    auction.setPayment(savedPayment);
                    auctionRepository.save(auction);
                }
            }

            return PaymentDto.fromEntity(savedPayment);

        } catch (Exception e) {
            handlePaymentFailure(cartOrder);
            throw new RuntimeException("결제 처리 중 오류가 발생했습니다. 주문이 취소되었습니다.", e);
        }
    }

    private PaymentDto handlePenaltyPayment(PaymentRequestDto dto) {
        Auction auction = auctionRepository.findById(dto.getAuctionId())
                .orElseThrow(() -> new IllegalArgumentException("경매 정보를 찾을 수 없습니다."));

        // winnerUserId → Long으로 변환 후 조회
        if (auction.getWinnerUserId() == null)
            throw new IllegalStateException("낙찰자 정보가 없습니다.");

        Long userId = Long.parseLong(auction.getWinnerUserId());
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalStateException("낙찰자를 찾을 수 없습니다."));

        Payment penaltyPayment = Payment.builder()
                .user(user)
                .cartOrder(null)
                .subscription(null)
                .targetType(PaymentTargetType.PENALTY)
                .totalAmount(dto.getTotalAmount())
                .paymentMethod(dto.getPaymentMethod())
                .paymentId(dto.getPaymentId())
                .paidAt(LocalDateTime.now())
                .build();

        Payment saved = paymentRepository.save(penaltyPayment);

        auction.setPayment(saved);
        auctionRepository.save(auction);

        return PaymentDto.fromEntity(saved);
    }

    private void handlePaymentFailure(CartOrder cartOrder) {
        cartOrderItemRepository.deleteByCartOrder(cartOrder);
        cartOrderRepository.delete(cartOrder);
    }
}
