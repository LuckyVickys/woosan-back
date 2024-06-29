package com.luckyvicky.woosan.domain.board.exception;

import com.luckyvicky.woosan.global.exception.ErrorCode;
import com.luckyvicky.woosan.global.exception.GlobalException;

public class BoardNotFoundException extends GlobalException {
    public BoardNotFoundException(ErrorCode errorCode) {
        super(errorCode);
    }
}

// 예외 상황 GlobalException에 전달