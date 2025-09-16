package com.artrithm.backendapi.repository;

import com.artrithm.backendapi.model.AuctionRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AuctionRequestRepository extends JpaRepository<AuctionRequest, Long> {
    List<AuctionRequest> findByApproved(boolean approved);
}

