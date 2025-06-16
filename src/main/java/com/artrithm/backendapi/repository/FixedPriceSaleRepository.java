package com.artrithm.backendapi.repository;

import com.artrithm.backendapi.model.FixedPriceSale;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface FixedPriceSaleRepository extends JpaRepository<FixedPriceSale, Long> {

    // 필요 시 확장 예시:
    // Optional<FixedPriceSale> findByArtworkId(Long artworkId);
    @Query("SELECT s FROM FixedPriceSale s " +
            "JOIN FETCH s.artwork a " +
            "JOIN FETCH a.user u")
    List<FixedPriceSale> findAllWithArtworkAndUser();
    Optional<FixedPriceSale> findByArtworkId(Long artworkId);

}
