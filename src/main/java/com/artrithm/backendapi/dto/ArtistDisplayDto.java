package com.artrithm.backendapi.dto;

import lombok.*;

import java.util.List;
import java.util.stream.Collectors;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ArtistDisplayDto {
    private String id;
    private String name; // name or nickname
    private String bio;  // bio or artistBio
    private String profileImage;
    private List<ArtworkDto> artworks;
    private String type; // "ARTIST" or "USER"

    public static ArtistDisplayDto fromArtistDto(ArtistDto a) {
        return ArtistDisplayDto.builder()
                .id("Artist_" + a.getId())
                .name(a.getName())
                .bio(a.getBio())
                .profileImage(a.getProfileImage())
                .artworks(
                        a.getArtworks() == null ? null :
                                a.getArtworks().stream()
                                        .filter(art -> "UNSOLD".equals(art.getSaleStatus()))
                                        .collect(Collectors.toList())
                )
                .type("ARTIST")
                .build();
    }

    public static ArtistDisplayDto fromUserDto(UserDto u) {
        return ArtistDisplayDto.builder()
                .id("User_" + u.getId())
                .name(u.getNickname())
                .bio(u.getArtistBio())
                .profileImage(u.getProfileImage())
                .artworks(
                        u.getArtworks() == null ? null :
                                u.getArtworks().stream()
                                        .filter(art -> "UNSOLD".equals(art.getSaleStatus()))
                                        .collect(Collectors.toList())
                )
                .type("USER")
                .build();
    }
}
