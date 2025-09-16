package com.artrithm.backendapi.repository;

import com.artrithm.backendapi.model.FixedPriceSale;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface FixedPriceSaleRepository extends JpaRepository<FixedPriceSale, Long> {

    // ✅ 구매자 ID 기준 구매 이력 조회
    List<FixedPriceSale> findAllByBuyer_Id(Long buyerId);

    // ✅ 판매자 ID 기준 판매 이력 조회
    List<FixedPriceSale> findAllBySeller_Id(Long sellerId);

    // ✅ 작품 및 등록 사용자 함께 조회
    @Query("SELECT s FROM FixedPriceSale s " +
            "JOIN FETCH s.artwork a " +
            "JOIN FETCH a.user u")
    List<FixedPriceSale> findAllWithArtworkAndUser();

    // ✅ 단일 작품 기준 조회
    Optional<FixedPriceSale> findByArtworkId(Long artworkId);
}
