package com.artrithm.backendapi.service;

import com.artrithm.backendapi.dto.CartOrderRequestDto;
import com.artrithm.backendapi.model.*;
import com.artrithm.backendapi.repository.*;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CartOrderService {

    private final CartItemRepository cartItemRepository;
    private final CartOrderRepository cartOrderRepository;
    private final CartOrderItemRepository cartOrderItemRepository;

    @Transactional
    public CartOrder createCartOrder(Long userId, CartOrderRequestDto requestDto) {
        // 장바구니 항목 가져오기
        List<CartItem> cartItems = cartItemRepository.findByIdIn(requestDto.getCartItemIds());

        if (cartItems.isEmpty()) throw new IllegalArgumentException("선택된 장바구니 항목이 없습니다.");

        // 주문 생성
        CartOrder cartOrder = CartOrder.builder()
                .user(cartItems.get(0).getUser())
                .orderedAt(LocalDateTime.now())
                .build();

        cartOrderRepository.save(cartOrder);

        // CartOrderItem으로 저장
        List<CartOrderItem> orderItems = cartItems.stream().map(item ->
                CartOrderItem.builder()
                        .cartOrder(cartOrder)
                        .artwork(item.getArtwork())
                        .type(item.getType())
                        .price(item.getType() == CartItemType.FIXED_PRICE ?
                                item.getFixedPriceSale().getPrice() :
                                item.getAuction().getFinalPrice())
                        .build()
        ).collect(Collectors.toList());

        cartOrderItemRepository.saveAll(orderItems);

        // 결제 완료 후 장바구니에서 삭제
        cartItemRepository.deleteAllByIdIn(requestDto.getCartItemIds());

        return cartOrder;
    }
}
