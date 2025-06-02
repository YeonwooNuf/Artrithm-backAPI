package com.artrithm.backendapi.repository;

import com.artrithm.backendapi.model.AuctionBid;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AuctionBidRepository extends JpaRepository<AuctionBid, Long> {
}
