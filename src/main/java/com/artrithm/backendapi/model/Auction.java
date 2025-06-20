package com.artrithm.backendapi.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "auction")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Auction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long auctionId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "artwork_id")
    private Artwork artwork;


    @JoinColumn(name = "nickname")
    private String winnerNickname;

    private LocalDateTime startTime;
    private LocalDateTime endTime;

    private Integer startPrice;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AuctionStatus status;

    private String winnerUserId;
    private Integer finalPrice;

    @Column(name = "payment_deadline")
    private LocalDateTime paymentDeadline;

}
