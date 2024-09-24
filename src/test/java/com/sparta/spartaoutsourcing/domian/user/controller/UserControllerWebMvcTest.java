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
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.filter.CharacterEncodingFilter;

import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@WebMvcTest(UserController.class)
public class UserControllerWebMvcTest {

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
    class DeleteUserTest {
        @Test
        void Principal이_없어서_에러_발생() throws Exception {
            String token = "JWT 토큰";
            UserDeleteRequestDto requestDto = new UserDeleteRequestDto("password123!");
            ObjectMapper objectMapper = new ObjectMapper();
            String body = objectMapper.writeValueAsString(requestDto);

            // when
            ResultActions resultActions = mockMvc.perform(
                    delete("/users")
                            .header(JwtUtil.AUTHORIZATION_HEADER, token)
                            .content(body)
                            .contentType("application/json")
                            .with(csrf())
            );

            // then
            resultActions.andExpect(status().isBadRequest());
        }


//        @Test
//        @WithMockUser
//        void 회원정보_DB에서_삭제_실패해서_에러_발생() throws Exception {
//            UserDeleteRequestDto requestDto = new UserDeleteRequestDto("password123!");
//            String body = objectMapper.writeValueAsString(requestDto);
//
//            when(userService.deleteUser(any(User.class), any(UserDeleteRequestDto.class))).thenReturn(false);
//
//            // when
//            ResultActions resultActions = mockMvc.perform(
//                    delete("/users")
//                            .header(JwtUtil.AUTHORIZATION_HEADER, "Bearer Token")
//                            .content(body)
//                            .contentType("application/json")
//                            .with(csrf())
//            );
//
//            // then
//            resultActions.andExpect(status().isInternalServerError())
//                    .andExpect(content().string("Failed to Delete ID"));
//
//            verify(userService, times(1)).deleteUser(any(User.class), any(UserDeleteRequestDto.class));
//        }

//        @Test
//        @WithMockUser
//        void 회원탈퇴_성공() throws Exception {
//            String token = "JWT토큰";
//            User user = new User("Rtan@sparta.com", "Rtan", "encodeddPassword", UserRole.USER);
//            ReflectionTestUtils.setField(user, "id", 1L);
//            UserDetailsImpl userDetails = new UserDetailsImpl(user);
//            UserDeleteRequestDto requestDto = new UserDeleteRequestDto("password123!");
//            ObjectMapper objectMapper = new ObjectMapper();
//            String body = objectMapper.writeValueAsString(requestDto);
//
//            when(userService.deleteUser(any(User.class), any(UserDeleteRequestDto.class))).thenReturn(true);
//            Mockito.doNothing().when(tokenBlacklistService).addTokenToBlackList(anyString());
//
//            // when
//            mockMvc.perform(
//                            delete("/users")
//                                    .header(JwtUtil.AUTHORIZATION_HEADER, token)
//                                    .content(body)
//                                    .contentType("application/json")
//                                    .with(SecurityMockMvcRequestPostProcessors.user(userDetails))
//                                    .with(csrf())
//                    ).andExpect(status().isOk())
//                    .andExpect(content().string("Delete ID Completed"));
//
//            verify(userService, times(1)).deleteUser(any(User.class), any(UserDeleteRequestDto.class));
//            verify(tokenBlacklistService, times(1)).addTokenToBlackList(anyString());
//        }

    }

}
