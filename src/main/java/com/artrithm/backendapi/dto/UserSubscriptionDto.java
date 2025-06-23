package com.artrithm.backendapi.dto;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserSubscriptionDto {

    private Long id;

    private Long tierId;
    private String tierName;
    private Integer priceMonthly;
    private Float commissionRate;

    private LocalDateTime startDate;
    private LocalDateTime endDate;

    private Boolean isActive;
    private Boolean isYearly;

    private Integer paidAmount;
    private String paymentId;
}
