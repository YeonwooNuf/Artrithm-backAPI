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
    private Long id;

    @OneToOne(mappedBy = "auction", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private AuctionBid auctionBid;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "artwork_id")
    private Artwork artwork;

    @Column(name = "nickname")
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

    // ✅ 결제 정보 연관관계 추가
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "payment_id")
    private Payment payment;
}
