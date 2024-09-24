package com.sparta.spartaoutsourcing.notification.dto;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class NotificationResponseDto {
    Long id;
    String message;
    LocalDateTime createAt;

    @Builder
    private NotificationResponseDto(Long id, String message, LocalDateTime createAt) {
        this.id = id;
        this.message = message;
        this.createAt = createAt;
    }
}
