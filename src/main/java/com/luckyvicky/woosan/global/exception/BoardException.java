package com.luckyvicky.woosan.global.exception;

import com.luckyvicky.woosan.global.exception.ErrorCode;
import com.luckyvicky.woosan.global.exception.GlobalException;

public class BoardException extends GlobalException {
    public BoardException(ErrorCode errorCode) {
        super(errorCode);
    }
}

// 예외 상황 GlobalException에 전달