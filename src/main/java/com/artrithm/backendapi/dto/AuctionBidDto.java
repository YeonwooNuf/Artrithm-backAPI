package com.artrithm.backendapi.dto;

import com.artrithm.backendapi.model.AuctionBid;
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

    public static AuctionBidDto fromEntity(AuctionBid bid) {
        return AuctionBidDto.builder()
                .auctionId(bid.getAuction().getId())
                .top1UserId(bid.getTop1UserId())
                .top1Price(bid.getTop1Price())
                .top2UserId(bid.getTop2UserId())
                .top2Price(bid.getTop2Price())
                .top3UserId(bid.getTop3UserId())
                .top3Price(bid.getTop3Price())
                .build();
    }
}
