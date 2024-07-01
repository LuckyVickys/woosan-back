package com.luckyvicky.woosan.domain.board.exception;

import com.luckyvicky.woosan.global.exception.ErrorCode;
import com.luckyvicky.woosan.global.exception.GlobalException;

public class MemberNotFoundException extends GlobalException {
    public MemberNotFoundException(ErrorCode errorCode) {
        super(errorCode);
    }
}
