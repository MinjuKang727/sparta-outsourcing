package com.sparta.spartaoutsourcing.auth.jwt;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sparta.spartaoutsourcing.auth.security.UserDetailsImpl;
import com.sparta.spartaoutsourcing.user.dto.request.UserLoginRequestDto;
import com.sparta.spartaoutsourcing.user.dto.response.UserResponseDto;
import com.sparta.spartaoutsourcing.user.enums.UserRole;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import java.io.IOException;
import java.nio.file.AccessDeniedException;
import java.util.Map;

@Slf4j(topic = "로그인 및 JWT 생성")
public class JwtAuthenticationFilter extends UsernamePasswordAuthenticationFilter {
    private final JwtUtil jwtUtil;
    private ObjectMapper objectMapper = new ObjectMapper();

    public JwtAuthenticationFilter(JwtUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
        setFilterProcessesUrl("/users/login");
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {
        log.info("::: 로그인 :::");
        String token = request.getHeader(JwtUtil.AUTHORIZATION_HEADER);

        if (token != null) {
            throw new RuntimeException("로그인 실패", new AccessDeniedException("이미 로그인 되어 있습니다. 로그아웃 후에 로그인 시도를 해 주십시오."));
        }

        try {
                UserLoginRequestDto requestDto = new ObjectMapper().readValue(request.getInputStream(), UserLoginRequestDto.class);

                return getAuthenticationManager().authenticate(
                        new UsernamePasswordAuthenticationToken(
                                requestDto.getEmail(),
                                requestDto.getPassword(),
                                null
                        )
                );
        } catch (IOException e) {
            log.error(e.getMessage());
            try {
                String errorMessage = objectMapper.writeValueAsString(Map.of(
                        "message", "로그인 실패",
                        "error", e.getMessage()
                ));
                throw new BadCredentialsException(errorMessage);
            } catch (JsonProcessingException ex) {
                throw new RuntimeException(ex);
            }

        }
    }

    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain, Authentication authResult) throws IOException, ServletException {
        log.info("로그인 성공 및 JWT 생성");

        Long userId = ((UserDetailsImpl) authResult.getPrincipal()).getUser().getId();
        String email = ((UserDetailsImpl) authResult.getPrincipal()).getEmail();
        String username = ((UserDetailsImpl) authResult.getPrincipal()).getUsername();
        UserRole role = ((UserDetailsImpl) authResult.getPrincipal()).getUser().getRole();

        String token = jwtUtil.createToken(userId, email, username, role);

        if (token != null) {
            UserResponseDto responseDto = new UserResponseDto(userId, email, username, role);
            response.addHeader(jwtUtil.AUTHORIZATION_HEADER, token);
            response.setStatus(200);
            response.getWriter().write(objectMapper.writeValueAsString(responseDto));
        } else {
            response.setStatus(500);
            response.getWriter().write("Create Token Error");
        }

    }

    @Override
    protected void unsuccessfulAuthentication(HttpServletRequest request, HttpServletResponse response, AuthenticationException failed) throws IOException, ServletException {
        log.info("로그인 실패");
        response.setStatus(401);
        response.getWriter().write("Failed to Login");
    }

}
