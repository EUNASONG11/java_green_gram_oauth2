package com.green.greengramver.config.jwt;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.Authentication;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest // 통합 테스트 때 사용
class TokenProviderTest {
    // 테스트는 생성자를 이용한 DI 불가능
    // DI 방법은 필드, setter 메소드, 생성자
    // 테스트 때는 필드 주입 방식을 사용한다.

    @Autowired //리플렉션 API를 이용해서 setter가 없어도 주입 가능
    private TokenProvider tokenProvider;

    @Test
    public void generateToken() {
        // Given (준비단계)
        JwtUser jwtUser = new JwtUser();
        jwtUser.setSignedUserId(10);

        List<String> roles = new ArrayList<>(2);
        roles.add("ROLE_USER");
        roles.add("ROLE_ADMIN");
        jwtUser.setRoles(roles);

        // When (실행단계)
        String token = tokenProvider.generateToken(jwtUser, Duration.ofHours(3));

        // Then (검증단계)
        assertNotNull(token);

        System.out.println("token: " + token);
    }

    @Test
    void validToken() {
        // 1분 지남
        String token = "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzUxMiJ9.eyJpc3MiOiJncmVlbkBncmVlbi5rciIsImlhdCI6MTczNDQwMTQxNiwiZXhwIjoxNzM0NDAxNDc2LCJzaWduZWRVc2VyIjoie1wic2lnbmVkVXNlcklkXCI6MTAsXCJyb2xlc1wiOltcIlJPTEVfVVNFUlwiLFwiUk9MRV9BRE1JTlwiXX0ifQ.8RRWv5pQ1MR-B7609pPO3VrYrgW9yT2tMjToMAPTihUCJm0EnBOVMs7gEINqLz-DeRUNIDE3na1YEgfv7OGSiA";

        boolean result = tokenProvider.validToken(token);

        assertFalse(result);
    }

    @Test
    void getAuthentication() {
        String token = "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzUxMiJ9.eyJpc3MiOiJncmVlbkBncmVlbi5rciIsImlhdCI6MTczNDQwMzUzNSwiZXhwIjoxNzM0NDE0MzM1LCJzaWduZWRVc2VyIjoie1wic2lnbmVkVXNlcklkXCI6MTAsXCJyb2xlc1wiOltcIlJPTEVfVVNFUlwiLFwiUk9MRV9BRE1JTlwiXX0ifQ.qXC7_9qYvR34LFsnn4ylNjtv_I33UBQqxPG13C-iw1f8ANNbX3irnnLAABbTbgusqGLQ7YvDMkd6VmKgAZcmXQ"; // 3시간

        Authentication authentication = tokenProvider.getAuthentication(token);

        assertNotNull(authentication);
    }
}