package com.luckyvicky.woosan.global.exception;

import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class ExceptionResponse {

    private final LocalDateTime timestamp;
    private final String message;
    private final String details;

    public ExceptionResponse(final String message, final String details) {
        this.timestamp = LocalDateTime.now();
        this.message = message;
        this.details = details;
    }
}

// 오류 설명 정보 포함해 전달하는 역할