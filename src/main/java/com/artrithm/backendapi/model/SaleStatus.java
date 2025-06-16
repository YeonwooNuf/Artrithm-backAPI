package com.artrithm.backendapi.model;

public enum SaleStatus {
    UNSOLD,   // 아직 팔리지 않음 (판매 중)
    PENDING,  // 누군가 구매 신청했지만 결제는 아직
    SOLD      // 결제 완료된 상태
}
