package com.auction.onlineauction.service;

import com.auction.onlineauction.entity.Notification;
import com.auction.onlineauction.repository.NotificationRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class NotificationService {

    private final NotificationRepository notificationRepository;

    public NotificationService(NotificationRepository notificationRepository) {
        this.notificationRepository = notificationRepository;
    }

    public Notification createNotification(
            Long userId,
            String message,
            String type) {

        // Prevent duplicate auction-ending notifications
        if ("AUCTION_ENDING_SOON".equals(type)
                && notificationRepository
                .existsByUserIdAndTypeAndMessage(
                        userId,
                        type,
                        message)) {

            return null;
        }

        Notification notification = new Notification();

        notification.setUserId(userId);
        notification.setMessage(message);
        notification.setType(type);
        notification.setReadStatus(false);
        notification.setCreatedAt(LocalDateTime.now());

        return notificationRepository.save(notification);
    }

    public List<Notification> getUserNotifications(Long userId) {
        return notificationRepository
                .findByUserIdOrderByCreatedAtDesc(userId);
    }

    public List<Notification> getAllNotifications() {
        return notificationRepository.findAll();
    }

    public void markAsRead(Long id) {
        notificationRepository.findById(id).ifPresent(notification -> {
            notification.setReadStatus(true);
            notificationRepository.save(notification);
        });
    }
}