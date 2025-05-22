package com.artrithm.backendapi.controller;

import com.artrithm.backendapi.dto.ArtistDto;
import com.artrithm.backendapi.service.ArtistService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/artists")
@RequiredArgsConstructor
public class ArtistController {

    private final ArtistService artistService;

    // ✅ 전체 작가 목록 조회
    @GetMapping
    public ResponseEntity<List<ArtistDto>> getAllArtists() {
        return ResponseEntity.ok(artistService.getAllArtists());
    }

    // ✅ 단일 작가 조회
    @GetMapping("/{id}")
    public ResponseEntity<ArtistDto> getArtist(@PathVariable Long id) {
        return ResponseEntity.ok(artistService.getArtistById(id));
    }

    // ✅ 작가 등록 (관리자만)
    @PostMapping
    public ResponseEntity<ArtistDto> createArtist(@RequestBody ArtistDto dto) {
        // ⚠️ 추후 관리자 권한 검증 로직 추가 필요
        return ResponseEntity.ok(artistService.createArtist(dto));
    }
}
