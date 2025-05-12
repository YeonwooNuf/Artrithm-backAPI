package com.artrithm.backendapi.dto;

import lombok.*;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ExhibitionDto {

    private Long id;
    private Long authorId;          // 작성자 userId
    private String title;
    private String description;
    private String theme;
    private String thumbnail;
    private List<String> keywords;

    private List<ArtworkDto> artworks;
    private List<GuestbookEntryDto> guestbook;
}
