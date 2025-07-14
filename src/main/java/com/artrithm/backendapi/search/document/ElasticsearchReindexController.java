package com.artrithm.backendapi.search.document;

import com.artrithm.backendapi.search.service.ExhibitionIndexer;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/elasticsearch")
@RequiredArgsConstructor
public class ElasticsearchReindexController {

    private final ExhibitionIndexer exhibitionIndexer;

    @PostMapping("/reindex")
    public ResponseEntity<String> reindexAllExhibitions() {
        exhibitionIndexer.deleteAllAndReindex();
        return ResponseEntity.ok("✅ 모든 전시 데이터가 Elasticsearch에 재색인되었습니다.");
    }
}
