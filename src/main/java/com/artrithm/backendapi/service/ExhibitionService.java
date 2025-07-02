package com.artrithm.backendapi.service;

import com.artrithm.backendapi.dto.ExhibitionDto;
import com.artrithm.backendapi.model.*;
import com.artrithm.backendapi.repository.*;
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
    private final ArtistRepository artistRepository;
    private final KeywordRepository keywordRepository;

    public void updateExhibition(Long exhibitionId, MultipartHttpServletRequest request) throws IOException {
        Exhibition exhibition = exhibitionRepository.findById(exhibitionId)
                .orElseThrow(() -> new IllegalArgumentException("전시 없음"));

        Long requesterId = Long.parseLong(request.getParameter("authorId"));
        String role = request.getParameter("role");
        boolean isOwner = exhibition.getAuthor().getId().equals(requesterId);
        boolean isAdmin = "ADMIN".equals(role);

        exhibition.setTitle(request.getParameter("title"));
        exhibition.setDescription(request.getParameter("description"));
        exhibition.setTheme(request.getParameter("theme"));

        MultipartFile thumbnailFile = request.getFile("thumbnail");
        if (thumbnailFile != null && !thumbnailFile.isEmpty()) {
            String thumbnailUrl = fileUploadService.saveFile(thumbnailFile, "thumbnails");
            exhibition.setThumbnailUrl(thumbnailUrl);
        } else {
            String thumbnailUrl = request.getParameter("thumbnailUrl");
            if (thumbnailUrl != null) {
                exhibition.setThumbnailUrl(thumbnailUrl);
            }
        }

        List<Keyword> keywordEntities = new ArrayList<>();
        int kwIndex = 0;
        while (true) {
            String kw = request.getParameter("keywords[" + kwIndex + "]");
            if (kw == null) break;
            Keyword keyword = keywordRepository.findByName(kw)
                    .orElseGet(() -> keywordRepository.save(Keyword.builder().name(kw).build()));
            keywordEntities.add(keyword);
            kwIndex++;
        }
        exhibition.setKeywords(keywordEntities);

        if (isAdmin) {
            String artistIdStr = request.getParameter("artistId");
            if (artistIdStr != null && !artistIdStr.isEmpty()) {
                Long artistId = Long.parseLong(artistIdStr);
                Artist artist = artistRepository.findById(artistId)
                        .orElseThrow(() -> new IllegalArgumentException("작가 없음"));
                exhibition.setArtist(artist);
            }
        }

        exhibition.getArtworks().clear();

        int workIndex = 0;
        while (true) {
            String workTitle = request.getParameter("works[" + workIndex + "].title");
            if (workTitle == null) break;

            String workDesc = request.getParameter("works[" + workIndex + "].description");
            MultipartFile workImg = request.getFile("works[" + workIndex + "].image");
            String existingImageUrl = request.getParameter("works[" + workIndex + "].imageUrl");

            String workImgUrl;
            if (workImg != null && !workImg.isEmpty()) {
                workImgUrl = fileUploadService.saveFile(workImg, "artworks");
            } else {
                workImgUrl = existingImageUrl;
            }

            Artwork artwork = Artwork.builder()
                    .title(workTitle)
                    .description(workDesc)
                    .imageUrl(workImgUrl)
                    .exhibition(exhibition)
                    .build();

            exhibition.getArtworks().add(artwork);
            workIndex++;
        }

        exhibitionRepository.save(exhibition);
    }

    public void saveExhibition(MultipartHttpServletRequest request) throws IOException {
        Long authorId = Long.parseLong(request.getParameter("authorId"));
        User user = userRepository.findById(authorId)
                .orElseThrow(() -> new IllegalArgumentException("유효하지 않은 사용자입니다."));

        String title = request.getParameter("title");
        String description = request.getParameter("description");
        String theme = request.getParameter("theme");
        MultipartFile thumbnail = request.getFile("thumbnail");
        String thumbnailUrl = fileUploadService.saveFile(thumbnail, "thumbnails");

        List<Keyword> keywordEntities = new ArrayList<>();
        int kwIndex = 0;
        while (true) {
            String kw = request.getParameter("keywords[" + kwIndex + "]");
            if (kw == null) break;
            Keyword keyword = keywordRepository.findByName(kw)
                    .orElseGet(() -> keywordRepository.save(Keyword.builder().name(kw).build()));
            keywordEntities.add(keyword);
            kwIndex++;
        }

        Exhibition exhibition = Exhibition.builder()
                .author(user)
                .title(title)
                .description(description)
                .theme(theme)
                .thumbnailUrl(thumbnailUrl)
                .keywords(keywordEntities)
                .build();

        String artistIdStr = request.getParameter("artistId");
        if (artistIdStr != null && !artistIdStr.isEmpty()) {
            Long artistId = Long.parseLong(artistIdStr);
            Artist artist = artistRepository.findById(artistId)
                    .orElseThrow(() -> new IllegalArgumentException("작가 없음"));
            exhibition.setArtist(artist);
        }

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
                    .user(user)
                    .artist(exhibition.getArtist())
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

    public List<ExhibitionDto> getExhibitionsByAuthor(Long authorId) {
        List<Exhibition> exhibitions = exhibitionRepository.findByAuthorId(authorId);
        return exhibitions.stream()
                .map(ExhibitionDto::fromEntity)
                .collect(Collectors.toList());
    }

    public List<ExhibitionDto> getExhibitionsByIds(List<Long> ids) {
        return exhibitionRepository.findAllById(ids).stream()
                .map(ExhibitionDto::fromEntity)
                .collect(Collectors.toList());
    }

    // 구독 서비스에 가입한 작가의 전시만 필터링
    public List<ExhibitionDto> getExhibitionsBySubscribedArtists() {
        List<User> subscribedArtists = userRepository.findSubscribedArtists(); // isActive = true
        List<Long> artistIds = subscribedArtists.stream().map(User::getId).toList();

        return exhibitionRepository.findByAuthorIdIn(artistIds).stream()
                .map(ExhibitionDto::fromEntity)
                .toList();
    }

    private ExhibitionDto toDto(Exhibition exhibition) {
        return ExhibitionDto.fromEntity(exhibition);
    }
}
