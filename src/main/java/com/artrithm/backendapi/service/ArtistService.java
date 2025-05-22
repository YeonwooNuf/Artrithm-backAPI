package com.artrithm.backendapi.service;

import com.artrithm.backendapi.dto.ArtistDto;
import com.artrithm.backendapi.model.Artist;
import com.artrithm.backendapi.repository.ArtistRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ArtistService {

    private final ArtistRepository artistRepository;

    // ✅ 전체 작가 목록 조회
    public List<ArtistDto> getAllArtists() {
        return artistRepository.findAll().stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    // ✅ 단일 작가 조회
    public ArtistDto getArtistById(Long id) {
        Artist artist = artistRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("작가를 찾을 수 없습니다."));
        return toDto(artist);
    }

    // ✅ 작가 등록
    public ArtistDto createArtist(ArtistDto dto) {
        Artist artist = Artist.builder()
                .name(dto.getName())
                .bio(dto.getBio())
                .profileImage(dto.getProfileImage())
                .nationality(dto.getNationality())
                .birthDate(dto.getBirthDate())
                .deathDate(dto.getDeathDate())
                .build();

        return toDto(artistRepository.save(artist));
    }

    private ArtistDto toDto(Artist artist) {
        return ArtistDto.builder()
                .id(artist.getId())
                .name(artist.getName())
                .bio(artist.getBio())
                .profileImage(artist.getProfileImage())
                .nationality(artist.getNationality())
                .birthDate(artist.getBirthDate())
                .deathDate(artist.getDeathDate())
                .build();
    }
}
