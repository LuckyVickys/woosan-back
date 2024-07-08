package com.luckyvicky.woosan.domain.board.exception;

import com.luckyvicky.woosan.global.exception.ErrorCode;
import com.luckyvicky.woosan.global.exception.GlobalException;

public class ReplyException extends GlobalException {
    public ReplyException(ErrorCode errorCode) {
        super(errorCode);
    }
}
