package com.artrithm.backendapi.controller;

import com.artrithm.backendapi.dto.ArtistDto;
import com.artrithm.backendapi.service.ArtistService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
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
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ArtistDto> createArtist(
            @RequestParam("name") String name,
            @RequestParam("bio") String bio,
            @RequestParam("nationality") String nationality,
            @RequestParam(value = "birthDate", required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate birthDate,
            @RequestParam(value = "deathDate", required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate deathDate,
            @RequestParam(value = "profileImageFile", required = false) MultipartFile profileImageFile
    ) {
        String profileImageUrl = null;
        if (profileImageFile != null && !profileImageFile.isEmpty()) {
            profileImageUrl = artistService.saveProfileImage(profileImageFile); // 또는 fileUploadService 사용
        }

        ArtistDto dto = ArtistDto.builder()
                .name(name)
                .bio(bio)
                .nationality(nationality)
                .birthDate(birthDate)
                .deathDate(deathDate)
                .profileImage(profileImageUrl)
                .build();

        return ResponseEntity.ok(artistService.createArtist(dto));
    }
}
