package com.green.greengramver.config.jwt;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.green.greengramver.config.security.MyUserDetails;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.time.Duration;
import java.util.Date;

@Service
public class TokenProvider {
    private final ObjectMapper objectMapper; // Jackson 라이브러리
    private final JwtProperties jwtProperties;
    private final SecretKey secretKey;

    public TokenProvider(ObjectMapper objectMapper, JwtProperties jwtProperties) {
        this.objectMapper = objectMapper;
        this.jwtProperties = jwtProperties;
        this.secretKey = Keys.hmacShaKeyFor(Decoders.BASE64.decode(jwtProperties.getSecretKey())); // 43자 이상
    }

    // JWT 생성
    public String generateToken(JwtUser jwtUser, Duration expiredAt) { // jwtUser는 jwt 생성을 담는 것, expiredAt은 기간 설정
        Date now = new Date();
        return makeToken(jwtUser, new Date(now.getTime() + expiredAt.toMillis()));
    }

    private String makeToken(JwtUser jwtUser, Date expiry) {
//        JwtBuilder builder = Jwts.builder();
//        JwtBuilder.BuilderHeader header = builder.header();
//        header.type("JWT");
//
//        builder.issuer(jwtProperties.getIssuer());


        // JWT 암호화
        return Jwts.builder()
                .header().type("JWT")
                .and()
                .issuer(jwtProperties.getIssuer())
                .issuedAt(new Date())
                .expiration(expiry)
                .claim("signedUser", makeClaimByUserToString(jwtUser))
                .signWith(secretKey)
                .compact();
    }

    private String makeClaimByUserToString(JwtUser jwtUser) {
        // 객체 자체를 JWT에 담고 싶어서 객체를 직렬화(여기서는 객체를 String으로 바꾸는 작업)
        // jwtUser에 담고 있는 데이터를 JSON 형태의 문자열로 변환 - 직렬화
        try {
            return objectMapper.writeValueAsString(jwtUser);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    public boolean validToken(String token) {
        try {
            // JWT 복호화
            getClaims(token);
        } catch (Exception e) {
            return false;
        }
        return true;
    }

    // Spring Security에서 인증 처리를 해주어야 한다. 그 때 Authentication 객체가 필요
    public Authentication getAuthentication(String token) { //인증, 인가할 때 쓰는
       UserDetails userDetails = getUserDetailsFromToken(token);
       return userDetails == null ? null : new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
    }

    public JwtUser getJwtUserFromToken(String token) {
        Claims claims = getClaims(token);
        String json = (String)claims.get("signedUser");
        JwtUser jwtUser = null;
        try {
            jwtUser = objectMapper.readValue(json, JwtUser.class);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
        return jwtUser;
    }

    public UserDetails getUserDetailsFromToken(String token) {
        JwtUser jwtUser = getJwtUserFromToken(token);
        MyUserDetails userDetails = new MyUserDetails();
        userDetails.setJwtUser(jwtUser);
        return userDetails;
    }

    private Claims getClaims(String token) {
        return Jwts.parser().verifyWith(secretKey).build().parseSignedClaims(token).getPayload();
    }
}
