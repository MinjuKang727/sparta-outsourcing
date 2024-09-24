package com.sparta.spartaoutsourcing.domian.user.service;

import com.sparta.spartaoutsourcing.auth.jwt.JwtUtil;
import com.sparta.spartaoutsourcing.user.dto.request.UserDeleteRequestDto;
import com.sparta.spartaoutsourcing.user.dto.request.UserSignupRequestDto;
import com.sparta.spartaoutsourcing.user.dto.response.UserResponseDto;
import com.sparta.spartaoutsourcing.user.entity.User;
import com.sparta.spartaoutsourcing.user.enums.UserRole;
import com.sparta.spartaoutsourcing.user.exception.UserException;
import com.sparta.spartaoutsourcing.user.repository.UserRepository;
import com.sparta.spartaoutsourcing.user.service.UserService;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.util.ObjectUtils;

import java.io.UnsupportedEncodingException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {
    @Mock
    private UserRepository userRepository;

    @Spy
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtUtil jwtUtil;

    @InjectMocks
    private UserService userService;

    @Value("${signup.owner.key}")
    private String ownerKey;

    @Nested
    class SignupTest {
        @Test
        void 해당_이메일의_사용자가_이미_존재해서_에러_발생() {
            // given
            UserSignupRequestDto requestDto = spy(UserSignupRequestDto.class);
            ReflectionTestUtils.setField(requestDto, "email", "Rtan@sparta.com");

            given(userRepository.existsByEmail(anyString())).willReturn(true);

            // when & then
            UserException exception = assertThrows(UserException.class, () ->
                    userService.signup(requestDto));

            verify(userRepository, times(1)).existsByEmail(anyString());
            assertEquals("회원 가입 실패", exception.getMessage());
            assertInstanceOf(IllegalArgumentException.class, exception.getCause());
            assertEquals("해당 이메일의 사용자가 이미 존재합니다.", exception.getCause().getMessage());
        }

        @Test
        void 사장님_권한_키_입력을_잘못해서_에러_발생() {
            // given
            UserSignupRequestDto requestDto = spy(UserSignupRequestDto.class);
            ReflectionTestUtils.setField(requestDto, "email", "Rtan@sparta.com");
            ReflectionTestUtils.setField(requestDto, "isOwner", true);
            ReflectionTestUtils.setField(requestDto, "ownerKey", "wrongOwnerKey");

            given(userRepository.existsByEmail(anyString())).willReturn(false);

            // when & then
            UserException exception = assertThrows(UserException.class, () ->
                    userService.signup(requestDto));

            assertTrue(requestDto.getIsOwner());
            assertFalse(ObjectUtils.nullSafeEquals(ownerKey,requestDto.getOwnerKey()));
            assertEquals("회원 가입 실패", exception.getMessage());
            assertInstanceOf(IllegalArgumentException.class, exception.getCause());
            assertEquals("사장님 권한 인가 키를 잘못 입력하셨습니다.", exception.getCause().getMessage());
        }

        @Test
        void USER_권한으로_회원가입_성공() throws UserException {
            // given
            UserSignupRequestDto requestDto = spy(UserSignupRequestDto.class);
            ReflectionTestUtils.setField(requestDto, "email", "Rtan@sparta.com");
            ReflectionTestUtils.setField(requestDto, "password", "spart123!");
            ReflectionTestUtils.setField(requestDto, "isOwner", false);
            User savedUser = new User(requestDto, "encodedPassword", UserRole.USER);
            ReflectionTestUtils.setField(savedUser, "id", 1L);


            given(userRepository.existsByEmail(anyString())).willReturn(false);
            given(passwordEncoder.encode(requestDto.getPassword())).willReturn("encodedPassword");
            given(userRepository.save(any(User.class))).willReturn(savedUser);

            // when
            UserResponseDto responseDto = userService.signup(requestDto);

            // then
            verify(userRepository, times(1)).existsByEmail(anyString());
            assertFalse(requestDto.getIsOwner());
            verify(passwordEncoder, times(1)).encode(anyString());
            assertNotNull(responseDto);
            assertEquals(savedUser.getId(), responseDto.getId());
            assertEquals(requestDto.getEmail(), responseDto.getEmail());
            assertEquals(requestDto.getUsername(), responseDto.getUsername());
            assertEquals(UserRole.USER, responseDto.getRole());
        }

        @Test
        void OWNER_권한으로_회원가입_성공() throws UserException {
            // given
            UserSignupRequestDto requestDto = spy(UserSignupRequestDto.class);
            ReflectionTestUtils.setField(requestDto, "email", "Rtan@sparta.com");
            ReflectionTestUtils.setField(requestDto, "password", "spart123!");
            ReflectionTestUtils.setField(requestDto, "isOwner", true);
            ReflectionTestUtils.setField(requestDto, "ownerKey", ownerKey);
            User savedUser = new User(requestDto, "encodedPassword", UserRole.OWNER);
            ReflectionTestUtils.setField(savedUser, "id", 1L);


            given(userRepository.existsByEmail(anyString())).willReturn(false);
            given(passwordEncoder.encode(requestDto.getPassword())).willReturn("encodedPassword");
            given(userRepository.save(any(User.class))).willReturn(savedUser);

            // when
            UserResponseDto responseDto = userService.signup(requestDto);

            // then
            verify(userRepository, times(1)).existsByEmail(anyString());
            assertTrue(requestDto.getIsOwner());
            verify(passwordEncoder, times(1)).encode(anyString());
            assertNotNull(responseDto);
            assertEquals(savedUser.getId(), responseDto.getId());
            assertEquals(requestDto.getEmail(), responseDto.getEmail());
            assertEquals(requestDto.getUsername(), responseDto.getUsername());
            assertEquals(UserRole.OWNER, responseDto.getRole());
        }

        @Nested
        class DeleteUserTest {
            @Test
            void 비밀번호를_잘못_입력해서_에러_발생() {
                // given
                User user = spy(User.class);
                ReflectionTestUtils.setField(user, "password", "encodedPassword");
                UserDeleteRequestDto requestDto = new UserDeleteRequestDto("wrongPassword400!");

                given(passwordEncoder.matches(anyString(), anyString())).willReturn(false);

                // when & then
                UserException exception = assertThrows(UserException.class, () ->
                        userService.deleteUser(user, requestDto));

                assertEquals("회원 탈퇴 실패", exception.getMessage());
                assertInstanceOf(IllegalArgumentException.class, exception.getCause());
                assertEquals("비밀번호를 잘못입력하셨습니다.", exception.getCause().getMessage());
                verify(passwordEncoder, times(1)).matches(anyString(), anyString());
            }

            @Test
            void 회원탈퇴_성공() throws UserException {
                // given
                User user = spy(User.class);
                ReflectionTestUtils.setField(user, "password", "encodedPassword");
                UserDeleteRequestDto requestDto = new UserDeleteRequestDto("wrongPassword400!");
                User savedUser = spy(User.class);
                ReflectionTestUtils.setField(savedUser, "isDeleted", true);

                given(passwordEncoder.matches(anyString(), anyString())).willReturn(true);
                given(userRepository.save(any(User.class))).willReturn(savedUser);

                // when
                Boolean isDeleted = userService.deleteUser(user, requestDto);

                // then
                verify(passwordEncoder, times(1)).matches(anyString(), anyString());
                verify(user, times(1)).delete();
                assertTrue(isDeleted);
            }

        }

        @Test
        void 토큰생성_성공() throws UnsupportedEncodingException {
            // given
            User user = spy(User.class);
            ReflectionTestUtils.setField(user, "id", 1L);
            ReflectionTestUtils.setField(user, "email", "Rtan@sparta.com");
            ReflectionTestUtils.setField(user, "username", "Rtan");
            ReflectionTestUtils.setField(user, "role", UserRole.USER);

            UserResponseDto responseDto = new UserResponseDto(user);
            String token = "Bearer%20T0K3N_V4LU3!";

            given(jwtUtil.createToken(anyLong(), anyString(), anyString(), any(UserRole.class))).willReturn(token);

            // when
            String JWT = userService.createToken(responseDto);

            // then
            verify(jwtUtil, times(1)).createToken(anyLong(), anyString(), anyString(), any(UserRole.class));
            assertNotNull(JWT);
        }
    }
}
