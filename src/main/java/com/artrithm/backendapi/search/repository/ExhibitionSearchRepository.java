package com.artrithm.backendapi.search.repository;

import com.artrithm.backendapi.search.document.ExhibitionDocument;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ExhibitionSearchRepository extends ElasticsearchRepository<ExhibitionDocument, Long> {

    // 키워드 리스트에 포함된 전시 검색
    List<ExhibitionDocument> findByKeywordsIn(List<String> keywords);

    // 제목에 포함된 전시 검색
    List<ExhibitionDocument> findByTitleContaining(String title);

}
