package com.auction.onlineauction.controller;

import com.auction.onlineauction.entity.*;
import com.auction.onlineauction.service.*;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin")
public class AdminController {

    private final UserService userService;
    private final ProductService productService;
    private final AuctionService auctionService;
    private final BidService bidService;
    private final PaymentService paymentService;
    private final NotificationService notificationService;

    public AdminController(
            UserService userService,
            ProductService productService,
            AuctionService auctionService,
            BidService bidService,
            PaymentService paymentService,
            NotificationService notificationService) {

        this.userService = userService;
        this.productService = productService;
        this.auctionService = auctionService;
        this.bidService = bidService;
        this.paymentService = paymentService;
        this.notificationService = notificationService;
    }

    @GetMapping("/users")
    public ResponseEntity<List<User>> getAllUsers() {
        return ResponseEntity.ok(
                userService.getAllUsers()
        );
    }

    @GetMapping("/products")
    public ResponseEntity<List<Product>> getAllProducts() {
        return ResponseEntity.ok(
                productService.getAllProducts()
        );
    }

    @GetMapping("/auctions")
    public ResponseEntity<List<Auction>> getAllAuctions() {
        return ResponseEntity.ok(
                auctionService.getAllAuctions()
        );
    }

    @GetMapping("/bids")
    public ResponseEntity<List<Bid>> getAllBids() {
        return ResponseEntity.ok(
                bidService.getAllBids()
        );
    }

    @GetMapping("/payments")
    public ResponseEntity<List<Payment>> getAllPayments() {
        return ResponseEntity.ok(
                paymentService.getAllPayments()
        );
    }

    @GetMapping("/notifications")
    public ResponseEntity<List<Notification>> getAllNotifications() {
        return ResponseEntity.ok(
                notificationService.getAllNotifications()
        );
    }
}