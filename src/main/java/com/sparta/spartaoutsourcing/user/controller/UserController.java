package com.sparta.spartaoutsourcing.user.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.sparta.spartaoutsourcing.auth.jwt.JwtUtil;
import com.sparta.spartaoutsourcing.auth.security.UserDetailsImpl;
import com.sparta.spartaoutsourcing.auth.token.TokenBlacklistService;
import com.sparta.spartaoutsourcing.user.dto.request.UserDeleteRequestDto;
import com.sparta.spartaoutsourcing.user.dto.request.UserSignupRequestDto;
import com.sparta.spartaoutsourcing.user.dto.response.UserResponseDto;
import com.sparta.spartaoutsourcing.user.exception.UserException;
import com.sparta.spartaoutsourcing.user.service.KakaoService;
import com.sparta.spartaoutsourcing.user.service.UserService;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.file.AccessDeniedException;

@Slf4j(topic = "UserController")
@Validated
@Controller
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;
    private final KakaoService kakaoService;
    private final TokenBlacklistService tokenBlacklistService;

    /**
     * 회원 가입
     * @param requestDto : 회원 가입 정보를 담은 객체
     * @return 회원 가입 결과
     */
    @PostMapping("/users/signup")
    public ResponseEntity<UserResponseDto> signup(@RequestHeader(value = JwtUtil.AUTHORIZATION_HEADER, required = false) String token, @RequestBody @Valid UserSignupRequestDto requestDto) throws UserException {
        log.info("::: 회원 가입 :::");

        if (token != null) {
            throw new UserException("회원 가입 실패", new AccessDeniedException("로그아웃 후에 회원 가입 해 주십시오."));
        }

        try {
            UserResponseDto responseDto = this.userService.signup(requestDto);
            String bearerToken = this.userService.createToken(responseDto);

            return ResponseEntity.status(HttpStatus.OK).header(JwtUtil.AUTHORIZATION_HEADER, bearerToken).body(responseDto);
        } catch (UnsupportedEncodingException e) {
            log.error("토큰 생성 에러 : {}", e.getMessage());

            throw new UserException("토큰 생성 실패", e);
        }
    }

    /**
     * 회원 탈퇴
     * @param userDetails : 인증 정보를 담은 객체
     * @param requestDto : 탈퇴할 계정 이메일과 비밀번호를 담은 객체
     * @return : 회원 탈퇴 결과
     */
    @DeleteMapping("/users")
    public ResponseEntity<String> deleteUser(@RequestHeader(JwtUtil.AUTHORIZATION_HEADER) String token, @AuthenticationPrincipal UserDetailsImpl userDetails, @RequestBody @Valid UserDeleteRequestDto requestDto) throws UserException, ServletException {
        log.info("::: 회원 탈퇴 :::");
        Boolean isDeleted = this.userService.deleteUser(userDetails.getUser(), requestDto);

        if (isDeleted) {
            this.tokenBlacklistService.addTokenToBlackList(token);
            return ResponseEntity.status(HttpStatus.OK).body("Delete ID Completed");
        }

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to Delete ID");
    }

    @GetMapping("/users/login/kakao")
    public void kakaoLogin(HttpServletResponse response) throws IOException {
        log.info("::: 카카오 로그인 :::");
        response.sendRedirect(this.kakaoService.kakaoLogin());
        response.setStatus(HttpStatus.FOUND.value());
    }

    /**
     * 카카오 로그인
     * @param code : 카카오 로그인 인가 코드
     * @return : (Header) JWT 토큰, (Body) 로그인된 회원 정보를 담은 Dto 객체
     * @throws JsonProcessingException : 카카오 로그인 인증 과정 중, JSON 파싱에서 발생 가능
     * @throws UnsupportedEncodingException : createToken() 메서드 실행 중 발생 가능
     */
    @GetMapping("/users/login/kakao/callback")
    public ResponseEntity<UserResponseDto> kakaoLoginCallback(@RequestParam String code) throws JsonProcessingException, UnsupportedEncodingException {
        log.info("::: 카카오 로그인 Callback :::");
        UserResponseDto responseDto = this.kakaoService.kakaoLoginCallback(code);
        String bearerToken = this.userService.createToken(responseDto);

        return ResponseEntity.status(HttpStatus.OK).header(JwtUtil.AUTHORIZATION_HEADER, bearerToken).body(responseDto);
    }
}
