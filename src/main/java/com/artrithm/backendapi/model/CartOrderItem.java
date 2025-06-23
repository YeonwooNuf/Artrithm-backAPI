package com.artrithm.backendapi.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CartOrderItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "cart_order_id")
    private CartOrder cartOrder;

    @ManyToOne(optional = false)
    @JoinColumn(name = "artwork_id")
    private Artwork artwork;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private CartItemType type; // FIXED_PRICE or AUCTION

    @ManyToOne
    @JoinColumn(name = "fixed_price_sale_id")
    private FixedPriceSale fixedPriceSale;

    @ManyToOne
    @JoinColumn(name = "auction_id")
    private Auction auction;

    @Column(nullable = false)
    private Integer price; // 실제 결제된 금액
}
