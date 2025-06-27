package com.artrithm.backendapi.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Map;

@Getter
@AllArgsConstructor
public class AdminRevenueDto {
    private int artworkRevenue;           // 작품 거래 매출
    private int subscriptionRevenue;      // 구독 서비스 매출
    private int totalRevenue;             // 총 매출
    private int totalCommission;          // 전시관 수수료 수익
    private int payoutAmount;             // 작가에게 정산된 금액

    private int soldArtworkCount;         // 판매된 작품 수
    private int auctionSoldCount;         // 경매 방식 판매 수
    private int fixedPriceSoldCount;      // 지정가 판매 수

    private int currentSubscriberCount;   // 현재 구독 중인 유저 수
    private long activeArtistCount;       // 결제 기록 있는 작가 수
    private Map<String, Long> tierBreakdown; // 티어별 구독자 수
}
