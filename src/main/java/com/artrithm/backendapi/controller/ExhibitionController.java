package com.artrithm.backendapi.controller;

import com.artrithm.backendapi.dto.ExhibitionDto;
import com.artrithm.backendapi.service.ExhibitionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/exhibitions")
@RequiredArgsConstructor
public class ExhibitionController {

    private final ExhibitionService exhibitionService;

    // 전시 업로드
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> uploadExhibition(MultipartHttpServletRequest request) throws IOException {
        exhibitionService.saveExhibition(request);
        return ResponseEntity.ok("전시가 성공적으로 업로드되었습니다.");
    }

    // 전체 전시 목록 조회
    @GetMapping
    public ResponseEntity<List<ExhibitionDto>> getAllExhibitions() {
        return ResponseEntity.ok(exhibitionService.getAllExhibitions());
    }

    // 단일 전시 상세 조회
    @GetMapping("/{id}")
    public ResponseEntity<ExhibitionDto> getExhibition(@PathVariable Long id) {
        return ResponseEntity.ok(exhibitionService.getExhibitionById(id));
    }
}
