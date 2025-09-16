package com.artrithm.backendapi.controller;

import com.artrithm.backendapi.dto.SubscriptionTierDto;
import com.artrithm.backendapi.dto.UserSubscriptionDto;
import com.artrithm.backendapi.model.SubscriptionTier;
import com.artrithm.backendapi.model.User;
import com.artrithm.backendapi.model.UserSubscription;
import com.artrithm.backendapi.service.SubscriptionService;
import com.artrithm.backendapi.service.UserService; // 유저 조회용
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/subscription")
@RequiredArgsConstructor
public class SubscriptionController {

    private final SubscriptionService subscriptionService;
    private final UserService userService;

    // ✅ 1. 요금제 목록 조회
    @GetMapping("/tiers")
    public ResponseEntity<List<SubscriptionTierDto>> getAllTiers() {
        List<SubscriptionTier> tiers = subscriptionService.getAllTiers();
        List<SubscriptionTierDto> result = tiers.stream().map(tier ->
                SubscriptionTierDto.builder()
                        .id(tier.getId())
                        .name(tier.getName())
                        .priceMonthly(tier.getPriceMonthly())
                        .benefits(tier.getBenefits())
                        .maxExhibitions(tier.getMaxExhibitions())
                        .commissionRate(tier.getCommissionRate())
                        .build()
        ).collect(Collectors.toList());

        return ResponseEntity.ok(result);
    }

    // ✅ 2. 현재 내 구독 정보 조회
    @GetMapping("/me")
    public ResponseEntity<UserSubscriptionDto> getMySubscription(@RequestParam Long userId) {
        User user = userService.findById(userId);

        return subscriptionService.getUserSubscription(user)
                .map(sub -> UserSubscriptionDto.builder()
                        .id(sub.getId())
                        .tierId(sub.getTier().getId())
                        .tierName(sub.getTier().getName())
                        .priceMonthly(sub.getTier().getPriceMonthly())
                        .commissionRate(sub.getTier().getCommissionRate())
                        .startDate(sub.getStartDate())
                        .endDate(sub.getEndDate())
                        .isActive(sub.getIsActive())
                        .isYearly(sub.getIsYearly())
                        .build())
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.noContent().build()); // FREE 플랜이면 구독 없음
    }

    // ✅ 3. 구독 신청 (프론트에서 tierId, isYearly 보내줌)
    @PostMapping("/subscribe")
    public ResponseEntity<String> subscribe(
            @RequestParam Long userId,
            @RequestParam Long tierId,
            @RequestParam(defaultValue = "false") boolean isYearly
    ) {
        User user = userService.findById(userId);
        subscriptionService.subscribe(user, tierId, isYearly);
        return ResponseEntity.ok("구독이 성공적으로 적용되었습니다.");
    }
}
