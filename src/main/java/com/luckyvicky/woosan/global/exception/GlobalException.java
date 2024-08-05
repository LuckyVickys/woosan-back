package com.luckyvicky.woosan.global.exception;

import lombok.Getter;

@Getter
public class GlobalException extends RuntimeException {

    private final ErrorCode errorCode;

    public GlobalException(ErrorCode errorCode) {
        this.errorCode = errorCode;
    }
}

// 예외 발생 시, 해당 예외외 관련된 에러코드 저장하고 전달
