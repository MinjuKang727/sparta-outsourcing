package com.sparta.spartaoutsourcing.user.repository;

import com.sparta.spartaoutsourcing.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findUserByEmailAndIsDeleted(String email, Boolean isDeleted);
    Boolean existsByEmail(String email);

    Optional<User> findByKakaoId(Long kakaoId);
    Optional<User> findByEmail(String kakaoEmail);
}
