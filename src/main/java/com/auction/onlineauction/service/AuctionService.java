
package com.auction.onlineauction.service;

import com.auction.onlineauction.entity.Auction;
import com.auction.onlineauction.entity.Bid;
import com.auction.onlineauction.entity.Product;
import com.auction.onlineauction.repository.AuctionRepository;
import com.auction.onlineauction.repository.BidRepository;
import com.auction.onlineauction.repository.ProductRepository;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
public class AuctionService {

    private final AuctionRepository auctionRepository;
    private final BidRepository bidRepository;
    private final ProductRepository productRepository;
    private final NotificationService notificationService;

    public AuctionService(
            AuctionRepository auctionRepository,
            BidRepository bidRepository,
            ProductRepository productRepository,
            NotificationService notificationService) {

        this.auctionRepository = auctionRepository;
        this.bidRepository = bidRepository;
        this.productRepository = productRepository;
        this.notificationService = notificationService;
    }

    public Auction createAuction(Auction auction) {
        return auctionRepository.save(auction);
    }

    public List<Auction> getAllAuctions() {
        return auctionRepository.findAll();
    }

    public Optional<Auction> getAuctionById(Long id) {
        return auctionRepository.findById(id);
    }

    public Auction updateAuction(Auction auction) {
        return auctionRepository.save(auction);
    }

    // Close auction and select the highest bidder
    public Auction closeAuctionNow(Long id) {

        Auction auction = auctionRepository.findById(id)
                .orElseThrow(() ->
                        new RuntimeException("Auction not found"));

        if ("CLOSED".equalsIgnoreCase(auction.getStatus())) {
            return auction;
        }

        Optional<Bid> highestBid =
                bidRepository.findTopByAuctionIdOrderByAmountDesc(id);

        if (highestBid.isPresent()) {
            Bid winningBid = highestBid.get();

            auction.setWinnerId(winningBid.getBidderId());
            auction.setCurrentBid(winningBid.getAmount());

            notificationService.createNotification(
                    winningBid.getBidderId(),
                    "Congratulations! You won auction "
                            + auction.getId()
                            + " with a bid of "
                            + winningBid.getAmount(),
                    "WINNER_ANNOUNCEMENT"
            );

            Optional<Product> product =
                    productRepository.findById(auction.getProductId());

            if (product.isPresent()
                    && product.get().getSellerId() != null) {

                notificationService.createNotification(
                        product.get().getSellerId(),
                        "Your auction " + auction.getId()
                                + " has ended. Winning bid: "
                                + winningBid.getAmount(),
                        "AUCTION_SOLD"
                );
            }
        } else {
            auction.setWinnerId(null);
        }

        auction.setStatus("CLOSED");

        return auctionRepository.save(auction);
    }

    @Scheduled(fixedRate = 30000)
    public void checkAuctionEndingSoon() {

        List<Auction> auctions = auctionRepository.findAll();
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime fiveMinutesLater = now.plusMinutes(5);

        for (Auction auction : auctions) {

            if ("ACTIVE".equalsIgnoreCase(auction.getStatus())
                    && auction.getEndTime() != null
                    && auction.getEndTime().isAfter(now)
                    && auction.getEndTime().isBefore(fiveMinutesLater)) {

                List<Bid> bids =
                        bidRepository.findByAuctionId(auction.getId());

                Set<Long> notifiedUsers = new HashSet<>();

                for (Bid bid : bids) {
                    Long userId = bid.getBidderId();

                    if (notifiedUsers.add(userId)) {
                        notificationService.createNotification(
                                userId,
                                "Auction " + auction.getId()
                                        + " is ending soon. Place your final bid!",
                                "AUCTION_ENDING_SOON"
                        );
                    }
                }
            }
        }
    }

    @Scheduled(fixedRate = 30000)
    public void closeExpiredAuctions() {

        List<Auction> auctions = auctionRepository.findAll();
        LocalDateTime now = LocalDateTime.now();

        for (Auction auction : auctions) {

            if ("ACTIVE".equalsIgnoreCase(auction.getStatus())
                    && auction.getEndTime() != null
                    && !auction.getEndTime().isAfter(now)) {

                closeAuctionNow(auction.getId());

                System.out.println(
                        "Auction " + auction.getId()
                                + " closed automatically.");
            }
        }
    }
}
