package com.artrithm.backendapi.service;

import com.artrithm.backendapi.dto.ArtworkDto;
import com.artrithm.backendapi.model.Artwork;
import com.artrithm.backendapi.repository.ArtworkRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ArtworkService {

    private final ArtworkRepository artworkRepository;
    private final RestTemplate restTemplate = new RestTemplate();

    // ✅ 작품 설명 파일 업로드
    public void uploadExplanationFile(Long artworkId, MultipartFile file) throws IOException {
        Artwork artwork = artworkRepository.findById(artworkId)
                .orElseThrow(() -> new IllegalArgumentException("작품이 존재하지 않습니다."));

        // ✅ FastAPI 서버가 참조할 수 있도록 저장
        String targetDir = "../backend-ai/documents";
        java.nio.file.Files.createDirectories(java.nio.file.Paths.get(targetDir));
        String targetPath = targetDir + "/" + artworkId + ".pdf";
        java.nio.file.Files.write(java.nio.file.Paths.get(targetPath), file.getBytes());

        // ✅ FastAPI 서버에 PDF 전송 (멀티파트 POST)
        String fastapiUrl = "http://localhost:8000/api/artchat/upload";

        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
        body.add("artworkId", artworkId.toString());
        body.add("file", new ByteArrayResource(file.getBytes()) {
            @Override
            public String getFilename() {
                return artworkId + ".pdf";
            }
        });

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);

        HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(body, headers);
        restTemplate.postForEntity(fastapiUrl, requestEntity, String.class);

        // (선택) 설명 파일 경로를 DB에 저장
        artwork.setExplanationFileUrl(targetPath);  // 필요 없으면 생략
        artworkRepository.save(artwork);
    }

    public List<ArtworkDto> getAllArtworks() {
        return artworkRepository.findAll().stream().map(this::toDto).collect(Collectors.toList());
    }

    public List<ArtworkDto> getArtworksByAdmin() {
        List<Artwork> artworks = artworkRepository.findMasterpieces();
        return artworks.stream().map(this::toDto).collect(Collectors.toList());
    }

    private ArtworkDto toDto(Artwork a) {
        return ArtworkDto.builder()
                .id(a.getId())
                .title(a.getTitle())
                .description(a.getDescription())
                .imageUrl(a.getImageUrl())
                .build();
    }

    public List<ArtworkDto> getMyArtworks(Long userId) {
        return artworkRepository.findByUserIdAndSaleStatusIsNull(userId).stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    private ArtworkDto convertToDto(Artwork artwork){
        return ArtworkDto.builder()
                .id(artwork.getId())
                .title(artwork.getTitle())
                .imageUrl(artwork.getImageUrl())
                .build();
    }
}
