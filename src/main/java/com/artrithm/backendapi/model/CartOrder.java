package com.artrithm.backendapi.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CartOrder {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "user_id")
    private User user;

    @Column(nullable = false)
    private LocalDateTime orderedAt;

    @OneToMany(mappedBy = "cartOrder", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<CartOrderItem> items;

    @Column(nullable = false)
    private Integer totalAmount;

    // 주문 상태 등을 나중에 추가 가능 (예: COMPLETED, CANCELED 등)
}
