package com.artrithm.backendapi.controller;

import com.artrithm.backendapi.dto.PaymentDto;
import com.artrithm.backendapi.dto.PaymentRequestDto;
import com.artrithm.backendapi.service.PaymentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/payments")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;

    // ✅ 결제 처리 (구독 / 장바구니 / 경매)
    @PostMapping
    public ResponseEntity<PaymentDto> processPayment(@RequestBody PaymentRequestDto requestDto) {
        PaymentDto result = paymentService.processPayment(requestDto);
        return ResponseEntity.ok(result);
    }
}
