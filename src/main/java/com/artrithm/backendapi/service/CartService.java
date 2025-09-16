package com.artrithm.backendapi.service;

import com.artrithm.backendapi.model.*;
import com.artrithm.backendapi.repository.*;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CartService {

    private final CartItemRepository cartItemRepository;
    private final UserRepository userRepository;
    private final ArtworkRepository artworkRepository;
    private final FixedPriceSaleRepository fixedPriceSaleRepository;
    private final AuctionRepository auctionRepository;


    /**
     * 사용자 장바구니 추가
     */
    @Transactional
    public void addToCart(Long userId, Long artworkId, CartItemType type, Long auctionId, Long fixedPriceSaleId) {
        if (cartItemRepository.existsByUserIdAndArtworkId(userId, artworkId)) {
            throw new IllegalStateException("이미 장바구니에 있는 작품입니다.");
        }

        Artwork artwork = artworkRepository.findById(artworkId)
                .orElseThrow(() -> new EntityNotFoundException("작품을 찾을 수 없습니다."));

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("사용자를 찾을 수 없습니다."));

        CartItem item = new CartItem();
        item.setUser(user);
        item.setArtwork(artwork);
        item.setType(type);
        item.setAddedAt(LocalDateTime.now());

        if (type == CartItemType.FIXED_PRICE) {
            FixedPriceSale sale = fixedPriceSaleRepository.findByArtworkId(artworkId)
                    .orElseThrow(() -> new RuntimeException("지정가 판매 정보 없음"));
            item.setFixedPriceSale(sale);
        } else if (type == CartItemType.AUCTION) {
            Auction auction = auctionRepository.findByArtworkId(artworkId)
                    .orElseThrow(() -> new RuntimeException("경매 정보 없음"));
            item.setAuction(auction);
        }

        cartItemRepository.save(item);
    }


    /**
     * 사용자 장바구니 전체 조회
     */
    @Transactional(readOnly = true)
    public List<CartItem> getUserCart(Long userId) {
        return cartItemRepository.findByUserIdOrderByAddedAtDesc(userId).stream()
                .filter(item -> {
                    if (item.getType() == CartItemType.FIXED_PRICE) {
                        return item.getArtwork().getSaleStatus() == SaleStatus.UNSOLD;
                    } else if (item.getType() == CartItemType.AUCTION) {
                        return item.getArtwork().getSaleStatus() == SaleStatus.PENDING &&
                                item.getAuction() != null &&
                                item.getAuction().getWinnerUserId() != null &&
                                item.getAuction().getWinnerUserId().equals(item.getUser().getId().toString());
                    }
                    return false;
                })
                .toList();
    }

    /**
     * 특정 작품 장바구니에서 제거
     */
    @Transactional
    public void removeFromCart(Long userId, Long artworkId) {
        cartItemRepository.deleteByUserIdAndArtworkId(userId, artworkId);
    }

    /**
     * 경매 낙찰 처리 시, 낙찰자 외 유저 장바구니에서 해당 작품 제거
     */
    @Transactional
    public void removeAuctionFromOthers(Long artworkId, Long winnerUserId) {
        cartItemRepository.deleteAllByArtworkIdAndUserIdNot(artworkId, winnerUserId);
    }

    public void deleteCartItem(Long cartItemId){
        cartItemRepository.deleteById(cartItemId);
    }
}
