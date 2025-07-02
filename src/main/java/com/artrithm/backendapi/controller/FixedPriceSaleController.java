package com.artrithm.backendapi.controller;

import com.artrithm.backendapi.dto.FixedPriceArtworkDto;
import com.artrithm.backendapi.dto.FixedPriceSaleDto;
import com.artrithm.backendapi.service.FixedPriceSaleService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/fixed-price-sale")
@RequiredArgsConstructor
public class FixedPriceSaleController {

    private final FixedPriceSaleService fixedPriceSaleService;

    /**
     * 판매 작품 전체 조회
     */
    @GetMapping("/all")
    public ResponseEntity<List<FixedPriceArtworkDto>> getAllSales() {
        return ResponseEntity.ok(fixedPriceSaleService.getAllFixedPriceSales());
    }

    /**
     * 지정가 판매 신청 API
     */
    @PostMapping
    public ResponseEntity<Void> applyForFixedPriceSale(@RequestBody FixedPriceSaleDto dto) {
        fixedPriceSaleService.registerFixedPriceSale(dto.getArtworkId(), dto.getPrice(), dto.getSellerUserId());
        return ResponseEntity.ok().build();
    }
}
