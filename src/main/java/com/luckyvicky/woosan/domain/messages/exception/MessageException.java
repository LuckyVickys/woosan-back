package com.luckyvicky.woosan.domain.messages.exception;

import com.luckyvicky.woosan.global.exception.ErrorCode;
import com.luckyvicky.woosan.global.exception.GlobalException;

public class MessageException extends GlobalException {
    public MessageException(ErrorCode errorCode) {
        super(errorCode);
    }
}
