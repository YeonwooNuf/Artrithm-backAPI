package com.artrithm.backendapi.service;

import com.artrithm.backendapi.dto.ExhibitionDto;
import com.artrithm.backendapi.dto.UserLikeDto;
import com.artrithm.backendapi.model.Exhibition;
import com.artrithm.backendapi.model.User;
import com.artrithm.backendapi.model.UserLike;
import com.artrithm.backendapi.repository.ExhibitionRepository;
import com.artrithm.backendapi.repository.UserLikeRepository;
import com.artrithm.backendapi.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserLikeService {

    private final UserLikeRepository userLikeRepository;
    private final UserRepository userRepository;
    private final ExhibitionRepository exhibitionRepository;

    // 좋아요 여부 확인
    public UserLikeDto getLikeStatus(Long userId, Long exhibitionId) {
        boolean liked = userLikeRepository.existsByUserIdAndExhibitionId(userId, exhibitionId);
        return new UserLikeDto(userId, exhibitionId, liked);
    }

    // 좋아요 추가
    public void like(Long userId, Long exhibitionId) {
        if (userLikeRepository.existsByUserIdAndExhibitionId(userId, exhibitionId)) return;

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));
        Exhibition exhibition = exhibitionRepository.findById(exhibitionId)
                .orElseThrow(() -> new IllegalArgumentException("전시를 찾을 수 없습니다."));

        UserLike userLike = new UserLike(user, exhibition);
        userLikeRepository.save(userLike);
    }

    // 좋아요 취소
    public void unlike(Long userId, Long exhibitionId) {
        Optional<UserLike> like = userLikeRepository.findByUserIdAndExhibitionId(userId, exhibitionId);
        like.ifPresent(userLikeRepository::delete);
    }

    // 관심 목록 조회
    public List<ExhibitionDto> getLikedExhibitions(Long userId) {
        return userLikeRepository.findAllByUserId(userId).stream()
                .map(userLike -> ExhibitionDto.fromEntity(userLike.getExhibition()))
                .collect(Collectors.toList());
    }
}
