package com.sparta.spartaoutsourcing.user.entity;

import com.sparta.spartaoutsourcing.user.dto.request.UserSignupRequestDto;
import com.sparta.spartaoutsourcing.user.enums.UserRole;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Long id;
    @Column(nullable = false, unique = true)
    private String email;
    @Column(nullable = false)
    private String password;
    @Column(nullable = false)
    private String username;
    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private UserRole role;
    private Boolean isDeleted = false;

    private Long kakaoId;

    public User(String email, String username, String password, UserRole role) {
        this.email = email;
        this.username = username;
        this.password = password;
        this.role = role;
    }

    public User(UserSignupRequestDto requestDto, String password, UserRole role) {
        this.email = requestDto.getEmail();
        this.username = requestDto.getUsername();
        this.password = password;
        this.role = role;
    }

    public User(String username, String password, String email, UserRole role, Long kakaoId) {
        this.username = username;
        this.password = password;
        this.email = email;
        this.role = role;
        this.kakaoId =kakaoId;
    }

    public void delete() {
        this.isDeleted = true;
    }

    public User kakaoIdUpdate(Long kakaoId) {
        this.kakaoId = kakaoId;
        return this;
    }
}
