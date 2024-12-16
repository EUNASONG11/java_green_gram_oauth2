package com.green.greengramver.config.jwt;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.Date;

@RequiredArgsConstructor
@Service
public class TokenProvider {
    private final ObjectMapper objectMapper; // Jackson 라이브러리
    private final JwtProperties jwtProperties;

    // JWT 생성
    public String generateToken(JwtUser jwtUser, Duration expiredAt) { // jwtUser는 jwt 생성을 담는 것, expiredAt은 기간 설정
        Date now = new Date();
        return makeToken(jwtUser, new Date(now.getTime() + expiredAt.toMillis()));
    }

    private String makeToken(JwtUser jwtUser, Date expiry) {
        return Jwts.builder()
                .header().add("typ", "JWT")
                         .add("alg", "HS256")
                .and()
                .issuer(jwtProperties.getIssuer())
                .issuedAt(new Date())
                .expiration(expiry)
                .claim("signedUser", makeClaimByUserToString(jwtUser))
                .signWith(SignatureAlgorithm.HS256, jwtProperties.getSecretKey())
                .compact();
    }

    private String makeClaimByUserToString(JwtUser jwtUser) {
        // 객체 자체를 JWT에 담고 싶어서 객체를 직렬화
        // jwtUser에 담고 있는 데이터를 JSON 형태의 문자열로 변환 - 직렬화
        try {
            return objectMapper.writeValueAsString(jwtUser);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
}
