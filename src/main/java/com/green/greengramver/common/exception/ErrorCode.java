package com.green.greengramver.common.exception;

import org.springframework.http.HttpStatus;

public interface ErrorCode {
    String name();
    String getMessage(); // ENUM에서 implement 할 때 String message 멤버필드를 꼭 가질 수 있도록 세팅
    HttpStatus getHttpStatus(); // 응답 코드 결정
}
