package com.artrithm.backendapi.dto;

import com.artrithm.backendapi.model.Artist;
import lombok.*;

import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ArtistDto {

    private Long id;                 // 작가 고유 ID
    private String name;            // 작가 이름 (ex. 빈센트 반 고흐)
    private String bio;             // 작가 소개글
    private String profileImage;    // 작가 프로필 이미지 URL
    private String nationality;     // 국적 (예: 네덜란드)
    private LocalDate birthDate;    // 출생일
    private LocalDate deathDate;    // 사망일

    private List<ArtworkDto> artworks; // 작가가 등록한 작품들

    public static ArtistDto fromEntity(Artist artist) {
        return ArtistDto.builder()
                .id(artist.getId())
                .name(artist.getName())
                .bio(artist.getBio())
                .profileImage(artist.getProfileImage())
                .nationality(artist.getNationality())
                .birthDate(artist.getBirthDate())
                .deathDate(artist.getDeathDate())
                .artworks(
                        artist.getArtworks() != null
                                ? artist.getArtworks().stream()
                                .map(ArtworkDto::fromEntity)
                                .toList()
                                : List.of()
                )
                .build();
    }
}