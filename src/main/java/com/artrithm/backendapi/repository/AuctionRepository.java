package com.artrithm.backendapi.repository;

import com.artrithm.backendapi.model.Auction;
import com.artrithm.backendapi.model.AuctionStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface AuctionRepository extends JpaRepository<Auction, Long> {

    List<Auction> findByStatus(AuctionStatus status);

    boolean existsByStatus(AuctionStatus status);

    Optional<Auction> findFirstByStatus(AuctionStatus status);

    Optional<Auction> findByArtworkId(Long artworkId);

    Optional<Auction> findFirstByStatusOrderByStartTimeDesc(AuctionStatus auctionStatus);

    Optional<Auction> findFirstByStatusOrderByEndTimeDesc(AuctionStatus status);

    List<Auction> findAllByWinnerUserId(String winnerUserId);
}
