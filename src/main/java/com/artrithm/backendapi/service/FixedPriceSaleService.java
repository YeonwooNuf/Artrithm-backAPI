package com.artrithm.backendapi.service;

import com.artrithm.backendapi.dto.FixedPriceArtworkDto;
import com.artrithm.backendapi.model.Artwork;
import com.artrithm.backendapi.model.FixedPriceSale;
import com.artrithm.backendapi.model.SaleStatus;
import com.artrithm.backendapi.model.User;
import com.artrithm.backendapi.repository.ArtworkRepository;
import com.artrithm.backendapi.repository.FixedPriceSaleRepository;
import com.artrithm.backendapi.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class FixedPriceSaleService {

    private final FixedPriceSaleRepository fixedPriceSaleRepository;
    private final ArtworkRepository artworkRepository;
    private final UserRepository userRepository;

    /**
     * 지정가 판매 신청
     *
     * @param artworkId    : 신청할 작품 ID
     * @param price        : 지정가 판매 가격
     * @param sellerUserId : 판매자 ID
     */
    @Transactional
    public void registerFixedPriceSale(Long artworkId, Integer price, Long sellerUserId) {
        // 작품 확인
        Artwork artwork = artworkRepository.findById(artworkId)
                .orElseThrow(() -> new IllegalArgumentException("작품이 존재하지 않습니다."));

        // 이미 판매 상태가 지정되어 있다면 신청 불가
        if (artwork.getSaleStatus() != null) {
            throw new IllegalStateException("이미 판매 중이거나 판매된 작품입니다.");
        }

        // 판매자 확인
        User seller = userRepository.findById(sellerUserId)
                .orElseThrow(() -> new IllegalArgumentException("판매자 정보를 찾을 수 없습니다."));

        // 지정가 판매 객체 생성
        FixedPriceSale sale = FixedPriceSale.builder()
                .artwork(artwork)
                .seller(seller)
                .price(price)
                .build();

        // 작품 상태 업데이트
        artwork.setSaleStatus(SaleStatus.UNSOLD);

        // 저장
        artworkRepository.save(artwork);
        fixedPriceSaleRepository.save(sale);
    }

    // 모든 지정가 판매 작품을 개별적으로 조회
    public List<FixedPriceArtworkDto> getAllFixedPriceSales() {
        List<FixedPriceSale> sales = fixedPriceSaleRepository.findAllWithArtworkAndUser();

        List<FixedPriceArtworkDto> result = new ArrayList<>();

        for (FixedPriceSale sale : sales) {
            Artwork artwork = sale.getArtwork();

            result.add(FixedPriceArtworkDto.builder()
                    .artworkId(artwork.getId())
                    .artworkTitle(artwork.getTitle())
                    .artworkImageUrl(artwork.getImageUrl())
                    .description(artwork.getDescription())
                    .price(sale.getPrice())
                    .exhibitionId(artwork.getExhibition().getId())
                    .sellerUserId(sale.getSeller().getId())
                    .sellerNickname(sale.getSeller().getNickname())
                    .buyerUserId(sale.getBuyer() != null ? sale.getBuyer().getId() : null)
                    .createdAt(sale.getCreatedAt()) // ✅ 판매 등록 날짜 추가
                    .build());
        }

        return result;
    }
}
