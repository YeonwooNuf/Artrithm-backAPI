package com.artrithm.backendapi.model;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "fixed_price_sale")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FixedPriceSale {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 작품 1:1
    @OneToOne
    @JoinColumn(name = "artwork_id")
    private Artwork artwork;

    // 판매자
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "seller_user_id")
    private User seller;

    // 구매자 (null이면 아직 구매되지 않음)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "buyer_user_id")
    private User buyer;

    @Column(nullable = false)
    private Integer price;
}
