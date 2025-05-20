package com.artrithm.backendapi.repository;

import com.artrithm.backendapi.model.Artwork;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ArtworkRepository extends JpaRepository<Artwork, Long> {

    // 특정 전시에 소속된 작품 목록 조회
    List<Artwork> findByExhibitionId(Long exhibitionId);
}
