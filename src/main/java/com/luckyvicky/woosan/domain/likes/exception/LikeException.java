package com.luckyvicky.woosan.domain.likes.exception;

import com.luckyvicky.woosan.global.exception.ErrorCode;
import com.luckyvicky.woosan.global.exception.GlobalException;

public class LikeException extends GlobalException {
    public LikeException(ErrorCode errorCode) {
        super(errorCode);
    }
}
