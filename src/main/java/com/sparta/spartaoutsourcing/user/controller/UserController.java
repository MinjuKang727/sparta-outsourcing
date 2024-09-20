package com.sparta.spartaoutsourcing.user.controller;

import com.sparta.spartaoutsourcing.domian.auth.jwt.JwtUtil;
import com.sparta.spartaoutsourcing.domian.auth.security.UserDetailsImpl;
import com.sparta.spartaoutsourcing.domian.auth.token.TokenBlacklistService;
import com.sparta.spartaoutsourcing.user.dto.request.UserDeleteRequestDto;
import com.sparta.spartaoutsourcing.user.dto.request.UserSignupRequestDto;
import com.sparta.spartaoutsourcing.user.dto.response.UserResponseDto;
import com.sparta.spartaoutsourcing.user.exception.UserException;
import com.sparta.spartaoutsourcing.user.service.UserService;
import jakarta.servlet.ServletException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.io.UnsupportedEncodingException;

@Slf4j(topic = "UserController")
@Validated
@RestController
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;
    private final TokenBlacklistService tokenBlacklistService;


    /**
     * 회원 가입
     * @param requestDto : 회원 가입 정보를 담은 객체
     * @return 회원 가입 결과
     */
    @PostMapping("/users/signup")
    public ResponseEntity<UserResponseDto> signup(@RequestHeader(value = JwtUtil.AUTHORIZATION_HEADER, required = false) String token, @RequestBody @Valid UserSignupRequestDto requestDto) throws UserException {
        log.info(":::회원 가입:::");
        try {
            UserResponseDto responseDto = this.userService.signup(requestDto);
            String bearerToken = this.userService.createToken(responseDto);

            if (token != null) {
                this.tokenBlacklistService.addTokenToBlackList(token);
            }

            return ResponseEntity.status(HttpStatus.OK).header(JwtUtil.AUTHORIZATION_HEADER, bearerToken).body(responseDto);
        } catch (UnsupportedEncodingException | ServletException e) {
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
        log.info(":::회원 탈퇴:::");
        this.userService.deleteUser(userDetails.getUser(), requestDto);

        this.tokenBlacklistService.addTokenToBlackList(token);

        return ResponseEntity.status(HttpStatus.OK).body("회원 탈퇴 성공");
    }

}
