package com.artrithm.backendapi.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "auction_bid")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AuctionBid {
    @Id
    private Long auctionId;

    private String top1UserId;
    private int top1Price;
    private String top2UserId;
    private int top2Price;
    private String top3UserId;
    private int top3Price;
}
