package com.artrithm.backendapi.service;

import com.artrithm.backendapi.dto.FixedPriceArtistGroupDto;
import com.artrithm.backendapi.dto.FixedPriceArtworkDto;
import com.artrithm.backendapi.dto.FixedPriceSaleDto;
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

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class FixedPriceSaleService {

    private final FixedPriceSaleRepository fixedPriceSaleRepository;
    private final ArtworkRepository artworkRepository;
    private final UserRepository userRepository;

    /**
     * 지정가 판매 신청
     * @param artworkId : 신청할 작품 ID
     * @param price : 지정가 판매 가격
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


// 작가랑 작품 모으기
    public List<FixedPriceArtistGroupDto> getGroupedFixedPriceSales() {
        List<FixedPriceSale> sales = fixedPriceSaleRepository.findAllWithArtworkAndUser();

        Map<Long, FixedPriceArtistGroupDto> grouped = new LinkedHashMap<>();

        for (FixedPriceSale sale : sales) {
            Artwork artwork = sale.getArtwork();
            User artist = artwork.getUser();  // 여기서 작가 정보 얻음

            Long artistId = artist.getId();
            String name = artist.getNickname();  // 작가 이름으로 nickname 사용
            String profileImg = artist.getProfileImage();
            String bio = artist.getArtistBio();  // artistBio 필드 있음

            grouped.computeIfAbsent(artistId, id -> new FixedPriceArtistGroupDto(
                    artistId, name, profileImg, bio, new ArrayList<>()
            )).getWorks().add(
                    new FixedPriceArtworkDto(
                            artwork.getId(),
                            artwork.getTitle(),
                            artwork.getImageUrl(),  // 이미지 컬럼 이름에 따라 조정 필요
                            artwork.getDescription(),
                            sale.getPrice(),
                            artwork.getExhibition().getId()
                    )
            );
        }
        return new ArrayList<>(grouped.values());
    }
}
