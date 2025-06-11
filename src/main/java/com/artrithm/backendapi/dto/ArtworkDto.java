package com.artrithm.backendapi.dto;

import com.artrithm.backendapi.model.Artwork;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ArtworkDto {

    private Long id;
    private String title;
    private String description;
    private String imageUrl;

    private String artistName; // 명화 작가일 경우
    private String userNickname; // 개인 작가일 경우
    private String explanationFileUrl; // 명화 설명용 PDF

    public static ArtworkDto fromEntity(Artwork artwork) {
        return ArtworkDto.builder()
                .id(artwork.getId())
                .title(artwork.getTitle())
                .description(artwork.getDescription())
                .imageUrl(artwork.getImageUrl())
                .explanationFileUrl(artwork.getExplanationFileUrl()) // 명화 설명 PDF
                .artistName(
                        artwork.getArtist() != null
                                ? artwork.getArtist().getName()
                                : null
                )
                .userNickname(
                        artwork.getUser() != null
                                ? artwork.getUser().getNickname()
                                : null
                )
                .build();
    }
}
