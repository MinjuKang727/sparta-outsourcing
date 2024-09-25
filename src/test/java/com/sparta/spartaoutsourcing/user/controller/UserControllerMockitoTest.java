package com.sparta.spartaoutsourcing.user.controller;

import com.sparta.spartaoutsourcing.auth.jwt.JwtUtil;
import com.sparta.spartaoutsourcing.auth.token.TokenBlacklistService;
import com.sparta.spartaoutsourcing.user.controller.UserController;
import com.sparta.spartaoutsourcing.user.dto.request.UserSignupRequestDto;
import com.sparta.spartaoutsourcing.user.dto.response.UserResponseDto;
import com.sparta.spartaoutsourcing.user.service.KakaoService;
import com.sparta.spartaoutsourcing.user.service.UserService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class UserControllerMockitoTest {
    @InjectMocks
    private UserController userController;

    @Mock
    private UserService userService;
    @Mock
    private KakaoService kakaoService;
    @Mock
    private TokenBlacklistService tokenBlacklistService;

    private UserSignupRequestDto signupRequestDto;
    private UserResponseDto userResponseDto;

    @Test
    void signup_Success() throws Exception {
        // given
        String token = null;  // 비로그인 상태
        when(userService.signup(signupRequestDto)).thenReturn(userResponseDto);
        when(userService.createToken(userResponseDto)).thenReturn("Bearer test_token");

        // when
        ResponseEntity<UserResponseDto> responseEntity = userController.signup(token, signupRequestDto);

        // then
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());  // 상태 코드 200 확인
        assertEquals(userResponseDto, responseEntity.getBody());  // 반환된 회원 정보 확인
        assertEquals("Bearer test_token", responseEntity.getHeaders().get(JwtUtil.AUTHORIZATION_HEADER).get(0));  // 토큰 확인
    }
}
