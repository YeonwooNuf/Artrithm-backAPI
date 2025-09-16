package com.artrithm.backendapi.controller;

import com.artrithm.backendapi.dto.CartOrderDto;
import com.artrithm.backendapi.dto.CartOrderRequestDto;
import com.artrithm.backendapi.dto.CartOrderResponseDto;
import com.artrithm.backendapi.dto.SingleOrderRequestDto;
import com.artrithm.backendapi.service.CartOrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/cart/orders")
@RequiredArgsConstructor
public class CartOrderController {

    private final CartOrderService cartOrderService;

    // ✅ 장바구니 주문 생성
    @PostMapping("/create")
    public ResponseEntity<CartOrderResponseDto> createCartOrder(
            @RequestParam("userId") Long userId,
            @RequestBody CartOrderRequestDto requestDto
    ) {
        CartOrderResponseDto order = cartOrderService.createCartOrder(userId, requestDto);
        return ResponseEntity.ok(order);
    }

    // ✅ 주문 상세 조회
    @GetMapping("/{orderId}")
    public ResponseEntity<CartOrderDto> getOrderDetail(@PathVariable Long orderId) {
        // Service에 getCartOrder 메서드가 있다면 사용
        // 없으면 Repository에서 직접 조회하거나 추가 구현 필요
        return ResponseEntity.notFound().build();
    }

    @PostMapping("/single")
    public ResponseEntity<CartOrderResponseDto> createSingleOrder(
            @RequestBody SingleOrderRequestDto dto
    ) {
        CartOrderResponseDto response = cartOrderService.createSingleOrder(dto);
        return ResponseEntity.ok(response);
    }
}
