package com.auction.onlineauction.controller;

import com.auction.onlineauction.entity.Auction;
import com.auction.onlineauction.service.AuctionService;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/auctions")
public class AuctionController {

    private final AuctionService auctionService;

    public AuctionController(AuctionService auctionService) {
        this.auctionService = auctionService;
    }

    @PostMapping
    public ResponseEntity<Auction> createAuction(
            @RequestBody Auction auction) {

        return ResponseEntity.ok(
                auctionService.createAuction(auction)
        );
    }

    @GetMapping
    public ResponseEntity<List<Auction>> getAllAuctions() {

        return ResponseEntity.ok(
                auctionService.getAllAuctions()
        );
    }

    @GetMapping("/{id}")
    public ResponseEntity<Auction> getAuctionById(
            @PathVariable Long id) {

        return auctionService.getAuctionById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}")
    public ResponseEntity<Auction> updateAuction(
            @PathVariable Long id,
            @RequestBody Auction auction) {

        return auctionService.getAuctionById(id)
                .map(existingAuction -> {

                    existingAuction.setProductId(
                            auction.getProductId());

                    existingAuction.setStartTime(
                            auction.getStartTime());

                    existingAuction.setEndTime(
                            auction.getEndTime());

                    existingAuction.setMinimumBidIncrement(
                            auction.getMinimumBidIncrement());

                    existingAuction.setCurrentBid(
                            auction.getCurrentBid());

                    existingAuction.setStatus(
                            auction.getStatus());

                    existingAuction.setWinnerId(
                            auction.getWinnerId());

                    return ResponseEntity.ok(
                            auctionService.updateAuction(
                                    existingAuction));
                })
                .orElse(ResponseEntity.notFound().build());
    }
}