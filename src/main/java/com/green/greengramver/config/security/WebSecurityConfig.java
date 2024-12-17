package com.green.greengramver.config.security;
// Spring Security 세팅

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;


@Configuration // 빈등록 가능, 메소드 빈등록 가능
@RequiredArgsConstructor
public class WebSecurityConfig {
    // Spring Security 기능 비활성화(Spring Security가 관여하지 않았으면 하는 부분)
    @Bean
    public WebSecurityCustomizer webSecurityCustomizer() {
        return web -> web.ignoring()
                         .requestMatchers(new AntPathRequestMatcher("/static/**"));
    }
}
