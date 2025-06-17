package com.artrithm.backendapi.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CartItem {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "artwork_id", nullable = false)
    private Artwork artwork;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private CartItemType type; // FIXED_PRICE or AUCTION

    @Column(nullable = false)
    private LocalDateTime addedAt;

    @Column(nullable = false)
    private Integer price; // 고정가든 경매든 이곳에 저장


}
