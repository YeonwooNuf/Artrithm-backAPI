package com.artrithm.backendapi.dto;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AuctionBidDto {
    private Long auctionId;

    private String top1UserId;
    private int top1Price;
    private String top2UserId;
    private int top2Price;
    private String top3UserId;
    private int top3Price;
}
