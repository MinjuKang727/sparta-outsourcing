package com.sparta.spartaoutsourcing.user.service;

import com.sparta.spartaoutsourcing.auth.jwt.JwtUtil;
import com.sparta.spartaoutsourcing.user.exception.UserException;
import com.sparta.spartaoutsourcing.user.dto.request.UserDeleteRequestDto;
import com.sparta.spartaoutsourcing.user.dto.request.UserSignupRequestDto;
import com.sparta.spartaoutsourcing.user.dto.response.UserResponseDto;
import com.sparta.spartaoutsourcing.user.entity.User;
import com.sparta.spartaoutsourcing.user.enums.UserRole;
import com.sparta.spartaoutsourcing.user.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;

import java.io.UnsupportedEncodingException;

@Slf4j(topic = "UserService")
@Service
@Transactional(readOnly = true)
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder, JwtUtil jwtUtil) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
    }

    // OWNER_TOKEN
    @Value("${jwt.owner.token}")
    private String OWNER_TOKEN;

    @Transactional
    public UserResponseDto signup(UserSignupRequestDto requestDto) throws UserException {
        log.info("signup() 메서드 실행");
        String email = requestDto.getEmail();

        // 회원 중복 확인
        if (this.userRepository.existsByEmail(email)) {
            throw new UserException("회원 가입 실패", new IllegalArgumentException("해당 이메일의 사용자가 이미 존재합니다."));
        }

        // 사용자 ROLE 확인
        UserRole role = UserRole.USER;
        if(requestDto.getIsOwner()) {
            if (!ObjectUtils.nullSafeEquals(OWNER_TOKEN,requestDto.getOwnerKey())) {
                throw new UserException("회원 가입 실패", new IllegalArgumentException("관리자 암호가 틀려 등록이 불가능합니다."));
            }
            role = UserRole.OWNER;
        }

        String password = this.passwordEncoder.encode(requestDto.getPassword());

        // 사용자 등록
        User user = new User(requestDto, password, role);
        User savedUser = this.userRepository.save(user);

        return new UserResponseDto(savedUser);
    }

    /**
     * 회원 탈퇴
     * @param user : 현재 로그인 중인 사용자 Entity
     * @param requestDto : 탈퇴 인증을 위한 email과 password가 담긴 requestDto
     * @throws IllegalArgumentException : 현재 로그인 한 계정과 탈퇴 요청한 계정이 다른 경우, 비밀번호가 일치하지 않는 경우 발생
     */
    @Transactional
    public void deleteUser(User user, UserDeleteRequestDto requestDto) throws UserException {
        log.info("deleteUser() 메서드 실행");

        if (!this.passwordEncoder.matches(requestDto.getPassword(), user.getPassword())) {
            throw new UserException("회원 탈퇴 실패", new IllegalArgumentException("비밀번호를 잘못입력하셨습니다."));
        }

        user.delete();
        this.userRepository.save(user);
    }

    public String createToken(UserResponseDto responseDto) throws UnsupportedEncodingException {
        log.info("createToken() 메서드 실행");
        return jwtUtil.createToken(responseDto.getId(), responseDto.getEmail(), responseDto.getUsername(), responseDto.getRole());
    }

}
