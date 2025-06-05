package com.artrithm.backendapi.repository;

import com.artrithm.backendapi.model.UserPromotionRequest;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface UserPromotionRequestRepository extends JpaRepository<UserPromotionRequest, Long> {
    boolean existsByUserIdAndApprovedFalse(Long userId);
    List<UserPromotionRequest> findAllByApprovedFalse();
}