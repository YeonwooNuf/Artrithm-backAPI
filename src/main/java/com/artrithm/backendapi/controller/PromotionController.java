package com.artrithm.backendapi.controller;

import com.artrithm.backendapi.dto.UserPromotionRequestDto;
import com.artrithm.backendapi.service.PromotionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/promotion-requests")
public class PromotionController {

    private final PromotionService promotionService;

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> submitRequest(
            @RequestParam Long userId,
            @RequestParam String reason,
            @RequestParam("artworkImages") List<MultipartFile> artworkImages
    ) {
        try {
            promotionService.submitRequest(userId, reason, artworkImages);
            return ResponseEntity.ok("작가 승인 요청이 완료되었습니다.");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/pending")
    public ResponseEntity<List<UserPromotionRequestDto>> getPendingRequests() {
        return ResponseEntity.ok(promotionService.getAllPendingRequests());
    }

    @PutMapping("/{requestId}/approve")
    public ResponseEntity<?> approve(@PathVariable Long requestId) {
        promotionService.approveRequest(requestId);
        return ResponseEntity.ok("승인 처리 완료");
    }
}
