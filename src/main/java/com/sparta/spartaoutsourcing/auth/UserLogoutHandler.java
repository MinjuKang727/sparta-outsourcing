package com.sparta.spartaoutsourcing.auth;

import com.sparta.spartaoutsourcing.auth.jwt.JwtUtil;
import com.sparta.spartaoutsourcing.auth.token.TokenBlacklistService;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.LogoutHandler;

@Slf4j(topic = "UserLogoutHandler")
public class UserLogoutHandler implements LogoutHandler {

    private final TokenBlacklistService tokenBlacklistService;

    public UserLogoutHandler(TokenBlacklistService tokenBlacklistService) {
        this.tokenBlacklistService = tokenBlacklistService;
    }

    @Override
    public void logout(HttpServletRequest request, HttpServletResponse response, Authentication authentication) {
        log.info("로그아웃 시도");
        try {
            String token = request.getHeader(JwtUtil.AUTHORIZATION_HEADER);
            if (token == null) {
                log.error("로그인 되어 있지않습니다.");
                return;
            }
            this.tokenBlacklistService.addTokenToBlackList(token);
            response.setHeader(JwtUtil.AUTHORIZATION_HEADER, null);
        } catch (ServletException e) {
            log.error("Logout Failed");
            response.setStatus(HttpStatus.BAD_REQUEST.value());
        }
    }
}
