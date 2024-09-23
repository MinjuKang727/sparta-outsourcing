package com.sparta.spartaoutsourcing.user.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sparta.spartaoutsourcing.user.dto.request.KakaoUserInfoDto;
import com.sparta.spartaoutsourcing.user.dto.response.UserResponseDto;
import com.sparta.spartaoutsourcing.user.entity.User;
import com.sparta.spartaoutsourcing.user.enums.UserRole;
import com.sparta.spartaoutsourcing.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.view.RedirectView;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.UUID;

@Slf4j(topic = "KAKAO Login")
@Service
@RequiredArgsConstructor
public class KakaoService {

    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;
    private final RestTemplate restTemplate;

    @Value("${rest.api.key}")
    private String clientId;

    public RedirectView kakaoLogin() {
        // 요청 URL 만들기
        String kakaoAuthUrl = UriComponentsBuilder
                .fromUriString("https://kauth.kakao.com")
                .path("/oauth/authorize")
                .queryParam("client_id", clientId)
                .queryParam("redirect_uri", "http://localhost:8080/users/login/kakao/callback")
                .queryParam("response_type", "code")
                .build()
                .toString();

        log.info("Redirect URL : {}", kakaoAuthUrl);

        return new RedirectView(kakaoAuthUrl);
    }

    public UserResponseDto kakaoLogin(String code) throws JsonProcessingException {
        log.info("kakaoLogin() 메서드 실행");
        // 1. "인가 코드"로 "액세스 토큰" 요청
        String accessToken = getToken(code);

        // 2. 토큰으로 카카오 API 호출 : "액세스 토큰"으로 "카카오 사용자 정보" 가져오기
        KakaoUserInfoDto kakaoUserInfo = getKakaoUserInfo(accessToken);

        // 3. 필요시에 회원가입
        User kakaoUser = registerKakaoUserIfNeeded(kakaoUserInfo);

        return new UserResponseDto(kakaoUser);
    }


    /**
     * 인가 코드로 토큰 요청
     * @param code : 인가 코드
     * @return : 엑세스 토큰 문자열
     * @throws JsonProcessingException : ObjectMapper로 액세스 토큰 파싱 시, 발생할 수 있음
     */
    private String getToken(String code) throws JsonProcessingException {
        log.info("getToken() 메서드 실행");
        // 요청 URL 만들기
        URI uri = UriComponentsBuilder
                .fromUriString("https://kauth.kakao.com")
                .path("/oauth/token")
                .encode()
                .build()
                .toUri();

        // HTTP Header 생성
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-type", "application/x-www-form-urlencoded;charset=utf-8");

        // HTTP Body 생성
        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("grant_type", "authorization_code");
        body.add("client_id", clientId);
        body.add("redirect_uri", "http://localhost:8080/users/login/kakao/callback");
        body.add("code", code);  // 매개변수로 받은 인가 코드

        RequestEntity<MultiValueMap<String, String>> requestEntity = RequestEntity
                .post(uri)  // body가 있으므로 post
                .headers(headers)
                .body(body);

        // HTTP 요청 보내기
        ResponseEntity<String> response = restTemplate.exchange(
                requestEntity,
                String.class  // 받아 올 데이터 타입  >> 받을 데이터가 토큰 값
        );

        // HTTP 응답 (JSON) -> 액세스 토큰 파싱
        JsonNode jsonNode = new ObjectMapper().readTree(response.getBody());
        return jsonNode.get("access_token").asText();
    }

    /**
     * 토큰으로 카카오 API 호출 : "액세스 토큰"으로 "카카오 사용자 정보" 가져오기
     * @param accessToken : 엑세스 토큰
     * @return : 사용자 정보를 담은 KakaoUserInfoDto 객체
     * @throws JsonProcessingException ObjectMapper로 사용자 정보 파싱 시, 발생할 수 있음
     */
    private KakaoUserInfoDto getKakaoUserInfo(String accessToken) throws JsonProcessingException {
        log.info("getKakaoUserInfo() 메서드 실행");
        // 요청 URL 만들기
        URI uri = UriComponentsBuilder
                .fromUriString("https://kapi.kakao.com")
                .path("/v2/user/me")
                .encode()
                .build()
                .toUri();

        // HTTP Header 생성
        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "Bearer " + accessToken);
        headers.add("Content-type", "application/x-www-form-urlencoded;charset=utf-8");

        RequestEntity<MultiValueMap<String, String>> requestEntity = RequestEntity
                .post(uri)
                .headers(headers)
                .body(new LinkedMultiValueMap<>());  // body 따로 넣어줄 필요가 없어서 그냥 new 해서 넣어줌.

        // HTTP 요청 보내기
        ResponseEntity<String> response = this.restTemplate.exchange(
                requestEntity,
                String.class
        );

        JsonNode jsonNode = new ObjectMapper().readTree(response.getBody());
        Long id = jsonNode.get("id").asLong();
        String nickname = jsonNode.get("properties")
                .get("nickname").asText();
        String email = jsonNode.get("kakao_account")
                .get("email").asText();

        return new KakaoUserInfoDto(id, nickname, email);
    }

    /**
     * 필요시에 회원가입 혹은 회원 정보 업데이트
     * @param kakaoUserInfo : 회원 정보가 들어 있는 Dto 객체
     * @return User Entity
     */
    private User registerKakaoUserIfNeeded(KakaoUserInfoDto kakaoUserInfo) {
        log.info("registerKakaoUserIfNeeded() 메서드 실행");
        // DB 에 중복된 Kakao Id 가 있는지 확인
        Long kakaoId = kakaoUserInfo.getId();
        User kakaoUser = this.userRepository.findByKakaoId(kakaoId).orElse(null);

        if (kakaoUser == null) {
            // 카카오 사용자 email 동일한 email 가진 회원이 있는지 확인
            String kakaoEmail = kakaoUserInfo.getEmail();
            User sameEmailUser = this.userRepository.findByEmail(kakaoEmail).orElse(null);

            if (sameEmailUser != null) {
                kakaoUser = sameEmailUser;
                // 기존 회원정보에 카카오 Id 추가
                kakaoUser = kakaoUser.kakaoIdUpdate(kakaoId);
            } else {
                // 신규 회원가입
                // password: random UUID
                String password = UUID.randomUUID().toString();
                String encodedPassword = this.passwordEncoder.encode(password);

                kakaoUser = new User(kakaoUserInfo.getNickname(), encodedPassword, kakaoEmail, UserRole.USER, kakaoId);
            }

            this.userRepository.save(kakaoUser);
        }

        return kakaoUser;
    }
}