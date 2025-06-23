package com.artrithm.backendapi.service;

import com.artrithm.backendapi.model.SubscriptionTier;
import com.artrithm.backendapi.model.User;
import com.artrithm.backendapi.model.UserSubscription;
import com.artrithm.backendapi.repository.SubscriptionTierRepository;
import com.artrithm.backendapi.repository.UserSubscriptionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class SubscriptionService {

    private final SubscriptionTierRepository tierRepository;
    private final UserSubscriptionRepository subscriptionRepository;

    /**
     * 전체 요금제 리스트 조회
     */
    public List<SubscriptionTier> getAllTiers() {
        return tierRepository.findAll();
    }

    /**
     * 현재 사용자 요금제 (FREE 포함)
     */
    public SubscriptionTier getCurrentTier(User user) {
        return subscriptionRepository.findByUserAndIsActiveTrue(user)
                .map(UserSubscription::getTier)
                .orElseGet(() -> tierRepository.findByName("FREE")
                        .orElseThrow(() -> new IllegalStateException("기본 요금제가 없습니다.")));
    }

    /**
     * 현재 유효한 구독 가져오기 (유료 구독만)
     */
    public Optional<UserSubscription> getUserSubscription(User user) {
        return subscriptionRepository.findByUserAndIsActiveTrue(user);
    }

    /**
     * 유료 구독 시작 (기존 구독 비활성화 → 새 구독 생성)
     */
    public void subscribe(User user, Long tierId, boolean isYearly) {
        SubscriptionTier tier = tierRepository.findById(tierId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 요금제입니다."));

        // FREE 요금제는 구독 대상 아님
        if (tier.getName().equalsIgnoreCase("FREE")) {
            throw new IllegalArgumentException("무료 요금제는 구독할 수 없습니다.");
        }

        // 기존 구독 비활성화 처리
        subscriptionRepository.findByUserAndIsActiveTrue(user).ifPresent(oldSub -> {
            oldSub.setIsActive(false);
            subscriptionRepository.save(oldSub);
        });

        LocalDateTime now = LocalDateTime.now();
        LocalDateTime endDate = isYearly ? now.plusYears(1) : now.plusMonths(1);

        UserSubscription newSub = UserSubscription.builder()
                .user(user)
                .tier(tier)
                .startDate(now)
                .endDate(endDate)
                .isYearly(isYearly)
                .isActive(true)
                .build();

        subscriptionRepository.save(newSub);
    }
}
