package com.sparta.spartaoutsourcing.domian.auth.token;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Date;

public interface TokenBlacklistRepository extends JpaRepository<Token, Long> {
    boolean existsByToken(String token);
    void deleteByExpirationTimeBefore(Date now);
}
