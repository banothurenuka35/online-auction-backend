package com.auction.onlineauction.controller;

import com.auction.onlineauction.entity.Notification;
import com.auction.onlineauction.service.NotificationService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/notifications")
public class NotificationController {

    private final NotificationService notificationService;

    public NotificationController(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    @PostMapping
    public ResponseEntity<Notification> createNotification(
            @RequestParam Long userId,
            @RequestParam String message,
            @RequestParam String type) {

        return ResponseEntity.ok(
                notificationService.createNotification(
                        userId, message, type
                )
        );
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<Notification>> getUserNotifications(
            @PathVariable Long userId) {

        return ResponseEntity.ok(
                notificationService.getUserNotifications(userId)
        );
    }

    @GetMapping
    public ResponseEntity<List<Notification>> getAllNotifications() {

        return ResponseEntity.ok(
                notificationService.getAllNotifications()
        );
    }

    @PutMapping("/{id}/read")
    public ResponseEntity<String> markAsRead(
            @PathVariable Long id) {

        notificationService.markAsRead(id);

        return ResponseEntity.ok("Notification marked as read");
    }
}