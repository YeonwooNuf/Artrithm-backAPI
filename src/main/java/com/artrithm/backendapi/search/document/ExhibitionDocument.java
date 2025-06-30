package com.artrithm.backendapi.search.document;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;

import java.util.List;

@Document(indexName = "exhibitions")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ExhibitionDocument {

    @Id
    private Long id;

    private String title;
    private String description;

    private List<String> keywords;

    // 필요한 경우 추가 필드
    private String thumbnailUrl;
    private Long authorId;
    private Long artistId;
}
