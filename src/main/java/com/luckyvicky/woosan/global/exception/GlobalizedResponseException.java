package com.luckyvicky.woosan.global.exception;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

import java.util.ArrayList;
import java.util.List;

@RestControllerAdvice
public class GlobalizedResponseException {

    @ExceptionHandler(GlobalException.class)
    public final ResponseEntity<?> handleGlobalException(final GlobalException e, WebRequest request) {
        return ResponseEntity.status(e.getErrorCode().getStatus())
                .body(new ExceptionResponse(e.getErrorCode().getMessage(),request.getDescription(false)));
    }
    // GlobalException 타입 예외 처리

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<?> handleMethodArgumentNotValidException(MethodArgumentNotValidException e, WebRequest request){
        List<ExceptionResponse> errors = new ArrayList<>();

        e.getBindingResult().getAllErrors().forEach(c ->errors.add(new ExceptionResponse(c.getDefaultMessage(),request.getDescription(false))));

        return ResponseEntity.badRequest().body(errors);
    }
    // 메서드 인자 유효하지 않을 때 발생하는 예외 처리

}
