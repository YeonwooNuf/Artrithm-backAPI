package com.artrithm.backendapi.repository;

import com.artrithm.backendapi.model.UserLike;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UserLikeRepository extends JpaRepository<UserLike, Long> {

    // 사용자가 해당 전시를 좋아요했는지 여부
    boolean existsByUserIdAndExhibitionId(Long userId, Long exhibitionId);

    // 사용자가 누른 좋아요 엔티티 찾기 (삭제용)
    Optional<UserLike> findByUserIdAndExhibitionId(Long userId, Long exhibitionId);

    // 사용자 관심 전시 목록 조회
    List<UserLike> findAllByUserId(Long userId);
}
