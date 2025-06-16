package com.artrithm.backendapi.controller;

import com.artrithm.backendapi.dto.ExhibitionDto;
import com.artrithm.backendapi.dto.UserLikeDto;
import com.artrithm.backendapi.service.UserLikeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/user/likes")
@RequiredArgsConstructor
public class UserLikeController {

    private final UserLikeService userLikeService;

    // ✅ 1. 좋아요 여부 조회
    @GetMapping("/{exhibitionId}")
    public ResponseEntity<UserLikeDto> getLikeStatus(
            @PathVariable Long exhibitionId,
            @RequestParam Long userId
    ) {
        UserLikeDto dto = userLikeService.getLikeStatus(userId, exhibitionId);
        return ResponseEntity.ok(dto);
    }

    // ✅ 2. 좋아요 등록
    @PostMapping("/{exhibitionId}")
    public ResponseEntity<Void> like(
            @PathVariable Long exhibitionId,
            @RequestParam Long userId
    ) {
        userLikeService.like(userId, exhibitionId);
        return ResponseEntity.ok().build();
    }

    // ✅ 3. 좋아요 취소
    @DeleteMapping("/{exhibitionId}")
    public ResponseEntity<Void> unlike(
            @PathVariable Long exhibitionId,
            @RequestParam Long userId
    ) {
        userLikeService.unlike(userId, exhibitionId);
        return ResponseEntity.noContent().build();
    }

    // ✅ 4. 사용자가 관심 등록한 전시 전체 목록 조회
    @GetMapping
    public ResponseEntity<List<ExhibitionDto>> getLikedExhibitions(
            @RequestParam Long userId
    ) {
        List<ExhibitionDto> liked = userLikeService.getLikedExhibitions(userId);
        return ResponseEntity.ok(liked);
    }
}
