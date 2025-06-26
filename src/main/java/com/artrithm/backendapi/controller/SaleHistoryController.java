package com.artrithm.backendapi.controller;

import com.artrithm.backendapi.dto.PurchaseHistoryDto;
import com.artrithm.backendapi.dto.SaleHistoryDto;
import com.artrithm.backendapi.service.SaleHistoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/history")
@RequiredArgsConstructor
public class SaleHistoryController {

    private final SaleHistoryService saleHistoryService;

    // ✅ 구매 내역 조회
    @GetMapping("/purchases/{userId}")
    public ResponseEntity<List<PurchaseHistoryDto>> getPurchaseHistory(@PathVariable Long userId) {
        List<PurchaseHistoryDto> history = saleHistoryService.getPurchaseHistory(userId);
        return ResponseEntity.ok(history);
    }

    // ✅ 판매 내역 조회
    @GetMapping("/sales/{userId}")
    public ResponseEntity<List<SaleHistoryDto>> getSaleHistory(@PathVariable Long userId) {
        List<SaleHistoryDto> history = saleHistoryService.getSaleHistory(userId);
        return ResponseEntity.ok(history);
    }
}
