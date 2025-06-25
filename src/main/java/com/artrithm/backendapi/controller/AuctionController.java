package com.artrithm.backendapi.controller;

import com.artrithm.backendapi.dto.AuctionBidDto;
import com.artrithm.backendapi.dto.AuctionDto;
import com.artrithm.backendapi.dto.AuctionRegisterDto;
import com.artrithm.backendapi.dto.AuctionRequestDto;
import com.artrithm.backendapi.model.Auction;
import com.artrithm.backendapi.model.AuctionRequest;
import com.artrithm.backendapi.model.AuctionStatus;
import com.artrithm.backendapi.repository.AuctionRepository;
import com.artrithm.backendapi.repository.AuctionRequestRepository;
import com.artrithm.backendapi.service.AuctionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/auctions")
@RequiredArgsConstructor
public class AuctionController {

    private final AuctionService auctionService;
    private final AuctionRequestRepository auctionRequestRepository;
    private final AuctionRepository auctionRepository;

    @GetMapping("/ongoing")
    public ResponseEntity<Long> getCurrentOngoingAuctionId() {
        Optional<Auction> ongoing = auctionRepository.findFirstByStatus(AuctionStatus.ONGOING);
        if (ongoing.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
        return ResponseEntity.ok(ongoing.get().getId());
    }

    @GetMapping("/{id}")
    public AuctionDto getAuction(@PathVariable("id") Long id) {
        return auctionService.getAuctionById(id);
    }

    @GetMapping("/bid/{id}")
    public ResponseEntity<AuctionBidDto> getTop3(@PathVariable("id") Long id) {
        return ResponseEntity.ok(auctionService.getTop3(id));
    }

    @PostMapping("/{id}/finalize")
    public ResponseEntity<Void> finalizeAuction(@PathVariable("id") Long id) {
        auctionService.finalizeAuction(id);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/request")
    public ResponseEntity<String> createAuctionRequest(@RequestBody AuctionRequestDto dto) {
        auctionService.requestAuction(dto);
        return ResponseEntity.ok("경매 신청이 완료되었습니다.");
    }

    @GetMapping("/admin/pending")
    public ResponseEntity<List<Map<String, Object>>> getPendingRequests() {
        List<AuctionRequest> requests = auctionRequestRepository.findByApproved(false);

        List<Map<String, Object>> result = requests.stream().map(req -> {
            Map<String, Object> map = new HashMap<>();
            map.put("requestId", req.getId());
            map.put("artworkId", req.getArtwork().getId());
            map.put("title", req.getArtwork().getTitle());
            map.put("imageUrl", req.getArtwork().getImageUrl());
            map.put("startPrice", req.getStartPrice());
            return map;
        }).collect(Collectors.toList());

        return ResponseEntity.ok(result);
    }

    @PostMapping("/admin/register")
    public ResponseEntity<String> registerAuction(@RequestBody AuctionRegisterDto dto) {
        try {
            auctionService.registerAuction(dto);
            return ResponseEntity.ok("경매가 등록되었습니다.");
        } catch (IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
        }
    }

    @GetMapping("/latest-ended")
    public ResponseEntity<Object> getLatestEndedAuction() {
        Optional<Auction> optional = auctionService.getLatestEndedAuction();

        if (optional.isPresent()) {
            return ResponseEntity.ok(optional.get());
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("최근 종료된 경매가 없습니다.");
        }
    }

    @PostMapping("/{id}/cancel")
    public ResponseEntity<Void> cancelAuction(@PathVariable Long id) {
        auctionService.markAuctionAsCancelled(id);
        return ResponseEntity.ok().build();
    }
}
