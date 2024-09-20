package com.sparta.spartaoutsourcing.auth.jwt;


import com.sparta.spartaoutsourcing.auth.security.UserDetailsServiceImpl;
import com.sparta.spartaoutsourcing.auth.token.TokenBlacklistService;
import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Slf4j(topic = "JWT 검증 및 인가")

public class JwtAuthorizationFilter extends OncePerRequestFilter {
    private  final JwtUtil jwtUtil;
    private final UserDetailsServiceImpl userDetailsService;
    private final TokenBlacklistService tokenBlacklistService;

    public JwtAuthorizationFilter(JwtUtil jwtUtil, UserDetailsServiceImpl userDetailsService, TokenBlacklistService tokenBlacklistService) {
        this.jwtUtil = jwtUtil;
        this.userDetailsService = userDetailsService;
        this.tokenBlacklistService = tokenBlacklistService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String bearerToken = request.getHeader(JwtUtil.AUTHORIZATION_HEADER);

        if (StringUtils.hasText(bearerToken)) {
            bearerToken = jwtUtil.getDecodedToken(bearerToken);
            String token = jwtUtil.substringToken(bearerToken);
            if (!this.tokenBlacklistService.isTokenBlackListed(token)) {
                jwtUtil.validateToken(token);

                Claims info = jwtUtil.getUserInfoFromToken(token);

                try {
                    this.setAuthentication(info.getSubject(), response);
                } catch (ServletException e) {
                    response.setStatus(404);
                    response.getWriter().write(e.getMessage());
                }
            } else {
                response.setStatus(401);
                response.getWriter().write("만료된 토큰입니다.");
            }
        }

        filterChain.doFilter(request, response);
    }

    private void setAuthentication(String email, HttpServletResponse response) throws ServletException {
        SecurityContext context = SecurityContextHolder.createEmptyContext();
        try {
            Authentication authentication = this.createAuthentication(email, response);
            context.setAuthentication(authentication);

            SecurityContextHolder.setContext(context);
        } catch (UsernameNotFoundException e) {
            throw new ServletException(e.getMessage());
        }

    }

    /**
     * 인증 객체 생성
     * @param email : 유저 ID(이메일 주소)
     * @return 인증 객체
     */
    private Authentication createAuthentication(String email, HttpServletResponse response) throws UsernameNotFoundException {
        try {
            UserDetails userDetails = this.userDetailsService.loadUserByUsername(email);
            return new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
        } catch (UsernameNotFoundException e) {
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            throw new UsernameNotFoundException(e.getMessage());
        }
    }
}

