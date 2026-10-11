package com.auction.onlineauction.repository;

import com.auction.onlineauction.entity.Bid;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface BidRepository extends JpaRepository<Bid, Long> {

    List<Bid> findByAuctionId(Long auctionId);

    List<Bid> findByBidderId(Long bidderId);

    Optional<Bid> findTopByAuctionIdOrderByAmountDesc(Long auctionId);
}