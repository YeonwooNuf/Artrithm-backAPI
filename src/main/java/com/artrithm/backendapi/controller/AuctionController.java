package com.artrithm.backendapi.controller;

import com.artrithm.backendapi.dto.AuctionBidDto;
import com.artrithm.backendapi.dto.AuctionDto;
import com.artrithm.backendapi.service.AuctionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auctions")
@RequiredArgsConstructor
public class AuctionController {

    private final AuctionService auctionService;

    @GetMapping("/{id}")
    public AuctionDto getAuction(@PathVariable Long id) {
        return auctionService.getAuctionById(id);
    }

    @GetMapping("/bid/{id}")
    public ResponseEntity<AuctionBidDto> getTop3(@PathVariable Long id){
        return ResponseEntity.ok(auctionService.getTop3(id));
    }

    @PostMapping("/{id}/finalize")
    public ResponseEntity<Void> finalizeAuction(@PathVariable Long id){
        auctionService.finalizeAuction(id);
        return ResponseEntity.ok().build();
    }
}
