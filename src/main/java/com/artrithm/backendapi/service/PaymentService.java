package com.artrithm.backendapi.service;

import com.artrithm.backendapi.dto.PaymentDto;
import com.artrithm.backendapi.dto.PaymentReceiptDto;
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
                .commissionRate(0)
                .build();

        return PaymentDto.fromEntity(paymentRepository.save(payment));
    }

    private PaymentDto handleCartOrderPayment(PaymentRequestDto dto) {
        CartOrder cartOrder = cartOrderRepository.findById(dto.getCartOrderId())
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 주문입니다."));

        try {
            List<Long> cartItemIdsToDelete = cartOrder.getItems().stream()
                    .map(CartOrderItem::getCartItemId)
                    .filter(id -> id != null)
                    .toList();

            if (!cartItemIdsToDelete.isEmpty()) {
                cartItemRepository.deleteAllByIdIn(cartItemIdsToDelete);
            }

            User user = cartOrder.getUser();

            float rate = 0.1f;
            if (!cartOrder.getItems().isEmpty()) {
                Artwork firstArtwork = cartOrder.getItems().get(0).getArtwork();
                User seller = firstArtwork.getUser();
                rate = seller.getSubscriptions().stream()
                        .filter(UserSubscription::getIsActive)
                        .map(sub -> sub.getTier().getCommissionRate())
                        .findFirst()
                        .orElse(0.1f);
            }

            Payment payment = Payment.builder()
                    .user(user)
                    .cartOrder(cartOrder)
                    .targetType(dto.getPaymentType())
                    .totalAmount(dto.getTotalAmount())
                    .paymentMethod(dto.getPaymentMethod())
                    .paymentId(dto.getPaymentId())
                    .paidAt(LocalDateTime.now())
                    .commissionRate(rate)
                    .build();

            Payment savedPayment = paymentRepository.save(payment);

            for (CartOrderItem item : cartOrder.getItems()) {
                Artwork artwork = artworkRepository.findById(item.getArtwork().getId())
                        .orElseThrow(() -> new IllegalArgumentException("작품을 찾을 수 없습니다."));
                artwork.setSaleStatus(SaleStatus.SOLD);
                artworkRepository.save(artwork);

                fixedPriceSaleRepository.findByArtworkId(artwork.getId())
                        .ifPresent(sale -> {
                            if (sale.getBuyer() == null) {
                                sale.setBuyer(user);
                            }
                            sale.setPayment(savedPayment);
                            fixedPriceSaleRepository.save(sale);
                        });

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
                .commissionRate(0)
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

    @Transactional
    public PaymentReceiptDto getPaymentReceipt(String paymentId) {
        Payment payment = paymentRepository.findByPaymentId(paymentId)
                .orElseThrow(() -> new IllegalArgumentException("❌ 결제 정보를 찾을 수 없습니다: " + paymentId));

        PaymentReceiptDto baseDto = PaymentReceiptDto.from(payment);

        if (payment.getTargetType() == PaymentTargetType.FIXED_ORDER || payment.getTargetType() == PaymentTargetType.AUCTION_ORDER) {
            List<CartOrderItem> items = payment.getCartOrder().getItems();
            if (!items.isEmpty()) {
                Float commissionRate = payment.getCommissionRate() != 0 ? payment.getCommissionRate() : 0.1f;
                int commissionAmount = Math.round(baseDto.getTotalAmount() * commissionRate);
                int payoutAmount = baseDto.getTotalAmount() - commissionAmount;

                baseDto.setCommissionRate(commissionRate);
                baseDto.setCommissionAmount(commissionAmount);
                baseDto.setPayoutAmount(payoutAmount);
            }
        }

        return baseDto;
    }
}
