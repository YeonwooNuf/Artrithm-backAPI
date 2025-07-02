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
@RequestMapping("/api/exhibitions") // ✅ 공통 prefix
@RequiredArgsConstructor
public class ExhibitionController {

    private final ExhibitionService exhibitionService;

    // ✅ 전시 업로드
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> uploadExhibition(MultipartHttpServletRequest request) throws IOException {
        exhibitionService.saveExhibition(request);
        return ResponseEntity.ok("전시가 성공적으로 업로드되었습니다.");
    }

    // ✅ 단일 전시 상세 조회
    @GetMapping("/{id}")
    public ResponseEntity<ExhibitionDto> getExhibition(@PathVariable Long id) {
        return ResponseEntity.ok(exhibitionService.getExhibitionById(id));
    }

    // ✅ 전시 수정 (multipart/form-data 기반 PUT 처리)
    @PutMapping(value = "/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Void> updateExhibition(
            @PathVariable Long id,
            MultipartHttpServletRequest request) throws IOException {

        exhibitionService.updateExhibition(id, request);
        return ResponseEntity.ok().build();
    }

    @GetMapping
    public ResponseEntity<List<ExhibitionDto>> getExhibitions(@RequestParam(name="authorId",required = false) Long authorId) {
        if (authorId != null) {
            return ResponseEntity.ok(exhibitionService.getExhibitionsByAuthor(authorId));
        } else {
            return ResponseEntity.ok(exhibitionService.getAllExhibitions());
        }
    }

    // 전시 ID 목록으로 여러 전시 조회
    @GetMapping("/by-ids")
    public ResponseEntity<List<ExhibitionDto>> getExhibitionsByIds(@RequestParam("ids") List<Long> ids) {
        return ResponseEntity.ok(exhibitionService.getExhibitionsByIds(ids));
    }
}
