package com.artrithm.backendapi.repository;

import com.artrithm.backendapi.model.Artwork;
import com.artrithm.backendapi.model.SaleStatus;
import com.artrithm.backendapi.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ArtworkRepository extends JpaRepository<Artwork, Long> {

    @Query("SELECT a FROM Artwork a WHERE a.user IS NULL AND a.artist IS NOT NULL")
    List<Artwork> findMasterpieces();  // 명화만 조회
    List<Artwork> findByUserId(Long userId);
    List<Artwork> findByUserIdAndSaleStatusIsNull(Long userId);
    List<Artwork> findByUserIdAndSaleStatus(Long userId, SaleStatus saleStatus);

}
