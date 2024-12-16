package com.green.greengramver.config.jwt;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Getter
@Setter
@ConfigurationProperties("jwt") // yaml에 있는 jwt를 담겠다는 것, Application에 @ConfigurationPropertiesScan 추가
public class JwtProperties {
    private String issuer;
    private String secretKey;
}
