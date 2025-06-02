package com.artrithm.backendapi.service;

import com.artrithm.backendapi.dto.ArtistDto;
import com.artrithm.backendapi.dto.ArtworkDto;
import com.artrithm.backendapi.dto.AuctionBidDto;
import com.artrithm.backendapi.dto.AuctionDto;
import com.artrithm.backendapi.model.*;
import com.artrithm.backendapi.repository.AuctionBidRepository;
import com.artrithm.backendapi.repository.AuctionRepository;
import com.artrithm.backendapi.repository.UserRepository;

import lombok.AllArgsConstructor;
import lombok.Generated;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class AuctionService {

    private final AuctionBidRepository auctionBidRepository;
    private final AuctionRepository auctionRepository;
    private final UserRepository userRepository;

    @Getter
    @AllArgsConstructor
    static class BidInfo{
        private final String userId;
        private final int price;
    }

    //값 없으면 null이나 0 반환
    private BidInfo getOrNull(List<BidInfo> list, int index){
        return index < list.size() ? list.get(index) : new BidInfo(null,0);
    }
    private int getOrZero(List<BidInfo> list, int index){
        return index < list.size() ? list.get(index).getPrice() : 0;
    }

    public AuctionDto getAuctionById(Long id) {
        Auction auction = auctionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("해당 경매를 찾을 수 없습니다."));

        Artwork artwork = auction.getArtwork();
        User user = artwork.getUser(); // 👈 추가
        Artist artist = artwork.getArtist(); // 명화 작가일 경우

        // 낙찰자 nickname 조회
        User winner = auction.getWinnerUserId() != null
                ? userRepository.findById(Long.valueOf(auction.getWinnerUserId())).orElse(null)
                : null;

        return AuctionDto.builder()
                .id(auction.getAuctionId())
                .startTime(auction.getStartTime())
                .endTime(auction.getEndTime())
                .startPrice(auction.getStartPrice())
                .winnerUserId(auction.getWinnerUserId())
                .winnerNickname(winner!=null ? winner.getNickname() : null )
                .finalPrice(auction.getFinalPrice())
                .artwork(ArtworkDto.builder()
                        .title(artwork.getTitle())
                        .description(artwork.getDescription())
                        .imageUrl(artwork.getImageUrl())
                        .artistName(artist != null ? artist.getName() : null)
                        .userNickname(user != null ? user.getNickname() : null)
                        .build())
                .build();
    }


    public void updateTop3(Long auctionId, String userId, int newPrice) {
        //찾는데 처음이면 나머지 null이랑 0으로 채움
        AuctionBid top = auctionBidRepository.findById(auctionId).orElseGet(()->
                AuctionBid.builder().auctionId(auctionId)
                .top1UserId(userId).top1Price(newPrice)
                .top2UserId(null).top2Price(0)
                .top3UserId(null).top3Price(0)
                .build()
        );

        // 기존 top3 + 새 입찰 정보 추가
        List<BidInfo> bids = new ArrayList<>(List.of(
                new BidInfo(top.getTop1UserId(), top.getTop1Price()),
                new BidInfo(top.getTop2UserId(), top.getTop2Price()),
                new BidInfo(top.getTop3UserId(), top.getTop3Price()),
                new BidInfo(userId, newPrice)
        ));

        // userId 기준 중복 제거 ( 최근 입찰 우선)
        Map<String, BidInfo> unique = new HashMap<>();
        for (BidInfo b: bids){
            if(b.getUserId() == null) continue;
            unique.put(b.getUserId(),b);
        }

        //가격 내림차순 정렬 후 상위 3개 추출
        List<BidInfo> top3 = unique.values().stream().sorted((a,b)->b.getPrice()-a.getPrice())
                .limit(3)
                .toList();

        //정렬된 top3 다시 설정
        top.setTop1UserId(getOrNull(top3, 0).userId);
        top.setTop1Price(getOrZero(top3, 0));
        top.setTop2UserId(getOrNull(top3, 1).userId);
        top.setTop2Price(getOrZero(top3, 1));
        top.setTop3UserId(getOrNull(top3, 2).userId);
        top.setTop3Price(getOrZero(top3, 2));

        //auctionId 기준으로 있으면 update수행
        auctionBidRepository.save(top);
    }

    public AuctionBidDto getTop3(Long auctionId){
        AuctionBid top = auctionBidRepository.findById(auctionId).orElseThrow();
        return AuctionBidDto.builder()
                .auctionId(top.getAuctionId())
                .top1UserId(top.getTop1UserId()).top1Price(top.getTop1Price())
                .top2UserId(top.getTop2UserId()).top2Price(top.getTop2Price())
                .top3UserId(top.getTop3UserId()).top3Price(top.getTop3Price())
                .build();
    }

    //낙찰 완료시
    public void finalizeAuction(Long auctionId) {
        AuctionBid bid = auctionBidRepository.findById(auctionId).orElseThrow();
        Auction auction = auctionRepository.findById(bid.getAuctionId()).orElseThrow();

        auction.setWinnerUserId(bid.getTop1UserId());
        auction.setFinalPrice(bid.getTop1Price());
        auction.setStatus(AuctionStatus.ENDED);

        auctionRepository.save(auction);
    }


}
