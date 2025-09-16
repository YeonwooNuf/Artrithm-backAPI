package com.artrithm.backendapi.repository;

import com.artrithm.backendapi.model.Guestbook;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface GuestbookRepository extends JpaRepository<Guestbook, Long> {

    // ✅ 특정 전시에 대한 방명록 목록을 최신순으로 조회
    List<Guestbook> findByExhibitionIdOrderByCreatedAtDesc(Long exhibitionId);
}
