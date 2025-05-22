package com.artrithm.backendapi.repository;

import com.artrithm.backendapi.model.Artist;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ArtistRepository extends JpaRepository<Artist, Long> {
    // 필요 시 이름으로 작가 검색 가능
    boolean existsByName(String name);
}
