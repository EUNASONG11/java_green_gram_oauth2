package com.green.greengramver.config.jwt;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Setter
@Getter
public class JwtUser {
    private long signedUserId;
    private List<String> roles; // 인가(권한)처리 때 사용, 여러 권한 부여를 위해 List, "ROLE_이름" > ROLE_USER, ROLE_ADMIN
}
