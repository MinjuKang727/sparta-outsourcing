package com.sparta.spartaoutsourcing.domian.user.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sparta.spartaoutsourcing.auth.JwtTestUtil;
import com.sparta.spartaoutsourcing.auth.jwt.JwtUtil;
import com.sparta.spartaoutsourcing.auth.security.UserDetailsImpl;
import com.sparta.spartaoutsourcing.auth.token.TokenBlacklistService;
import com.sparta.spartaoutsourcing.user.controller.UserController;
import com.sparta.spartaoutsourcing.user.dto.request.UserDeleteRequestDto;
import com.sparta.spartaoutsourcing.user.dto.request.UserSignupRequestDto;
import com.sparta.spartaoutsourcing.user.dto.response.UserResponseDto;
import com.sparta.spartaoutsourcing.user.entity.User;
import com.sparta.spartaoutsourcing.user.enums.UserRole;
import com.sparta.spartaoutsourcing.user.service.KakaoService;
import com.sparta.spartaoutsourcing.user.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.filter.CharacterEncodingFilter;

import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@AutoConfigureMockMvc(addFilters = false)  // 필터 없애는 설정
@WebMvcTest(UserController.class)
public class UserControllerNoFilterWebMvcTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private WebApplicationContext context;

    @MockBean
    private UserService userService;

    @MockBean
    private KakaoService kakaoService;

    @MockBean
    private TokenBlacklistService tokenBlacklistService;

    @MockBean
    private JwtUtil jwtUtil;


    @Value("${rest.api.key}")
    private String clientId;

    private ObjectMapper objectMapper;


    @BeforeEach
    void setup() {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(context)
                .addFilter(new CharacterEncodingFilter("UTF-8", true))
                .alwaysDo(print())
                .build();

        MockitoAnnotations.openMocks(this);
        ReflectionTestUtils.setField(kakaoService, "clientId", clientId);

        objectMapper = new ObjectMapper();
    }

    @Nested
    class SignupTest {
        @Test
        void 헤더에_토큰이_있어서_에러_발생() throws Exception {
            String token = "JWT 토큰";
            User user = new User("Rtan@sparta.com", "Rtan", "encodeddPassword", UserRole.USER);
            ReflectionTestUtils.setField(user, "id", 1L);
            UserDetailsImpl userDetails = new UserDetailsImpl(user);
            UserSignupRequestDto requestDto = new UserSignupRequestDto("Rtan", "Rtan@sparta.com", "password123!", false, null);


            // when
            ResultActions resultActions = mockMvc.perform(
                    post("/users/signup")
                            .header(JwtUtil.AUTHORIZATION_HEADER, token)
                            .content(objectMapper.writeValueAsString(requestDto))
                            .contentType("application/json")
                            .with(SecurityMockMvcRequestPostProcessors.user(userDetails))
            );

            // then
            String errorMessage = objectMapper.writeValueAsString(Map.of(
                    "message", "회원 가입 실패",
                    "error", "로그아웃 후에 회원 가입 해 주십시오."
            ));

            resultActions.andExpect(status().isBadRequest())
                    .andExpect(content().string(errorMessage));
        }

        @Test
        void 회원가입_성공() throws Exception {
            UserSignupRequestDto requestDto = new UserSignupRequestDto("Rtan", "Rtan@sparta.com", "password123!", false, null);
            ObjectMapper objectMapper = new ObjectMapper();
            String body = objectMapper.writeValueAsString(requestDto);

            User user = new User(requestDto, "encodedPassword", UserRole.USER);
            ReflectionTestUtils.setField(user, "id", 1L);

            UserResponseDto responseDto = new UserResponseDto(user);
            JwtTestUtil jwtTestUtil = new JwtTestUtil();
            String token = jwtTestUtil.createToken(user.getId(), user.getEmail(), user.getUsername(), user.getRole());

           when(userService.signup(any(UserSignupRequestDto.class))).thenReturn(responseDto);
           when(userService.createToken(any(UserResponseDto.class))).thenReturn(token);

            // when
            ResultActions resultActions = mockMvc.perform(
                    post("/users/signup")
                            .content(body)
                            .contentType("application/json")
            );

            // then
            String response = objectMapper.writeValueAsString(responseDto);
            resultActions.andExpect(status().isOk())
                    .andExpect(header().string(JwtUtil.AUTHORIZATION_HEADER, token))
                    .andExpect(content().string(response));

        }
    }


    @Nested
    class DeleteUserTest {
        @Test
        void 토큰이_없어서_에러_발생() throws Exception {
            User user = new User("Rtan@sparta.com", "Rtan", "encodeddPassword", UserRole.USER);
            ReflectionTestUtils.setField(user, "id", 1L);
            UserDetailsImpl userDetails = new UserDetailsImpl(user);
            UserDeleteRequestDto requestDto = new UserDeleteRequestDto("password123!");
            ObjectMapper objectMapper = new ObjectMapper();
            String body = objectMapper.writeValueAsString(requestDto);

            // when
            ResultActions resultActions = mockMvc.perform(
                    delete("/users")
                            .content(body)
                            .contentType("application/json")
                            .with(SecurityMockMvcRequestPostProcessors.user(userDetails))
                            .with(csrf())
            );

            // then
            resultActions.andExpect(status().isBadRequest());
        }

        @ParameterizedTest
        @ValueSource(strings = {"a", "a1", "a1!", "ab1!", "ab12!", "ab12!@", "abc12!@"})
        void requestDto_비밀번호_길이가_8보다_짧아서_에러_발생(String password) throws Exception {
            String token = "JWT 토큰";
            User user = new User("Rtan@sparta.com", "Rtan", "encodeddPassword", UserRole.USER);
            ReflectionTestUtils.setField(user, "id", 1L);
            UserDetailsImpl userDetails = new UserDetailsImpl(user);
            UserDeleteRequestDto requestDto = new UserDeleteRequestDto(password);
            ObjectMapper objectMapper = new ObjectMapper();
            String body = objectMapper.writeValueAsString(requestDto);

            // when
            ResultActions resultActions = mockMvc.perform(
                    delete("/users")
                            .header(JwtUtil.AUTHORIZATION_HEADER, token)
                            .content(body)
                            .contentType("application/json")
                            .with(SecurityMockMvcRequestPostProcessors.user(userDetails))
                            .with(csrf())
            );

            // then
            String errorMessage = objectMapper.writeValueAsString(Map.of(
                    "password", Map.of(
                            "validation", "비밀번호는 대소문자 포함 영문 + 숫자 + 특수문자 최소 1글자씩 포함, 길이는 8 ~ 15자 사이로 작성하여야 합니다.",
                            "value", requestDto.getPassword()
                    )
            ));
            resultActions.andExpect(status().isBadRequest())
                    .andExpect(content().string(errorMessage));
        }

        @Test
        void requestDto_비밀번호에_영문을_안넣어서_에러_발생() throws Exception {
            String token = "JWT 토큰";
            User user = new User("Rtan@sparta.com", "Rtan", "encodeddPassword", UserRole.USER);
            ReflectionTestUtils.setField(user, "id", 1L);
            UserDetailsImpl userDetails = new UserDetailsImpl(user);
            UserDeleteRequestDto requestDto = new UserDeleteRequestDto("12345!@#$%");
            ObjectMapper objectMapper = new ObjectMapper();
            String body = objectMapper.writeValueAsString(requestDto);

            // when
            ResultActions resultActions = mockMvc.perform(
                    delete("/users")
                            .header(JwtUtil.AUTHORIZATION_HEADER, token)
                            .content(body)
                            .contentType("application/json")
                            .with(SecurityMockMvcRequestPostProcessors.user(userDetails))
                            .with(csrf())
            );

            // then
            String errorMessage = objectMapper.writeValueAsString(Map.of(
                    "password", Map.of(
                            "validation", "비밀번호는 대소문자 포함 영문 + 숫자 + 특수문자 최소 1글자씩 포함, 길이는 8 ~ 15자 사이로 작성하여야 합니다.",
                            "value", requestDto.getPassword()
                    )
            ));
            resultActions.andExpect(status().isBadRequest())
                    .andExpect(content().string(errorMessage));
        }

        @Test
        void requestDto_비밀번호에_숫자를_안넣어서_에러_발생() throws Exception {
            String token = "JWT 토큰";
            User user = new User("Rtan@sparta.com", "Rtan", "encodeddPassword", UserRole.USER);
            ReflectionTestUtils.setField(user, "id", 1L);
            UserDetailsImpl userDetails = new UserDetailsImpl(user);
            UserDeleteRequestDto requestDto = new UserDeleteRequestDto("abcde!@#$%");
            ObjectMapper objectMapper = new ObjectMapper();
            String body = objectMapper.writeValueAsString(requestDto);

            // when
            ResultActions resultActions = mockMvc.perform(
                    delete("/users")
                            .header(JwtUtil.AUTHORIZATION_HEADER, token)
                            .content(body)
                            .contentType("application/json")
                            .with(SecurityMockMvcRequestPostProcessors.user(userDetails))
                            .with(csrf())
            );

            // then
            String errorMessage = objectMapper.writeValueAsString(Map.of(
                    "password", Map.of(
                            "validation", "비밀번호는 대소문자 포함 영문 + 숫자 + 특수문자 최소 1글자씩 포함, 길이는 8 ~ 15자 사이로 작성하여야 합니다.",
                            "value", requestDto.getPassword()
                    )
            ));
            resultActions.andExpect(status().isBadRequest())
                    .andExpect(content().string(errorMessage));
        }

        @Test
        void requestDto_비밀번호에_특수문자를_안넣어서_에러_발생() throws Exception {
            String token = "JWT 토큰";
            User user = new User("Rtan@sparta.com", "Rtan", "encodeddPassword", UserRole.USER);
            ReflectionTestUtils.setField(user, "id", 1L);
            UserDetailsImpl userDetails = new UserDetailsImpl(user);
            UserDeleteRequestDto requestDto = new UserDeleteRequestDto("12345abcd");
            ObjectMapper objectMapper = new ObjectMapper();
            String body = objectMapper.writeValueAsString(requestDto);

            // when
            ResultActions resultActions = mockMvc.perform(
                    delete("/users")
                            .header(JwtUtil.AUTHORIZATION_HEADER, token)
                            .content(body)
                            .contentType("application/json")
                            .with(SecurityMockMvcRequestPostProcessors.user(userDetails))
                            .with(csrf())
            );

            // then
            String errorMessage = objectMapper.writeValueAsString(Map.of(
                    "password", Map.of(
                            "validation", "비밀번호는 대소문자 포함 영문 + 숫자 + 특수문자 최소 1글자씩 포함, 길이는 8 ~ 15자 사이로 작성하여야 합니다.",
                            "value", requestDto.getPassword()
                    )
            ));
            resultActions.andExpect(status().isBadRequest())
                    .andExpect(content().string(errorMessage));
        }

        @Test
        void requestDto_비밀번호에_한글_넣어서_에러_발생() throws Exception {
            String token = "JWT 토큰";
            User user = new User("Rtan@sparta.com", "Rtan", "encodeddPassword", UserRole.USER);
            ReflectionTestUtils.setField(user, "id", 1L);
            UserDetailsImpl userDetails = new UserDetailsImpl(user);
            UserDeleteRequestDto requestDto = new UserDeleteRequestDto("12345ㅁ!@#$%");
            ObjectMapper objectMapper = new ObjectMapper();
            String body = objectMapper.writeValueAsString(requestDto);

            // when
            ResultActions resultActions = mockMvc.perform(
                    delete("/users")
                            .header(JwtUtil.AUTHORIZATION_HEADER, token)
                            .content(body)
                            .contentType("application/json")
                            .with(SecurityMockMvcRequestPostProcessors.user(userDetails))
                            .with(csrf())
            );

            // then
            String errorMessage = objectMapper.writeValueAsString(Map.of(
                    "password", Map.of(
                            "validation", "비밀번호는 대소문자 포함 영문 + 숫자 + 특수문자 최소 1글자씩 포함, 길이는 8 ~ 15자 사이로 작성하여야 합니다.",
                            "value", requestDto.getPassword()
                    )
            ));
            resultActions.andExpect(status().isBadRequest())
                    .andExpect(content().string(errorMessage));
        }


        @Test
        void requestDto_비밀번호_길이가_15보다_길어서_에러_발생() throws Exception {
            String token = "JWT 토큰";
            User user = new User("Rtan@sparta.com", "Rtan", "encodeddPassword", UserRole.USER);
            ReflectionTestUtils.setField(user, "id", 1L);
            UserDetailsImpl userDetails = new UserDetailsImpl(user);
            UserDeleteRequestDto requestDto = new UserDeleteRequestDto("abcde12345!@#$%1");
            ObjectMapper objectMapper = new ObjectMapper();
            String body = objectMapper.writeValueAsString(requestDto);

            // when
            ResultActions resultActions = mockMvc.perform(
                    delete("/users")
                            .header(JwtUtil.AUTHORIZATION_HEADER, token)
                            .content(body)
                            .contentType("application/json")
                            .with(SecurityMockMvcRequestPostProcessors.user(userDetails))
                            .with(csrf())
            );

            // then
            String errorMessage = objectMapper.writeValueAsString(Map.of(
                    "password", Map.of(
                            "validation", "비밀번호는 대소문자 포함 영문 + 숫자 + 특수문자 최소 1글자씩 포함, 길이는 8 ~ 15자 사이로 작성하여야 합니다.",
                            "value", requestDto.getPassword()
                    )
            ));
            resultActions.andExpect(status().isBadRequest())
                    .andExpect(content().string(errorMessage));
        }

    }

    @Test
    @WithMockUser
    void 카카오로그인_리다이렉트_성공() throws Exception {
        // given
        String redirectURL = "https://kauth.kakao.com/oauth/authorize?client_id=" + clientId + "&redirect_uri=http://localhost:8080/users/login/kakao/callback&response_type=code";
        when(kakaoService.kakaoLogin()).thenReturn(redirectURL);

        // when
        ResultActions resultActions = mockMvc.perform(get("/users/login/kakao"));

        // then
        resultActions.andExpect(status().is3xxRedirection())
                        .andExpect(redirectedUrl(redirectURL));
        verify(kakaoService, times(1)).kakaoLogin();
    }

    @Test
    void 카카오로그인_콜벡_성공() throws Exception {
        // given
        String code = "sample_code";
        String bearerToken = "sample_token";
        UserResponseDto userResponseDto = new UserResponseDto(new User());

        ReflectionTestUtils.setField(userResponseDto, "username", "홍길동");
        ReflectionTestUtils.setField(userResponseDto, "email", "test@example.com");

        // Mocking 서비스 호출
        when(kakaoService.kakaoLoginCallback(code)).thenReturn(userResponseDto);
        when(userService.createToken(any())).thenReturn(bearerToken);

        // when & then
        mockMvc.perform(get("/users/login/kakao/callback")
                        .param("code", code)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(header().string(JwtUtil.AUTHORIZATION_HEADER, bearerToken))
                .andExpect(content().string(objectMapper.writeValueAsString(userResponseDto)));

        verify(kakaoService).kakaoLoginCallback(code);
        verify(userService).createToken(userResponseDto);

    }
}
