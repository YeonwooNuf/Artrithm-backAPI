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
public class Payment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    // 어떤 결제인지 (구독 / 장바구니 / 경매)
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private PaymentTargetType targetType;

    @OneToOne(optional = true)
    @JoinColumn(name = "cart_order_id", nullable = true) // 💡 null 가능하도록 수정
    private CartOrder cartOrder;

    @OneToOne
    @JoinColumn(name = "subscription_id", nullable = true)
    private UserSubscription subscription;

    // 결제 총 금액
    @Column(nullable = false)
    private Integer totalAmount;

    // 결제 방식: "CARD", "KAKAOPAY" 등 (확장성 고려)
    @Column(nullable = false, length = 20)
    private String paymentMethod;

    // 외부 결제사로부터 받은 고유 결제 ID (포트원 등)
    @Column(nullable = false, unique = true)
    private String paymentId;

    // 결제 시각
    @Column(nullable = false)
    private LocalDateTime paidAt;

    @Column(nullable = false)
    private float commissionRate; // 결제 시점의 수수료율
}
