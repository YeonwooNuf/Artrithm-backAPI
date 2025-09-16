package com.artrithm.backendapi.controller;

import com.artrithm.backendapi.search.document.ExhibitionDocument;
import com.artrithm.backendapi.search.service.ExhibitionSearchService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/search")
@RequiredArgsConstructor
public class ExhibitionSearchController {

    private final ExhibitionSearchService searchService;

    // ✅ 실시간 검색 (키워드 기반)
    @GetMapping
    public List<ExhibitionDocument> search(@RequestParam("query") String query) {
        return searchService.search(query);
    }

    // ✅ 단일 키워드 기반 추천
    @GetMapping("/recommend")
    public List<ExhibitionDocument> recommendByKeyword(@RequestParam("keyword") List<String> keyword) {
        return searchService.recommendByKeywordContext(keyword);
    }

    // ✅ 전시 ID 기반 유사 전시 추천
    @GetMapping("/recommend/{exhibitionId}")
    public List<ExhibitionDocument> recommendByExhibitionId(@PathVariable("exhibitionId") Long exhibitionId) {
        return searchService.recommendSimilarToExhibition(exhibitionId);
    }
}
