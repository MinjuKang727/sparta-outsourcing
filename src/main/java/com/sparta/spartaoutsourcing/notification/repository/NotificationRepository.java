package com.sparta.spartaoutsourcing.notification.repository;

import com.sparta.spartaoutsourcing.notification.entity.Notification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {
    List<Notification> findAllByUser_IdOrderByCreatedAtDesc(Long user_id);
}
