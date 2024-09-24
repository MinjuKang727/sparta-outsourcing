package com.sparta.spartaoutsourcing.domian.user.service;

import com.sparta.spartaoutsourcing.user.repository.UserRepository;
import com.sparta.spartaoutsourcing.user.service.KakaoService;
import org.junit.jupiter.api.*;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpHeaders;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.view.RedirectView;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

@WebMvcTest(KakaoService.class)
public class KakaoServiceTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private PasswordEncoder passwordEncoder;

    @MockBean
    private UserRepository userRepository;

    @MockBean
    private RestTemplate restTemplate;

    @InjectMocks
    private KakaoService kakaoService;

    @Value("${rest.api.key}")
    private String clientId;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
        ReflectionTestUtils.setField(kakaoService, "clientId", clientId);
    }

    @Test
    void 카카오_로그인_리다이렉트_성공() {
        // given
        String kakaoAuthURL = UriComponentsBuilder
                .fromUriString("https://kauth.kakao.com")
                .path("/oauth/authorize")
                .queryParam("client_id", clientId)
                .queryParam("redirect_uri", "http://localhost:8080/users/login/kakao/callback")
                .queryParam("response_type", "code")
                .build()
                .toString();


        // when
        String result = kakaoService.kakaoLogin();

        // then
        assertNotNull(result);
        assertEquals(kakaoAuthURL, result);
    }

//    @Nested
//    class KakaoLoginTest {
//        @Test
//        void 인가_코드로_토큰_요청_성공() {
//            String code = "인가코드";
//
//            URI uri = UriComponentsBuilder
//                    .fromUriString("https://kauth.kakao.com")
//                    .path("/oauth/token")
//                    .encode()
//                    .build()
//                    .toUri();
//
//            // HTTP Header 생성
//            HttpHeaders headers = new HttpHeaders();
//            headers.add("Content-type", "application/x-www-form-urlencoded;charset=utf-8");
//
//            String accessToken = "엑세스 토큰";
//
//            // HTTP Body 생성
//            MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
//            body.add("grant_type", "authorization_code");
//            body.add("client_id", clientId);
//            body.add("redirect_uri", "http://localhost:8080/users/login/kakao/callback");
//            body.add("code", code);
//
//            RequestEntity<MultiValueMap<String, String>> requestEntity = RequestEntity
//                    .post(uri)  // body가 있으므로 post
//                    .headers(headers)
//                    .body(body);
//
//            ResponseEntity<String> response = restTemplate.exchange(
//                    requestEntity,
//                    String.class  // 받아 올 데이터 타입  >> 받을 데이터가 토큰 값
//            );
//
//            given(restTemplate.exchange(any(RequestEntity.class), String.class)).willReturn(response);
//        }
//    }

}
