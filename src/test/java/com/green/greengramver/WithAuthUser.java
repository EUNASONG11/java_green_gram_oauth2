package com.green.greengramver;

import org.springframework.security.test.context.support.WithSecurityContext;
import org.springframework.security.test.context.support.WithSecurityContextFactory;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME) //라이프사이클 지정, 런타임동안 사용 가능
@WithSecurityContext(factory = WithAuthMockUserSecurityContextFactory.class)
public @interface WithAuthUser {
    long signedUserId() default 1L;
    String[] roles() default {"ROLE_USER", "ROLE_ADMIN"};
}
