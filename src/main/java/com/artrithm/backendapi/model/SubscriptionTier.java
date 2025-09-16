package com.artrithm.backendapi.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SubscriptionTier {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String name; // 예: FREE, PREMIUM, PRO

    @Column(nullable = false)
    private Integer priceMonthly; // 월 요금 (₩ 단위)

    @Lob
    private String benefits; // 혜택 설명 (JSON 문자열 or 단순 텍스트)

    @Column(nullable = false)
    private Integer maxExhibitions; // 월간 전시 개설 제한

    @Column(nullable = false)
    private Float commissionRate; // 수수료율 (예: 0.2 = 20%)

    // 👉 필요 시 연간 요금 계산 메서드
    public int getDiscountedYearlyPrice() {
        return (int) Math.round(priceMonthly * 12 * 0.8); // 20% 할인
    }
}
