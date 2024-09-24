package com.sparta.spartaoutsourcing.auth;

import com.sparta.spartaoutsourcing.user.enums.UserRole;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.ServletException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.security.Key;
import java.util.Base64;
import java.util.Date;

public class JwtTestUtil {
    public static final String AUTHORIZATION_HEADER = "Authorization";
    public static final String AUTHORIZATION_KEY = "auth";
    public static final String BEARER_PERFIX = "Bearer ";
    private final long TOKEN_TIME = 60 * 60 * 1000L; // 60분

//    @Value("${jwt.secret.key")
    private String secretkey = "7Iqk7YyM66W07YOA7L2U65Sp7YG065+9U3ByaW5n6rCV7J2Y7Yqc7YSw7LWc7JuQ67mI7J6F64uI64ukLg==";
    private Key key = Keys.hmacShaKeyFor(Base64.getDecoder().decode(secretkey));;
    private final SignatureAlgorithm signatureAlgorithm = SignatureAlgorithm.HS256;
    public static final Logger logger = LoggerFactory.getLogger("JWT 관련 로그");

    /**
     * JWT 생성
     * @param userId : 사용자 고유 식별자
     * @param email : 사용자 이메일
     * @param username : 사용자명
     * @param role : 사용자 권한
     * @return JWT 토큰
     */
    public String createToken(Long userId, String email, String username, UserRole role) throws UnsupportedEncodingException {
        Date date = new Date();

        String token = BEARER_PERFIX +
                Jwts.builder()
                        .setSubject(email)
                        .claim("username", username)
                        .claim("userId", userId)
                        .claim(AUTHORIZATION_KEY, role)
                        .setExpiration(new Date(date.getTime() + TOKEN_TIME))
                        .setIssuedAt(date)
                        .signWith(key, signatureAlgorithm)
                        .compact();

        return URLEncoder.encode(token, "UTF-8").replaceAll("\\+", "%20");
    }

    /**
     * JWT 검증
     *
     * @param token : 토큰값
     * @return : 토큰 검증 여부
     */
    public boolean validateToken(String token) throws ServletException {
        try {
            token = token.replaceAll("\\s", "");
            Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token);

            return true;
        } catch (SecurityException | MalformedJwtException e) {
            logger.error("Invalid JWT signature, 유효하지 않은 JWT 서명입니다.");
            throw new ServletException("Invalid JWT signature, 유효하지 않은 JWT 서명입니다.");
        } catch (ExpiredJwtException e) {
            logger.error("Expired JWT token, 만료된 JWT token입니다.");
            throw new ServletException("Expired JWT token, 만료된 JWT token입니다.");
        } catch (UnsupportedJwtException e) {
            logger.error("Unsupported JWT token, 지원되지 않는 JWT 토큰 입니다.");
            throw new ServletException("Unsupported JWT token, 지원되지 않는 JWT 토큰 입니다.");
        } catch (IllegalArgumentException e) {
            logger.error("JWT claims is empty, 잘못된 JWT 토큰 입니다.");
            throw new ServletException("JWT claims is empty, 잘못된 JWT 토큰 입니다.");
        }
    }


    /**
     * JWT에서 사용자 정보 가져오기
     */
    public Claims getUserInfoFromToken(String token) {
        return Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token).getBody();
    }

    public Date getExpirationTime(String token) {
        return this.getUserInfoFromToken(token).getExpiration();
    }


    public String getDecodedToken(String bearerToken) throws ServletException {
        if (bearerToken != null) {
            try {
                return URLDecoder.decode(bearerToken, "UTF-8");
            } catch (UnsupportedEncodingException e) {
                throw new ServletException("Fail to decode Token");
            }
        }

        return null;
    }

    /**
     * JWT 토큰의 앞 BEARER_PREFIX 자르기
     * @param bearerToken : 토큰 값
     * @return BEARER_PREFIX를 자른 토큰 값
     */
    public String substringToken(String bearerToken) throws ServletException {
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith(BEARER_PERFIX)) {
            return bearerToken.substring(7);
        }

        logger.error("Not Found Token");
        throw new ServletException("Invalid Token");
    }
}
