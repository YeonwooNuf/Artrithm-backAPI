package com.artrithm.backendapi.controller;

import com.artrithm.backendapi.dto.ArtworkDto;
import com.artrithm.backendapi.service.ArtworkService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/artworks")
@RequiredArgsConstructor
public class ArtworkController {

    private final ArtworkService artworkService;

    //  작품 설명 파일 업로드
    @PostMapping("/{artworkId}/upload-explanation")
    public ResponseEntity<String> uploadExplanationFile(
            @PathVariable Long artworkId,
            @RequestParam("file") MultipartFile file) throws IOException {

        artworkService.uploadExplanationFile(artworkId, file);
        return ResponseEntity.ok("설명 파일이 성공적으로 업로드되었습니다.");
    }

    // (선택) 전체 작품 목록 조회 API
    // @GetMapping
    // public ResponseEntity<List<ArtworkDto>> getAllArtworks() {
    //     return ResponseEntity.ok(artworkService.getAllArtworks());
    // }

    //  관리자(명화 전시) 업로드 작품만 조회
    @GetMapping("/admin")
    public ResponseEntity<?> getAdminUploadedArtworks() {
        return ResponseEntity.ok(artworkService.getArtworksByAdmin());
    }

    @GetMapping("/my/{userId}")
    public ResponseEntity<List<ArtworkDto>> getMyArtworks(@PathVariable Long userId) {
        List<ArtworkDto> artworks = artworkService.getMyArtworks(userId);
        return ResponseEntity.ok(artworks);
    }

    @GetMapping("/allMy/{userId}")
    public ResponseEntity<List<ArtworkDto>> getAllMyArtworks(@PathVariable("userId") Long userId) {
        List<ArtworkDto> artworks = artworkService.getAllMyArtworks(userId);
        return ResponseEntity.ok(artworks);
    }
}
