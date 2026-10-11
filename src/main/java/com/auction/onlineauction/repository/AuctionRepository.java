package com.auction.onlineauction.repository;

import com.auction.onlineauction.entity.Auction;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AuctionRepository extends JpaRepository<Auction, Long> {
}