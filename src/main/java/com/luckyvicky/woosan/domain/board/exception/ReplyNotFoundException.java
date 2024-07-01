package com.luckyvicky.woosan.domain.board.exception;

import com.luckyvicky.woosan.global.exception.ErrorCode;
import com.luckyvicky.woosan.global.exception.GlobalException;

public class ReplyNotFoundException extends GlobalException {
    public ReplyNotFoundException(ErrorCode errorCode) {
        super(errorCode);
    }
}
