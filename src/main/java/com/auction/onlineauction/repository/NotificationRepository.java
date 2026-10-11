package com.auction.onlineauction.repository;

import com.auction.onlineauction.entity.Notification;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface NotificationRepository
        extends JpaRepository<Notification, Long> {

    List<Notification> findByUserIdOrderByCreatedAtDesc(Long userId);

    boolean existsByUserIdAndTypeAndMessage(
            Long userId,
            String type,
            String message);
}