package com.artrithm.backendapi.dto;

import com.artrithm.backendapi.model.*;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

import static java.util.stream.Collectors.toList;

@Getter
@Setter
@Builder
public class PaymentReceiptDto {

    private String paymentId;
    private String paymentMethod;
    private int totalAmount;
    private LocalDateTime paidAt;

    private List<ArtworkItemDto> items;

    private Float commissionRate;      // 💡 대표 수수료율 (첫 작품 기준)
    private Integer commissionAmount;  // 💡 총 수수료
    private Integer payoutAmount;      // 💡 총 정산 금액

    public static PaymentReceiptDto from(Payment payment) {
        CartOrder order = payment.getCartOrder();
        User buyer = order.getUser();

        List<ArtworkItemDto> itemDtos = order.getItems().stream().map(item -> {
            Artwork artwork = item.getArtwork();
            User seller = artwork.getUser();
            String purchaseType = item.getType() == CartItemType.AUCTION ? "경매 낙찰" : "즉시 구매";

            String sellerNickname = seller != null ? seller.getNickname() : "알 수 없음";
            String buyerNickname = buyer != null ? buyer.getNickname() : "알 수 없음";
            Long buyerId = buyer != null ? buyer.getId() : null;

            // 💡 개별 작가 수수료율 계산
            float rate = 0.1f; // 기본 수수료 10%
            if (seller != null && seller.getSubscriptions() != null) {
                rate = seller.getSubscriptions().stream()
                        .filter(UserSubscription::getIsActive)
                        .map(s -> s.getTier().getCommissionRate())
                        .findFirst()
                        .orElse(rate);
            }

            int price = item.getPrice();
            int commission = Math.round(price * rate);
            int payout = price - commission;

            return ArtworkItemDto.builder()
                    .artworkId(artwork.getId())
                    .artworkTitle(artwork.getTitle())
                    .artworkImageUrl(artwork.getImageUrl())
                    .sellerNickname(sellerNickname)
                    .buyerNickname(buyerNickname)
                    .buyerId(buyerId)
                    .price(price)
                    .purchaseType(purchaseType)
                    .commissionRate(rate)
                    .commissionAmount(commission)
                    .payoutAmount(payout)
                    .build();
        }).collect(toList());

        // 💰 총 수수료 및 정산 금액 계산
        int totalCommission = itemDtos.stream().mapToInt(ArtworkItemDto::getCommissionAmount).sum();
        int totalPayout = itemDtos.stream().mapToInt(ArtworkItemDto::getPayoutAmount).sum();

        // 📌 대표 수수료율은 첫 작품 기준
        Float representativeRate = !itemDtos.isEmpty() ? itemDtos.get(0).getCommissionRate() : 0.1f;

        return PaymentReceiptDto.builder()
                .paymentId(payment.getPaymentId())
                .paymentMethod(payment.getPaymentMethod())
                .totalAmount(payment.getTotalAmount())
                .paidAt(payment.getPaidAt())
                .items(itemDtos)
                .commissionRate(representativeRate)
                .commissionAmount(totalCommission)
                .payoutAmount(totalPayout)
                .build();
    }
}
