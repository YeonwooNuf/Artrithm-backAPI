package com.artrithm.backendapi.service;

import com.artrithm.backendapi.dto.UserPromotionRequestDto;
import com.artrithm.backendapi.model.User;
import com.artrithm.backendapi.model.UserPromotionRequest;
import com.artrithm.backendapi.repository.UserPromotionRequestRepository;
import com.artrithm.backendapi.repository.UserRepository;
import com.artrithm.backendapi.service.FileUploadService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PromotionService {

    private final UserRepository userRepository;
    private final UserPromotionRequestRepository requestRepository;
    private final FileUploadService fileUploadService;

    public void submitRequest(Long userId, String reason, List<MultipartFile> artworkFiles) throws IOException {
        if (artworkFiles.size() != 4) {
            throw new IllegalArgumentException("대표작은 정확히 4개를 첨부해야 합니다.");
        }

        if (requestRepository.existsByUserIdAndApprovedFalse(userId)) {
            throw new IllegalStateException("이미 대기 중인 요청이 존재합니다.");
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NoSuchElementException("사용자를 찾을 수 없습니다."));

        List<String> savedPaths = new ArrayList<>();
        for (MultipartFile file : artworkFiles) {
            String imageUrl = fileUploadService.saveFile(file, "promotion-requests");
            savedPaths.add(imageUrl);
        }

        UserPromotionRequest request = new UserPromotionRequest();
        request.setUser(user);
        request.setReason(reason);
        request.setArtworkImagePaths(savedPaths);
        request.setApproved(false);
        request.setCreatedAt(LocalDateTime.now());

        requestRepository.save(request);
    }

    public List<UserPromotionRequestDto> getAllPendingRequests() {
        return requestRepository.findAllByApprovedFalse().stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    public void approveRequest(Long requestId) {
        UserPromotionRequest req = requestRepository.findById(requestId)
                .orElseThrow(() -> new NoSuchElementException("요청이 존재하지 않습니다."));

        User user = req.getUser();
        user.setRole(User.Role.ARTIST); // enum으로 관리 중일 경우
        req.setApproved(true);

        userRepository.save(user);
        requestRepository.save(req);
    }

    private UserPromotionRequestDto toDto(UserPromotionRequest req) {
        return UserPromotionRequestDto.builder()
                .requestId(req.getId())
                .userId(req.getUser().getId())
                .nickname(req.getUser().getNickname())
                .reason(req.getReason())
                .artworkImageUrls(req.getArtworkImagePaths())
                .approved(req.isApproved())
                .createdAt(req.getCreatedAt())
                .build();
    }
}
