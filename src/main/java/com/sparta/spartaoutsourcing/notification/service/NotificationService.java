package com.sparta.spartaoutsourcing.notification.service;

import com.sparta.spartaoutsourcing.notification.entity.Notification;
import com.sparta.spartaoutsourcing.notification.repository.NotificationRepository;
import com.sparta.spartaoutsourcing.order.entity.OrderState;
import com.sparta.spartaoutsourcing.user.entity.User;
import com.sparta.spartaoutsourcing.user.repository.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class NotificationService {
    private final NotificationRepository notificationRepository;
    private final UserRepository userRepository;

    public NotificationService(NotificationRepository notificationRepository, UserRepository userRepository) {
        this.notificationRepository = notificationRepository;
        this.userRepository = userRepository;
    }

    public List<Notification> getNotifications(Long userId) {
        return notificationRepository.findAllByUser_IdOrderByCreatedAtDesc(userId);
    }

    private Notification createNotification(Long userId, String message) {
        User user = userRepository.findById(userId).orElseThrow(() -> new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY, "해당하는 유저가 존재하지 않습니다."));
        Notification notification = Notification.builder().message(message).user(user).build();
        return notificationRepository.save(notification);
    }

    public Notification createOrderNotification(Long userId, OrderState orderState) {
        String message = switch (orderState) {
            case DELIVERED -> "배달 완료되었습니다.";
            case DELIVERING -> "배달이 시작되었습니다.";
            case ACCEPT_ORDER -> "주문이 수락되었습니다.";
            case REJECT_ORDER -> "주문이 거절되었습니다.";
            case REQUEST_ORDER -> "주문이 접수되었습니다.";
        };

        return createNotification(userId, message);
    }
}
