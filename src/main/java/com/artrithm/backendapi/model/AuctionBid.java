package com.artrithm.backendapi.model;

import com.artrithm.backendapi.model.Auction;
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
    @Column(name = "auction_id")
    private Long id;  // 실제 PK

    @OneToOne(fetch = FetchType.LAZY)
    @MapsId // 이 필드가 위의 id와 매핑됨
    @JoinColumn(name = "auction_id")
    private Auction auction;

    private String top1UserId;
    private int top1Price;
    private String top2UserId;
    private int top2Price;
    private String top3UserId;
    private int top3Price;
}
