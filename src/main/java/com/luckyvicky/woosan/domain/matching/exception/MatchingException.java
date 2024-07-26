package com.luckyvicky.woosan.domain.matching.exception;

import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus
public class MatchingException extends RuntimeException {
    public MatchingException(String message){
        super(message);
    }
}
