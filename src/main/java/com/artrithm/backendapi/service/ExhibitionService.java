package com.artrithm.backendapi.service;

import com.artrithm.backendapi.dto.ArtworkDto;
import com.artrithm.backendapi.dto.ExhibitionDto;
import com.artrithm.backendapi.model.Artwork;
import com.artrithm.backendapi.model.Exhibition;
import com.artrithm.backendapi.model.User;
import com.artrithm.backendapi.repository.ArtworkRepository;
import com.artrithm.backendapi.repository.ExhibitionRepository;
import com.artrithm.backendapi.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ExhibitionService {

    private final ExhibitionRepository exhibitionRepository;
    private final ArtworkRepository artworkRepository;
    private final UserRepository userRepository;
    private final FileUploadService fileUploadService;

    public void saveExhibition(MultipartHttpServletRequest request) throws IOException {
        Long authorId = Long.parseLong(request.getParameter("authorId"));
        User user = userRepository.findById(authorId)
                .orElseThrow(() -> new IllegalArgumentException("유효하지 않은 사용자입니다."));

        String title = request.getParameter("title");
        String description = request.getParameter("description");
        String theme = request.getParameter("theme");
        MultipartFile thumbnail = request.getFile("thumbnail");
        String thumbnailUrl = fileUploadService.saveFile(thumbnail, "thumbnails");

        List<String> keywords = new ArrayList<>();
        int kwIndex = 0;
        while (true) {
            String kw = request.getParameter("keywords[" + kwIndex + "]");
            if (kw == null) break;
            keywords.add(kw);
            kwIndex++;
        }

        Exhibition exhibition = Exhibition.builder()
                .author(user)
                .title(title)
                .description(description)
                .theme(theme)
                .thumbnailUrl(thumbnailUrl)
                .keywords(keywords)
                .build();

        exhibitionRepository.save(exhibition);

        int workIndex = 0;
        while (true) {
            String workTitle = request.getParameter("works[" + workIndex + "].title");
            if (workTitle == null) break;

            String workDesc = request.getParameter("works[" + workIndex + "].description");
            MultipartFile workImg = request.getFile("works[" + workIndex + "].image");
            String workImgUrl = fileUploadService.saveFile(workImg, "artworks");

            Artwork artwork = Artwork.builder()
                    .title(workTitle)
                    .description(workDesc)
                    .imageUrl(workImgUrl)
                    .exhibition(exhibition)
                    .build();

            artworkRepository.save(artwork);
            workIndex++;
        }
    }

    public List<ExhibitionDto> getAllExhibitions() {
        return exhibitionRepository.findAll().stream().map(this::toDto).collect(Collectors.toList());
    }

    public ExhibitionDto getExhibitionById(Long id) {
        Exhibition exhibition = exhibitionRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("전시를 찾을 수 없습니다."));
        return toDto(exhibition);
    }

    private ExhibitionDto toDto(Exhibition exhibition) {
        return ExhibitionDto.builder()
                .id(exhibition.getId())
                .authorId(exhibition.getAuthor().getId())
                .authorNickname(exhibition.getAuthor().getNickname())
                .title(exhibition.getTitle())
                .description(exhibition.getDescription())
                .theme(exhibition.getTheme())
                .thumbnailUrl(exhibition.getThumbnailUrl())
                .keywords(exhibition.getKeywords())
                .artworks(exhibition.getArtworks().stream().map(this::toArtworkDto).collect(Collectors.toList()))
                .build();
    }

    private ArtworkDto toArtworkDto(Artwork a) {
        return ArtworkDto.builder()
                .id(a.getId())
                .title(a.getTitle())
                .description(a.getDescription())
                .imageUrl(a.getImageUrl())
                .build();
    }
}
