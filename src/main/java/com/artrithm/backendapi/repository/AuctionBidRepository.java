package com.artrithm.backendapi.repository;

import com.artrithm.backendapi.model.Auction;
import com.artrithm.backendapi.model.AuctionBid;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface AuctionBidRepository extends JpaRepository<AuctionBid, Auction> {
    Optional<AuctionBid> findByAuction_Id(Long auctionId);

    Optional<AuctionBid> findByAuction(Auction auction);
}