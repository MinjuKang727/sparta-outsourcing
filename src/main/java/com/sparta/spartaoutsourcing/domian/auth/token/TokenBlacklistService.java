package com.sparta.spartaoutsourcing.domian.auth.token;

import com.sparta.spartaoutsourcing.domian.auth.jwt.JwtUtil;
import jakarta.servlet.ServletException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;

@Slf4j(topic = "TokenBlacklistService")
@Service
@Transactional(readOnly = true)
public class TokenBlacklistService {
    private final TokenBlacklistRepository tokenBlacklistRepository;
    private final JwtUtil jwtUtil;

    public TokenBlacklistService(TokenBlacklistRepository tokenBlacklistRepository, JwtUtil jwtUtil) {
        this.tokenBlacklistRepository = tokenBlacklistRepository;
        this.jwtUtil = jwtUtil;
    }

    @Transactional
    public void addTokenToBlackList(String token) throws ServletException {
        log.info("토큰 블랙리스트에 추가");

        if (token != null) {
            token = jwtUtil.getDecodedToken(token);
            token = jwtUtil.substringToken(token);

            Date expirationTime = jwtUtil.getExpirationTime(token);
            Token expiredToken = new Token(token, expirationTime);
            this.tokenBlacklistRepository.save(expiredToken);
        }
    }

    public boolean isTokenBlackListed(String token) {
        log.info("토큰 not 블랙리스트 검증");
        return this.tokenBlacklistRepository.existsByToken(token);
    }

    @Transactional
    public void removeTokenFromBlackList() {
        log.info("만료된 토큰 블랙리스트 정리");
        this.tokenBlacklistRepository.deleteByExpirationTimeBefore(new Date());
    }
}
