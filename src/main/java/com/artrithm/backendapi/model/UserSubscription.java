package com.artrithm.backendapi.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserSubscription {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 어떤 사용자(작가)가 구독했는지
    @ManyToOne(optional = false)
    @JoinColumn(name = "user_id")
    private User user;

    // 어떤 요금제인지
    @ManyToOne(optional = false)
    @JoinColumn(name = "tier_id")
    private SubscriptionTier tier;

    // 구독 시작 시점
    private LocalDateTime startDate;

    // 구독 만료 시점
    private LocalDateTime endDate;

    // 현재 유효한 구독인지 여부 (true인 것 1개만 존재해야 함)
    @Column(nullable = false)
    private Boolean isActive;

    // 연간 구독 여부 (true = 연간 / false = 월간)
    @Column(nullable = false)
    private Boolean isYearly;

    // 실 결제 금액
    @Column(nullable = false)
    private Integer paidAmount;

    // 포트원 외부 결제 ID
    private String paymentId;

    @OneToOne(mappedBy = "subscription")
    private Payment payment;
}
