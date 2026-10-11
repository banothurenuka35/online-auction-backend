package com.auction.onlineauction.controller;

import com.auction.onlineauction.entity.Payment;
import com.auction.onlineauction.service.PaymentService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/payments")
public class PaymentController {

    private final PaymentService paymentService;

    public PaymentController(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    @PostMapping
    public ResponseEntity<Payment> createPayment(
            @RequestBody Payment payment) {

        return ResponseEntity.ok(
                paymentService.createPayment(payment)
        );
    }

    @GetMapping
    public ResponseEntity<List<Payment>> getAllPayments() {

        return ResponseEntity.ok(
                paymentService.getAllPayments()
        );
    }

    @GetMapping("/{id}")
    public ResponseEntity<Payment> getPaymentById(
            @PathVariable Long id) {

        return paymentService.getPaymentById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<Payment>> getPaymentsByUser(
            @PathVariable Long userId) {

        return ResponseEntity.ok(
                paymentService.getPaymentsByUser(userId)
        );
    }

    @GetMapping("/auction/{auctionId}")
    public ResponseEntity<List<Payment>> getPaymentsByAuction(
            @PathVariable Long auctionId) {

        return ResponseEntity.ok(
                paymentService.getPaymentsByAuction(auctionId)
        );
    }

    @PutMapping("/{id}/status")
    public ResponseEntity<Payment> updatePaymentStatus(
            @PathVariable Long id,
            @RequestParam String status) {

        return ResponseEntity.ok(
                paymentService.updatePaymentStatus(id, status)
        );
    }
}