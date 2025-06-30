package com.artrithm.backendapi.dto;

import com.artrithm.backendapi.model.Exhibition;
import com.artrithm.backendapi.model.Keyword;
import com.artrithm.backendapi.search.document.ExhibitionDocument;
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
    private String authorNickname;        // ✅ 프론트에서 표시용
    private String authorProfileImage;    // ✅ 사용자 작가 전시용
    private String authorBio;             // ✅ 사용자 작가 전시용

    private String title;
    private String description;
    private String theme;
    private String thumbnailUrl;

    private List<String> keywords;
    private List<ArtworkDto> artworks;
    private List<GuestbookDto> guestbook;

    // ✅ 명화 전시일 경우에만 존재
    private ArtistDto artistInfo;

    public static ExhibitionDto fromEntity(Exhibition exhibition) {
        return ExhibitionDto.builder()
                .id(exhibition.getId())
                .title(exhibition.getTitle())
                .description(exhibition.getDescription())
                .theme(exhibition.getTheme())
                .thumbnailUrl(exhibition.getThumbnailUrl())
                .keywords(
                        exhibition.getKeywords() != null
                                ? exhibition.getKeywords().stream()
                                .map(Keyword::getName)
                                .toList()
                                : List.of()
                )
                .authorId(exhibition.getAuthor().getId())
                .authorNickname(exhibition.getAuthor().getNickname())
                .authorProfileImage(exhibition.getAuthor().getProfileImage()) // ✅
                .authorBio(exhibition.getAuthor().getArtistBio())             // ✅
                .artworks(
                        exhibition.getArtworks() != null
                                ? exhibition.getArtworks().stream()
                                .map(ArtworkDto::fromEntity)
                                .toList()
                                : List.of()
                )
                .guestbook(
                        exhibition.getGuestbook() != null
                                ? exhibition.getGuestbook().stream()
                                .map(GuestbookDto::fromEntity)
                                .toList()
                                : List.of()
                )
                .artistInfo(
                        exhibition.getArtist() != null
                                ? ArtistDto.fromEntity(exhibition.getArtist())
                                : null
                )
                .build();
    }

    public static ExhibitionDto fromDocument(ExhibitionDocument doc) {
        return ExhibitionDto.builder()
                .id(doc.getId())
                .title(doc.getTitle())
                .description(doc.getDescription())
                .thumbnailUrl(doc.getThumbnailUrl())
                .keywords(doc.getKeywords())
                .authorId(doc.getAuthorId())
                .build();
    }
}
