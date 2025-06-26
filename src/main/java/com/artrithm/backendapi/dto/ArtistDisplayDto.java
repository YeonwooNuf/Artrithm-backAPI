package com.artrithm.backendapi.dto;

import lombok.*;

import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ArtistDisplayDto {
    private Long id;
    private String name; // or nickname
    private String bio;  // artistBio or bio
    private String profileImage;
    private List<ArtworkDto> artworks;

    public static ArtistDisplayDto fromArtistDto(ArtistDto a) {
        return ArtistDisplayDto.builder()
                .id(a.getId())
                .name(a.getName())
                .bio(a.getBio())
                .profileImage(a.getProfileImage())
                .artworks(a.getArtworks())
                .build();
    }

    public static ArtistDisplayDto fromUserDto(UserDto u) {
        return ArtistDisplayDto.builder()
                .id(u.getId())
                .name(u.getNickname())
                .bio(u.getArtistBio())
                .profileImage(u.getProfileImage())
                .artworks(u.getArtworks())
                .build();
    }
}
