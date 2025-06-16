package com.artrithm.backendapi.dto;

import com.artrithm.backendapi.model.Artwork;
import lombok.*;

import java.util.List;

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

    private String userProfileImage; // 개인 작가의 프로필 사진

    // 명화 전시 관련
    private Long artistId;
    private String artistName; // 명화 작가일 경우

    // 개인 전시 관련
    private Long userId;
    private String userNickname; // 개인 작가일 경우

    private Long exhibitionId; // 이 작품이 속한 전시 ID
    private String explanationFileUrl; // 명화 설명용 PDF

    public static ArtworkDto fromEntity(Artwork artwork) {
        return ArtworkDto.builder()
                .id(artwork.getId())
                .title(artwork.getTitle())
                .description(artwork.getDescription())
                .imageUrl(artwork.getImageUrl())
                .explanationFileUrl(artwork.getExplanationFileUrl()) // 명화 설명 PDF

                // 👤 개인 작가 정보
                .userId(artwork.getUser() != null ? artwork.getUser().getId() : null)
                .userNickname(artwork.getUser() != null ? artwork.getUser().getNickname() : null)
                .userProfileImage(
                        artwork.getUser() != null
                                ? artwork.getUser().getProfileImage()
                                : null
                )
                // 🎨 명화 작가 정보
                .artistId(artwork.getArtist() != null ? artwork.getArtist().getId() : null)
                .artistName(artwork.getArtist() != null ? artwork.getArtist().getName() : null)

                // 전시 ID
                .exhibitionId(artwork.getExhibition() != null ? artwork.getExhibition().getId() : null)
                .build();
    }
}
