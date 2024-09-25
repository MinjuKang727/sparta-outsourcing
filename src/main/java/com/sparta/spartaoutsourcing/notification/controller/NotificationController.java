package com.sparta.spartaoutsourcing.notification.controller;

import com.sparta.spartaoutsourcing.auth.security.UserDetailsImpl;
import com.sparta.spartaoutsourcing.notification.dto.NotificationResponseDto;
import com.sparta.spartaoutsourcing.notification.entity.Notification;
import com.sparta.spartaoutsourcing.notification.service.NotificationService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class NotificationController {
    private final NotificationService notificationService;

    public NotificationController(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    @GetMapping("/users/notifications")
    public ResponseEntity<List<NotificationResponseDto>> getNotifications(@AuthenticationPrincipal UserDetailsImpl userDetails) {
        List<Notification> notifications = notificationService.getNotifications(userDetails.getUser().getId());
        List<NotificationResponseDto> notificationResponseDtos = notifications.stream().map(v -> NotificationResponseDto.builder()
                        .id(v.getId())
                        .message(v.getMessage())
                        .createAt(v.getCreatedAt()).build())
                .toList();
        return ResponseEntity.ok(notificationResponseDtos);
    }
}
