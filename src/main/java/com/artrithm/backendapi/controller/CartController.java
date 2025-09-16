package com.artrithm.backendapi.controller;

import com.artrithm.backendapi.dto.CartAddRequestDto;
import com.artrithm.backendapi.dto.CartItemResponseDto;
import com.artrithm.backendapi.model.CartItem;
import com.artrithm.backendapi.service.CartService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/cart")
@RequiredArgsConstructor
public class CartController {

    private final CartService cartService;

    /**
     * 장바구니에 작품 추가
     */
    @PostMapping
    public ResponseEntity<?> addToCart(@RequestBody CartAddRequestDto request) {
        try {
            cartService.addToCart(
                    request.getUserId(),
                    request.getArtworkId(),
                    request.getType(),
                    request.getAuctionId(),
                    request.getFixedPriceSaleId()
            );
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/{userId}")
    public List<CartItemResponseDto> getUserCart(@PathVariable Long userId) {
        return cartService.getUserCart(userId).stream().map(CartItemResponseDto::fromEntity).collect(Collectors.toList());
    }

    //장바구니에서 해당 아이템 삭제
    @DeleteMapping("/delete/{cartItemId}")
    public ResponseEntity<Void> deleteCartItem(@PathVariable Long cartItemId){
        cartService.deleteCartItem(cartItemId);
        return ResponseEntity.noContent().build();
    }
}
