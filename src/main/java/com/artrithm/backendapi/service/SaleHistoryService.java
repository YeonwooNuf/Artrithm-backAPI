package com.artrithm.backendapi.service;

import com.artrithm.backendapi.dto.PurchaseHistoryDto;
import com.artrithm.backendapi.dto.SaleHistoryDto;
import com.artrithm.backendapi.model.*;
import com.artrithm.backendapi.repository.AuctionRepository;
import com.artrithm.backendapi.repository.FixedPriceSaleRepository;
import com.artrithm.backendapi.repository.ArtworkRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class SaleHistoryService {

    private final FixedPriceSaleRepository fixedPriceSaleRepository;
    private final AuctionRepository auctionRepository;
    private final ArtworkRepository artworkRepository;

    // ✅ 구매 내역 (지정가/경매 결제 완료된 작품만)
    public List<PurchaseHistoryDto> getPurchaseHistory(Long userId) {
        List<PurchaseHistoryDto> result = new ArrayList<>();

        // ✅ 지정가 구매 (결제 완료된 작품만)
        List<FixedPriceSale> fixedSales = fixedPriceSaleRepository.findAllByBuyer_Id(userId);
        for (FixedPriceSale sale : fixedSales) {
            if (sale.getArtwork().getSaleStatus() != SaleStatus.SOLD) continue;

            result.add(PurchaseHistoryDto.builder()
                    .artworkId(sale.getArtwork().getId())
                    .artworkTitle(sale.getArtwork().getTitle())
                    .artworkImageUrl(sale.getArtwork().getImageUrl())
                    .price(sale.getPrice())
                    .sellerNickname(sale.getSeller().getNickname())
                    .purchasedAt(sale.getPayment().getPaidAt())
                    .method(PaymentTargetType.FIXED_ORDER) // 👈 고정가 구매
                    .build());
        }

        // ✅ 경매 낙찰 후 결제 완료된 작품만
        List<Auction> auctions = auctionRepository.findAllByWinnerUserId(String.valueOf(userId));
        for (Auction auction : auctions) {
            if (auction.getArtwork().getSaleStatus() != SaleStatus.SOLD) continue;

            result.add(PurchaseHistoryDto.builder()
                    .artworkId(auction.getArtwork().getId())
                    .artworkTitle(auction.getArtwork().getTitle())
                    .artworkImageUrl(auction.getArtwork().getImageUrl())
                    .price(auction.getFinalPrice())
                    .sellerNickname(auction.getArtwork().getUser().getNickname())
                    .purchasedAt(auction.getPayment().getPaidAt())
                    .method(PaymentTargetType.AUCTION_ORDER) // 👈 경매 구매
                    .build());
        }

        return result;
    }

    // ✅ 판매 내역 (판매 중 + 낙찰됨(미결제) + 결제 완료된 작품 모두)
    public List<SaleHistoryDto> getSaleHistory(Long userId) {
        List<SaleHistoryDto> result = new ArrayList<>();

        // 전체 작품 중 내가 등록한 것만 조회
        List<Artwork> myArtworks = artworkRepository.findByUserId(userId);

        for (Artwork artwork : myArtworks) {
            SaleStatus status = artwork.getSaleStatus();

            if (status == SaleStatus.UNSOLD) {
                // 판매 중 (지정가 또는 경매 등록만 되어 있음)
                result.add(SaleHistoryDto.builder()
                        .artworkId(artwork.getId())
                        .artworkTitle(artwork.getTitle())
                        .artworkImageUrl(artwork.getImageUrl())
                        .price(null) // 아직 미판매 상태
                        .buyerNickname(null)
                        .status("판매중")
                        .soldAt(null)
                        .build());

            } else if (status == SaleStatus.PENDING) {
                // 경매 낙찰됐지만 결제 전
                Auction auction = auctionRepository.findByArtworkId(artwork.getId()).orElse(null);
                if (auction != null && auction.getWinnerNickname() != null) {
                    result.add(SaleHistoryDto.builder()
                            .artworkId(artwork.getId())
                            .artworkTitle(artwork.getTitle())
                            .artworkImageUrl(artwork.getImageUrl())
                            .price(auction.getFinalPrice())
                            .buyerNickname(auction.getWinnerNickname())
                            .status("결제대기")
                            .soldAt(null)
                            .build());
                }

            } else if (status == SaleStatus.SOLD) {
                // 결제 완료된 판매
                FixedPriceSale sale = fixedPriceSaleRepository.findByArtworkId(artwork.getId()).orElse(null);
                if (sale != null && sale.getBuyer() != null) {
                    // 지정가 판매
                    result.add(SaleHistoryDto.builder()
                            .artworkId(artwork.getId())
                            .artworkTitle(artwork.getTitle())
                            .artworkImageUrl(artwork.getImageUrl())
                            .price(sale.getPrice())
                            .buyerNickname(sale.getBuyer().getNickname())
                            .status("판매완료")
                            .soldAt(sale.getPayment().getPaidAt())
                            .build());
                } else {
                    // 경매 판매
                    Auction auction = auctionRepository.findByArtworkId(artwork.getId()).orElse(null);
                    if (auction != null && auction.getWinnerNickname() != null) {
                        result.add(SaleHistoryDto.builder()
                                .artworkId(artwork.getId())
                                .artworkTitle(artwork.getTitle())
                                .artworkImageUrl(artwork.getImageUrl())
                                .price(auction.getFinalPrice())
                                .buyerNickname(auction.getWinnerNickname())
                                .status("판매완료")
                                .soldAt(auction.getPayment().getPaidAt())
                                .build());
                    }
                }
            }
        }
        return result;
    }
}
