package com.artrithm.backendapi.repository;

import com.artrithm.backendapi.model.Auction;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AuctionRepository extends JpaRepository<Auction, Long> {
}
