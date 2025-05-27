package com.artrithm.backendapi.service;

import com.artrithm.backendapi.dto.ArtworkDto;
import com.artrithm.backendapi.model.Artwork;
import com.artrithm.backendapi.model.User;
import com.artrithm.backendapi.repository.ArtworkRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ArtworkService {

    private final ArtworkRepository artworkRepository;
    private final FileUploadService fileUploadService;

    // ✅ 작품 설명 파일 업로드
    public void uploadExplanationFile(Long artworkId, MultipartFile file) throws IOException {
        Artwork artwork = artworkRepository.findById(artworkId)
                .orElseThrow(() -> new IllegalArgumentException("작품이 존재하지 않습니다."));

        String path = fileUploadService.saveFile(file, "explanations");
        artwork.setExplanationFileUrl(path);
        artworkRepository.save(artwork);
    }

    // ✅ 전체 작품 조회 (DTO 변환)
    public List<ArtworkDto> getAllArtworks() {
        return artworkRepository.findAll().stream().map(this::toDto).collect(Collectors.toList());
    }

    // 관리자 업로드 작품(명화) 조회
    public List<ArtworkDto> getArtworksByAdmin() {
        List<Artwork> artworks = artworkRepository.findMasterpieces();
        return artworks.stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    private ArtworkDto toDto(Artwork a) {
        return ArtworkDto.builder()
                .id(a.getId())
                .title(a.getTitle())
                .description(a.getDescription())
                .imageUrl(a.getImageUrl())
                .build();
    }
}
