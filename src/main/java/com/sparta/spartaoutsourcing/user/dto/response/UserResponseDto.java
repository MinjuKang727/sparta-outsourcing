package com.sparta.spartaoutsourcing.user.dto.response;

import com.sparta.spartaoutsourcing.user.entity.User;
import com.sparta.spartaoutsourcing.user.enums.UserRole;
import lombok.Getter;

@Getter
public class UserResponseDto {
    private final Long id;
    private final String email;
    private final String username;
    private final UserRole role;

    public UserResponseDto(User user) {
        this.id = user.getId();
        this.email = user.getEmail();
        this.username = user.getUsername();
        this.role = user.getRole();
    }
}
