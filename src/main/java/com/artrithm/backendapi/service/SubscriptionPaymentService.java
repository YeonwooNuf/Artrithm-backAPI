package com.artrithm.backendapi.service;

import com.artrithm.backendapi.model.*;
import com.artrithm.backendapi.repository.*;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class SubscriptionPaymentService {

    private final UserRepository userRepository;
    private final SubscriptionTierRepository tierRepository;
    private final UserSubscriptionRepository subscriptionRepository;

    @Transactional
    public UserSubscription processSubscriptionPayment(Long userId, Long tierId, boolean isYearly, String paymentId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("사용자가 존재하지 않습니다."));
        SubscriptionTier tier = tierRepository.findById(tierId)
                .orElseThrow(() -> new IllegalArgumentException("해당 요금제가 존재하지 않습니다."));

        subscriptionRepository.deactivateAll(userId);

        int amount = isYearly ? tier.getDiscountedYearlyPrice() : tier.getPriceMonthly();

        UserSubscription newSub = UserSubscription.builder()
                .user(user)
                .tier(tier)
                .isYearly(isYearly)
                .startDate(LocalDateTime.now())
                .endDate(LocalDateTime.now().plusDays(isYearly ? 365 : 30))
                .isActive(true)
                .paidAmount(amount)
                .paymentId(paymentId)
                .build();

        return subscriptionRepository.save(newSub); // 💡 저장 후 반환
    }
}
