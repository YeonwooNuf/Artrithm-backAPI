package com.artrithm.backendapi.search.service;

import com.artrithm.backendapi.model.Exhibition;
import com.artrithm.backendapi.repository.ExhibitionRepository;
import com.artrithm.backendapi.search.document.ExhibitionDocument;
import com.artrithm.backendapi.search.repository.ExhibitionSearchRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ExhibitionIndexer {

    private final ExhibitionRepository exhibitionRepository;
    private final ExhibitionSearchRepository searchRepository;

    public void reindexAllExhibitions() {
        List<Exhibition> exhibitions = exhibitionRepository.findAll();
        List<ExhibitionDocument> docs = exhibitions.stream()
                .map(this::toDocument)
                .toList();
        searchRepository.saveAll(docs);
    }

    private ExhibitionDocument toDocument(Exhibition exhibition) {
        return ExhibitionDocument.builder()
                .id(exhibition.getId())
                .title(exhibition.getTitle())
                .description(exhibition.getDescription())
                .thumbnailUrl(exhibition.getThumbnailUrl())
                .authorId(exhibition.getAuthor().getId())
                .artistId(exhibition.getArtist() != null ? exhibition.getArtist().getId() : null)
                .keywords(exhibition.getKeywords().stream().map(k -> k.getName()).toList())
                .build();
    }
}
