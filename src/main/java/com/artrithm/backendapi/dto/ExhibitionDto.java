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

    // 작성자 정보
    private Long authorId;
    private String authorNickname; // ✅ 선택: 프론트에서 표시용

    private String title;
    private String description;
    private String theme;

    private String thumbnailUrl;

    private List<String> keywords;

    private List<ArtworkDto> artworks;

    private List<GuestbookEntryDto> guestbook;

    // ✅ 관리자 전용: 실존 작가 정보 연결
    private ArtistDto artistInfo;
}
