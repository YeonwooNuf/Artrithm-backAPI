package com.artrithm.backendapi.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SubscriptionTierDto {

    private Long id;
    private String name;              // FREE, PREMIUM, PRO
    private Integer priceMonthly;     // 월 요금
    private String benefits;          // 혜택 설명 (프론트 표시용)
    private Integer maxExhibitions;   // 전시 개수 제한
    private Float commissionRate;     // 수수료율 (%)
}
