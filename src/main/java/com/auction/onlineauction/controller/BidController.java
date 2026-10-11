package com.auction.onlineauction.controller;

import com.auction.onlineauction.entity.Bid;
import com.auction.onlineauction.service.BidService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/bids")
public class BidController {

    private final BidService bidService;

    public BidController(BidService bidService) {
        this.bidService = bidService;
    }

    @PostMapping
    public ResponseEntity<Bid> placeBid(@RequestBody Bid bid) {
        return ResponseEntity.ok(bidService.placeBid(bid));
    }

    @GetMapping
    public ResponseEntity<List<Bid>> getAllBids() {
        return ResponseEntity.ok(bidService.getAllBids());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Bid> getBidById(@PathVariable Long id) {
        return bidService.getBidById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/auction/{auctionId}")
    public ResponseEntity<List<Bid>> getBidsByAuction(
            @PathVariable Long auctionId) {
        return ResponseEntity.ok(
                bidService.getBidsByAuction(auctionId));
    }

    @GetMapping("/bidder/{bidderId}")
    public ResponseEntity<List<Bid>> getBidsByBidder(
            @PathVariable Long bidderId) {
        return ResponseEntity.ok(
                bidService.getBidsByBidder(bidderId));
    }
}