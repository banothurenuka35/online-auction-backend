package com.auction.onlineauction.service;

import com.auction.onlineauction.entity.Auction;
import com.auction.onlineauction.entity.Payment;
import com.auction.onlineauction.repository.AuctionRepository;
import com.auction.onlineauction.repository.PaymentRepository;

import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final AuctionRepository auctionRepository;
    private final NotificationService notificationService;

    public PaymentService(
            PaymentRepository paymentRepository,
            AuctionRepository auctionRepository,
            NotificationService notificationService) {

        this.paymentRepository = paymentRepository;
        this.auctionRepository = auctionRepository;
        this.notificationService = notificationService;
    }

    public Payment createPayment(Payment payment) {

        Auction auction = auctionRepository
                .findById(payment.getAuctionId())
                .orElseThrow(() ->
                        new RuntimeException("Auction not found"));

        if (!"CLOSED".equalsIgnoreCase(auction.getStatus())) {
            throw new RuntimeException(
                    "Payment is allowed only after auction is closed");
        }

        if (auction.getWinnerId() == null) {
            throw new RuntimeException(
                    "No winner exists for this auction");
        }

        if (!auction.getWinnerId().equals(payment.getUserId())) {
            throw new RuntimeException(
                    "Only the auction winner can make the payment");
        }

        if (payment.getAmount() == null
                || !payment.getAmount().equals(auction.getCurrentBid())) {

            throw new RuntimeException(
                    "Payment amount must match the winning bid");
        }

        payment.setStatus("PENDING");
        payment.setPaymentDate(LocalDateTime.now());

        return paymentRepository.save(payment);
    }

    public List<Payment> getAllPayments() {
        return paymentRepository.findAll();
    }

    public Optional<Payment> getPaymentById(Long id) {
        return paymentRepository.findById(id);
    }

    public List<Payment> getPaymentsByUser(Long userId) {
        return paymentRepository.findByUserId(userId);
    }

    public List<Payment> getPaymentsByAuction(Long auctionId) {
        return paymentRepository.findByAuctionId(auctionId);
    }

    public Payment updatePaymentStatus(Long id, String status) {

        Payment payment = paymentRepository.findById(id)
                .orElseThrow(() ->
                        new RuntimeException("Payment not found"));

        payment.setStatus(status);

        Payment updatedPayment =
                paymentRepository.save(payment);

        // Send payment confirmation after successful payment
        if ("SUCCESS".equalsIgnoreCase(status)) {

            notificationService.createNotification(
                    payment.getUserId(),
                    "Payment successful for auction "
                            + payment.getAuctionId()
                            + ". Amount paid: "
                            + payment.getAmount(),
                    "PAYMENT_CONFIRMATION"
            );
        }

        return updatedPayment;
    }
}