package com.artrithm.backendapi.repository;

import com.artrithm.backendapi.model.Exhibition;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ExhibitionRepository extends JpaRepository<Exhibition, Long> {

    // 특정 작가(사용자)의 전시 목록 조회
    List<Exhibition> findByAuthorId(Long authorId);

    // 조건에 맞는 작가 전시 목록 조회
    List<Exhibition> findByAuthorIdIn(List<Long> authorIds);
}
