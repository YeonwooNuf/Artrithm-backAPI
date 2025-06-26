package com.artrithm.backendapi.service;

import com.artrithm.backendapi.dto.*;
import com.artrithm.backendapi.model.*;
import com.artrithm.backendapi.repository.*;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;

@Service
@RequiredArgsConstructor
public class AuctionService {

    private final AuctionBidRepository auctionBidRepository;
    private final AuctionRepository auctionRepository;
    private final UserRepository userRepository;
    private final CartService cartService;

    private final ArtworkRepository artworkRepository;
    private final AuctionRequestRepository auctionRequestRepository;
    private final PaymentRepository paymentRepository;

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
                .id(auction.getId())
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
        Auction auction = auctionRepository.findById(auctionId)
                .orElseThrow(() -> new RuntimeException("경매를 찾을 수 없습니다."));

        AuctionBid top = auctionBidRepository.findByAuction_Id(auctionId)
                .orElseGet(() ->
                        AuctionBid.builder()
                                .auction(auction) // ✅ 연관관계로 설정
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

        Map<String, BidInfo> unique = new HashMap<>();
        for (BidInfo b : bids) {
            if (b.getUserId() == null) continue;
            unique.put(b.getUserId(), b);
        }

        List<BidInfo> top3 = unique.values().stream()
                .sorted((a, b) -> b.getPrice() - a.getPrice())
                .limit(3)
                .toList();

        top.setTop1UserId(getOrNull(top3, 0).userId);
        top.setTop1Price(getOrZero(top3, 0));
        top.setTop2UserId(getOrNull(top3, 1).userId);
        top.setTop2Price(getOrZero(top3, 1));
        top.setTop3UserId(getOrNull(top3, 2).userId);
        top.setTop3Price(getOrZero(top3, 2));

        auctionBidRepository.save(top);
    }

    public AuctionBidDto getTop3(Long auctionId) {
        AuctionBid bid = auctionBidRepository.findByAuction_Id(auctionId)
                .orElseThrow(() -> new RuntimeException("입찰 정보가 없습니다."));

        return AuctionBidDto.builder()
                .auctionId(auctionId)
                .top1UserId(bid.getTop1UserId()).top1Price(bid.getTop1Price())
                .top2UserId(bid.getTop2UserId()).top2Price(bid.getTop2Price())
                .top3UserId(bid.getTop3UserId()).top3Price(bid.getTop3Price())
                .build();
    }

    // 낙찰 완료시
    @Transactional
    public void finalizeAuction(Long auctionId) {
        Auction auction = auctionRepository.findById(auctionId)
                .orElseThrow(() -> new RuntimeException("해당 경매를 찾을 수 없습니다."));

        Optional<AuctionBid> bidOpt = auctionBidRepository.findByAuction_Id(auctionId);

        if (bidOpt.isPresent() && bidOpt.get().getTop1UserId() != null) {
            AuctionBid bid = bidOpt.get();

            // 낙찰자 정보 저장
            auction.setWinnerUserId(bid.getTop1UserId());
            auction.setFinalPrice(bid.getTop1Price());
//            auction.setPaymentDeadline(LocalDateTime.now().plusHours(24));

            User winner = userRepository.findById(Long.valueOf(bid.getTop1UserId())).orElse(null);
            if (winner != null) {
                auction.setWinnerNickname(winner.getNickname());
            }

            // 작품 상태 변경
            Artwork artwork = auction.getArtwork();
            artwork.setSaleStatus(SaleStatus.PENDING); // "결제 대기중"
            artworkRepository.save(artwork);

            // 장바구니에 낙찰작품 추가
            cartService.addToCart(winner.getId(), artwork.getId(), CartItemType.AUCTION, auction.getId(), null);
        } else {
            // 상태를 유찰로 변경
            auction.setStatus(AuctionStatus.CANCELLED);
            auctionRepository.saveAndFlush(auction);

            // 작품 상태 초기화 (판매 안됨 상태로)
            Artwork artwork = auction.getArtwork();
            artwork.setSaleStatus(null); // 혹은 SaleStatus.NONE 또는 미지정 상태
            artworkRepository.save(artwork);
        }

        // 상태 종료로 변경
        auction.setStatus(AuctionStatus.ENDED);
        auctionRepository.saveAndFlush(auction);
    }

    //경매 신청
    public void requestAuction(AuctionRequestDto dto) {
        Artwork artwork = artworkRepository.findById(dto.getArtworkId())
                .orElseThrow(() -> new IllegalArgumentException("작품이 존재하지 않습니다."));

        // 이미 등록된 작품은 다시 신청 불가
        if (artwork.getSaleStatus() == SaleStatus.UNSOLD ||
                artwork.getSaleStatus() == SaleStatus.PENDING) {
            throw new IllegalStateException("이미 경매가 신청되었거나 대기 중입니다.");
        }

        //경매 신청 저장
        AuctionRequest request = AuctionRequest.builder()
                .artwork(artwork)
                .startPrice(dto.getStartPrice())
                .approved(false)
                .build();

        auctionRequestRepository.save(request);

        //신청과 동시에 상태 unsold로 변경
        artwork.setSaleStatus(SaleStatus.UNSOLD);
        artworkRepository.save(artwork);
    }

    // 경매 등록
    public void registerAuction(AuctionRegisterDto dto) {
        boolean isOngoingExists = auctionRepository.existsByStatus(AuctionStatus.ONGOING);
        if (isOngoingExists) {
            throw new IllegalStateException("이미 진행 중인 경매가 있습니다.");
        }

        AuctionRequest request = auctionRequestRepository.findById(dto.getAuctionRequestId())
                .orElseThrow(() -> new IllegalArgumentException("신청 정보를 찾을 수 없습니다."));

        // 승인 처리
        request.setApproved(true);
        auctionRequestRepository.save(request);

        // 현재 시간 기준으로 경매 시작
        LocalDateTime now = LocalDateTime.now();

        Auction auction = Auction.builder()
                .artwork(request.getArtwork())
                .startPrice(request.getStartPrice())
                .startTime(now)
                .endTime(dto.getEndTime())
                .status(AuctionStatus.ONGOING)
                .build();

        auctionRepository.save(auction);

        // ✅ Auction 등록 후 Bid도 생성 (이게 빠져있어서 문제였음)
        AuctionBid bid = AuctionBid.builder()
                .auction(auction)
                .top1UserId(null)
                .top1Price(0)
                .top2UserId(null)
                .top2Price(0)
                .top3UserId(null)
                .top3Price(0)
                .build();

        auctionBidRepository.save(bid);
    }

    @Transactional
    public void markAuctionAsCancelled(Long auctionId) {
        Auction auction = auctionRepository.findById(auctionId)
                .orElseThrow(() -> new RuntimeException("해당 경매를 찾을 수 없습니다."));

        if (auction.getStatus() != AuctionStatus.ONGOING) {
            throw new IllegalStateException("진행 중인 경매가 아닙니다.");
        }

        Optional<AuctionBid> bidOpt = auctionBidRepository.findByAuction_Id(auctionId);
        if (bidOpt.isPresent() && bidOpt.get().getTop1UserId() != null) {
            throw new IllegalStateException("입찰자가 있는 경매는 유찰 처리할 수 없습니다.");
        }

        // 유찰 처리
        auction.setStatus(AuctionStatus.CANCELLED);
        auctionRepository.saveAndFlush(auction);

        // 작품 상태 초기화
        Artwork artwork = auction.getArtwork();
        artwork.setSaleStatus(null); // ✅ saleStatus를 null로 초기화
        artworkRepository.save(artwork);
    }

    @Transactional
    public void handleOverdueAuctionPayments() {
        LocalDateTime now = LocalDateTime.now();

        List<Auction> expired = auctionRepository.findAll().stream()
                .filter(a -> a.getStatus() == AuctionStatus.ENDED)
                .filter(a -> a.getWinnerUserId() != null)
                .filter(a -> {
                    Artwork artwork = a.getArtwork();
                    return artwork.getSaleStatus() == SaleStatus.PENDING
                            && a.getEndTime().plusHours(24).isBefore(now);
                })
                .toList();

        for (Auction auction : expired) {
            Artwork artwork = auction.getArtwork();

            // 1. 작품 상태 초기화 → 재판매 가능
            artwork.setSaleStatus(null);
            artworkRepository.save(artwork);

            // 2. 낙찰자 조회
            String winnerIdStr = auction.getWinnerUserId();
            if (winnerIdStr == null) continue;
            Long winnerId = Long.valueOf(winnerIdStr);
            User user = userRepository.findById(winnerId)
                    .orElseThrow(() -> new RuntimeException("낙찰자 정보를 찾을 수 없습니다."));

            // 3. 패널티 결제 생성
            Payment penalty = Payment.builder()
                    .user(user) // ✅ 연관관계로 바꿨다면
                    .targetType(PaymentTargetType.PENALTY)
                    .paymentMethod("NONE") // 아직 결제 안됨
                    .paymentId("penalty-" + UUID.randomUUID())
                    .totalAmount(5000) // 예시: 5,000원
                    .paidAt(null) // 아직 미결제
                    .build();

            paymentRepository.save(penalty);
        }
    }

    public Optional<Auction> getOngoingAuction() {
        return auctionRepository.findFirstByStatusOrderByStartTimeDesc(AuctionStatus.ONGOING);
    }

    public Optional<Auction> getLatestEndedAuction() {
        return auctionRepository.findFirstByStatusOrderByEndTimeDesc(AuctionStatus.ENDED);
    }

    public List<AuctionRequest> getUnapprovedRequests() {
        return auctionRequestRepository.findByApproved(false);
    }

    public List<Auction> getOngoingAuctions(){
        return auctionRepository.findByStatus(AuctionStatus.ONGOING);
    }
}
