package com.artrithm.backendapi.controller;

import com.artrithm.backendapi.dto.GuestbookDto;
import com.artrithm.backendapi.service.GuestbookService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/exhibitions/{exhibitionId}/guestbook")
@RequiredArgsConstructor
public class GuestbookController {

    private final GuestbookService guestbookService;

    @GetMapping
    public List<GuestbookDto> getAll(@PathVariable Long exhibitionId) {
        return guestbookService.getAll(exhibitionId);
    }

    @PostMapping
    public ResponseEntity<?> write(@PathVariable Long exhibitionId,
                                   @RequestBody GuestbookDto dto) {
        guestbookService.write(exhibitionId, dto);
        return ResponseEntity.ok("작성 완료");
    }

    @PutMapping("/{guestbookId}")
    public ResponseEntity<?> updateGuestbook(@PathVariable Long exhibitionId,
                                             @PathVariable Long guestbookId,
                                             @RequestBody GuestbookDto dto) {
        guestbookService.update(guestbookId, dto);
        return ResponseEntity.ok("수정 완료");
    }

    @DeleteMapping("/{guestbookId}")
    public ResponseEntity<?> deleteGuestbook(@PathVariable Long exhibitionId,
                                             @PathVariable Long guestbookId,
                                             @RequestParam Long userId) {
        guestbookService.delete(guestbookId, userId);
        return ResponseEntity.ok("삭제 완료");
    }

}
