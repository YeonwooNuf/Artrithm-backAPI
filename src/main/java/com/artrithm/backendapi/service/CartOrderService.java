package com.artrithm.backendapi.service;

import com.artrithm.backendapi.dto.CartOrderItemDto;
import com.artrithm.backendapi.dto.CartOrderRequestDto;
import com.artrithm.backendapi.dto.CartOrderResponseDto;
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
    private final AuctionBidRepository auctionBidRepository;

    @Transactional
    public CartOrderResponseDto createCartOrder(Long userId, CartOrderRequestDto requestDto) {
        if (requestDto.getCartItemIds() == null || requestDto.getCartItemIds().isEmpty()) {
            throw new IllegalArgumentException("장바구니 ID 목록이 비어 있습니다.");
        }

        if (requestDto.getCartItemIds().contains(null)) {
            throw new IllegalArgumentException("장바구니 ID 목록에 null이 포함되어 있습니다.");
        }

        System.out.println(">>> 요청받은 cartItemIds: " + requestDto.getCartItemIds());

        List<CartItem> cartItems = cartItemRepository.findByIdIn(requestDto.getCartItemIds());

        if (cartItems.isEmpty()) {
            throw new IllegalArgumentException("선택된 장바구니 항목이 없습니다.");
        }

        // 주문 생성
        CartOrder cartOrder = CartOrder.builder()
                .user(cartItems.get(0).getUser())
                .orderedAt(LocalDateTime.now())
                .totalAmount(0)
                .build();
        cartOrderRepository.save(cartOrder);

        // 주문 아이템 생성
        List<CartOrderItem> orderItems = cartItems.stream().map(item -> {
            Integer price;
            if (item.getType() == CartItemType.FIXED_PRICE) {
                if (item.getFixedPriceSale() == null) {
                    throw new IllegalStateException("FIXED_PRICE인데 FixedPriceSale 정보가 없습니다.");
                }
                price = item.getFixedPriceSale().getPrice();
            } else {
                if (item.getAuction() == null || item.getAuction().getFinalPrice() == null) {
                    throw new IllegalStateException("AUCTION인데 Auction 정보나 최종 가격이 없습니다.");
                }
                price = item.getAuction().getFinalPrice();
            }

            return CartOrderItem.builder()
                    .cartOrder(cartOrder)
                    .artwork(item.getArtwork())
                    .type(item.getType())
                    .price(price)
                    .auction(item.getAuction())
                    .fixedPriceSale(item.getFixedPriceSale())
                    .cartItemId(item.getId())
                    .build();
        }).collect(Collectors.toList());

        cartOrderItemRepository.saveAll(orderItems);
        cartOrder.setItems(orderItems);

        // 총 주문 금액 계산 및 저장
        int totalAmount = orderItems.stream()
                .mapToInt(CartOrderItem::getPrice)
                .sum();
        cartOrder.setTotalAmount(totalAmount);
        cartOrderRepository.save(cartOrder);

        // 응답용 DTO 생성
        List<CartOrderItemDto> itemDtos = orderItems.stream().map(item -> {
            Long top1UserId = null;
            if (item.getType() == CartItemType.AUCTION && item.getAuction() != null) {
                top1UserId = auctionBidRepository.findByAuction(item.getAuction())
                        .map(AuctionBid::getTop1UserId)
                        .map(Long::valueOf)
                        .orElse(null);
            }
            return CartOrderItemDto.fromEntity(item, top1UserId);
        }).collect(Collectors.toList());

        CartItemType type = orderItems.get(0).getType();

        return CartOrderResponseDto.builder()
                .orderId(cartOrder.getId())
                .userId(cartOrder.getUser().getId())
                .items(itemDtos)
                .totalAmount(totalAmount)
                .orderedAt(cartOrder.getOrderedAt())
                .type(type)
                .build();
    }
}
