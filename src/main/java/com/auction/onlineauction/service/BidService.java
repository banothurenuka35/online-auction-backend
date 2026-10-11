package com.auction.onlineauction.service;

import com.auction.onlineauction.entity.Auction;
import com.auction.onlineauction.entity.Bid;
import com.auction.onlineauction.repository.AuctionRepository;
import com.auction.onlineauction.repository.BidRepository;

import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class BidService {

    private final BidRepository bidRepository;
    private final AuctionRepository auctionRepository;
    private final NotificationService notificationService;

    public BidService(
            BidRepository bidRepository,
            AuctionRepository auctionRepository,
            NotificationService notificationService) {

        this.bidRepository = bidRepository;
        this.auctionRepository = auctionRepository;
        this.notificationService = notificationService;
    }

    public Bid placeBid(Bid bid) {

        Auction auction = auctionRepository
                .findById(bid.getAuctionId())
                .orElseThrow(() ->
                        new RuntimeException("Auction not found"));

        if (!"ACTIVE".equalsIgnoreCase(auction.getStatus())) {
            throw new RuntimeException("Auction is not active");
        }

        double currentBid = auction.getCurrentBid();
        double minimumIncrement = auction.getMinimumBidIncrement();
        double minimumAllowedBid = currentBid + minimumIncrement;

        if (bid.getAmount() < minimumAllowedBid) {
            throw new RuntimeException(
                    "Bid must be at least " + minimumAllowedBid);
        }

        // Find the previous highest bidder
        Optional<Bid> previousHighestBid =
                bidRepository
                        .findTopByAuctionIdOrderByAmountDesc(
                                auction.getId());

        // Save the new bid
        Bid savedBid = bidRepository.save(bid);

        // Update current auction bid
        auction.setCurrentBid(bid.getAmount());
        auctionRepository.save(auction);

        // Send outbid notification to previous highest bidder
        if (previousHighestBid.isPresent()
                && !previousHighestBid.get()
                        .getBidderId()
                        .equals(bid.getBidderId())) {

            Long previousBidderId =
                    previousHighestBid.get().getBidderId();

            notificationService.createNotification(
                    previousBidderId,
                    "You have been outbid on auction "
                            + auction.getId()
                            + ". New highest bid is "
                            + bid.getAmount(),
                    "OUTBID_ALERT"
            );
        }

        // Bid confirmation notification
        notificationService.createNotification(
                bid.getBidderId(),
                "Your bid of "
                        + bid.getAmount()
                        + " was placed successfully on auction "
                        + auction.getId(),
                "BID_CONFIRMATION"
        );

        return savedBid;
    }

    public List<Bid> getAllBids() {
        return bidRepository.findAll();
    }

    public Optional<Bid> getBidById(Long id) {
        return bidRepository.findById(id);
    }

    public List<Bid> getBidsByAuction(Long auctionId) {
        return bidRepository.findByAuctionId(auctionId);
    }

    public List<Bid> getBidsByBidder(Long bidderId) {
        return bidRepository.findByBidderId(bidderId);
    }
}