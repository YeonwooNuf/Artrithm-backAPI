package com.artrithm.backendapi.search.service;

import co.elastic.clients.elasticsearch._types.query_dsl.MoreLikeThisQuery;
import co.elastic.clients.elasticsearch._types.query_dsl.MultiMatchQuery;
import co.elastic.clients.elasticsearch._types.query_dsl.Query;
import com.artrithm.backendapi.search.document.ExhibitionDocument;
import lombok.RequiredArgsConstructor;
import org.springframework.data.elasticsearch.client.elc.NativeQuery;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ExhibitionSearchService {

    private final ElasticsearchOperations elasticsearchTemplate;

    /**
     * 실시간 검색: title, description, keywords 전체 대상 + 오타 허용
     */
    public List<ExhibitionDocument> search(String queryText) {
        Query query = MultiMatchQuery.of(m -> m
                .query(queryText)
                .fields("title^3", "description^2", "keywords")
                .fuzziness("AUTO")
        )._toQuery();

        NativeQuery searchQuery = NativeQuery.builder()
                .withQuery(query)
                .build();

        SearchHits<ExhibitionDocument> hits = elasticsearchTemplate.search(searchQuery, ExhibitionDocument.class);
        return hits.stream().map(SearchHit::getContent).collect(Collectors.toList());
    }

    /**
     * 단일 키워드 기반 유사 전시 추천
     */
    public List<ExhibitionDocument> recommendByKeywordContext(String keyword) {
        Query query = MoreLikeThisQuery.of(m -> m
                .fields("keywords", "description")
                .like(l -> l.text(keyword))
                .minTermFreq(1)
                .minDocFreq(1)
        )._toQuery();

        NativeQuery searchQuery = NativeQuery.builder()
                .withQuery(query)
                .build();

        SearchHits<ExhibitionDocument> hits = elasticsearchTemplate.search(searchQuery, ExhibitionDocument.class);
        return hits.stream().map(SearchHit::getContent).collect(Collectors.toList());
    }

    /**
     * 전시 ID 기준 유사 전시 추천
     */
    public List<ExhibitionDocument> recommendSimilarToExhibition(Long exhibitionId) {
        Query query = MoreLikeThisQuery.of(m -> m
                .fields("title", "description", "keywords")
                .like(l -> l.document(d -> d
                        .index("exhibitions")
                        .id(exhibitionId.toString())
                ))
                .minTermFreq(1)
                .minDocFreq(1)
        )._toQuery();

        NativeQuery searchQuery = NativeQuery.builder()
                .withQuery(query)
                .build();

        SearchHits<ExhibitionDocument> hits = elasticsearchTemplate.search(searchQuery, ExhibitionDocument.class);
        return hits.stream().map(SearchHit::getContent).collect(Collectors.toList());
    }
}
