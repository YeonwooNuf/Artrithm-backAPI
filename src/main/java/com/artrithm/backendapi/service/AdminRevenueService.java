package com.artrithm.backendapi.service;

import com.artrithm.backendapi.dto.AdminRevenueDto;
import com.artrithm.backendapi.model.CartItem;
import com.artrithm.backendapi.model.Payment;
import com.artrithm.backendapi.model.PaymentTargetType;
import com.artrithm.backendapi.model.UserSubscription;
import com.artrithm.backendapi.repository.CartItemRepository;
import com.artrithm.backendapi.repository.PaymentRepository;
import com.artrithm.backendapi.repository.UserSubscriptionRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AdminRevenueService {

    private final PaymentRepository paymentRepository;
    private final UserSubscriptionRepository userSubscriptionRepository;

    @Transactional
    public AdminRevenueDto getRevenueStatistics(LocalDate start, LocalDate end) {

        LocalDateTime startDateTime = start != null ? start.atStartOfDay() : LocalDate.of(2000, 1, 1).atStartOfDay();
        LocalDateTime endDateTime = end != null ? end.atTime(23, 59, 59) : LocalDate.now().atTime(23, 59, 59);

        List<Payment> payments = paymentRepository.findByPaidAtBetween(startDateTime, endDateTime);

        int artworkRevenue = 0;
        int subscriptionRevenue = 0;
        int totalCommission = 0;
        int payoutAmount = 0;

        int soldArtworkCount = 0;
        int auctionSoldCount = 0;
        int fixedPriceSoldCount = 0;

        for (Payment payment : payments) {
            if (payment.getTargetType() == PaymentTargetType.SUBSCRIPTION) {
                subscriptionRevenue += payment.getTotalAmount();
            } else if (payment.getTargetType() == PaymentTargetType.FIXED_ORDER
                    || payment.getTargetType() == PaymentTargetType.AUCTION_ORDER) {

                int amount = payment.getTotalAmount();
                artworkRevenue += amount;
                soldArtworkCount++;

                if (payment.getTargetType() == PaymentTargetType.AUCTION_ORDER) auctionSoldCount++;
                if (payment.getTargetType() == PaymentTargetType.FIXED_ORDER) fixedPriceSoldCount++;

                float rate = 0.1f; // 기본 수수료율

                if (payment.getCartOrder() != null && !payment.getCartOrder().getItems().isEmpty()) {
                    var firstItem = payment.getCartOrder().getItems().get(0);
                    var seller = firstItem.getArtwork().getUser();

                    if (seller != null && seller.getSubscriptions() != null) {
                        rate = seller.getSubscriptions().stream()
                                .filter(UserSubscription::getIsActive)
                                .map(s -> s.getTier().getCommissionRate())
                                .findFirst()
                                .orElse(rate);
                    }
                }

                int commission = Math.round(amount * rate);
                totalCommission += commission;
                payoutAmount += (amount - commission);
            }
        }

        int totalRevenue = artworkRevenue + subscriptionRevenue;

        // 현재 구독자 수
        long currentSubscriberCount = userSubscriptionRepository.countByIsActive(true);

        // 티어별 구독자 수
        Map<String, Long> tierBreakdown = userSubscriptionRepository.findAll().stream()
                .filter(UserSubscription::getIsActive)
                .collect(Collectors.groupingBy(
                        us -> us.getTier().getName(),
                        Collectors.counting()
                ));

        // 활동 작가 수
        long activeArtistCount = paymentRepository.countDistinctSellerIdsByPaidAtBetween(startDateTime, endDateTime);

        return new AdminRevenueDto(
                artworkRevenue,
                subscriptionRevenue,
                totalRevenue,
                totalCommission,
                payoutAmount,
                soldArtworkCount,
                auctionSoldCount,
                fixedPriceSoldCount,
                (int) currentSubscriberCount,
                activeArtistCount,
                tierBreakdown
        );
    }
}
