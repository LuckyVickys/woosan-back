package com.luckyvicky.woosan.global.auth.exception;

import com.luckyvicky.woosan.global.exception.ErrorCode;
import com.luckyvicky.woosan.global.exception.GlobalException;

public class JWTException extends GlobalException {
    public JWTException(ErrorCode errorCode) {
        super(errorCode);
    }
}
