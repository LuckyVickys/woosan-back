package com.luckyvicky.woosan.global.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum ErrorCode {

    // 게시물 관련 오류
    BOARD_NOT_FOUND(HttpStatus.NOT_FOUND, "게시물을 찾을 수 없습니다."),
    BOARD_ALREADY_DELETED(HttpStatus.BAD_REQUEST, "이미 삭제된 게시물입니다."),

    // 댓글 관련 오류
    REPLY_NOT_FOUND(HttpStatus.NOT_FOUND, "댓글을 찾을 수 없습니다."),
    PARENT_REPLY_NOT_FOUND(HttpStatus.NOT_FOUND, "댓글을 찾을 수 없습니다."),

    // 공통 오류
    INVALID_INPUT_VALUE(HttpStatus.BAD_REQUEST, "유효하지 않은 입력입니다."),
    UNAUTHORIZED(HttpStatus.UNAUTHORIZED, "접근 가능한 레벨이 아닙니다."),
    SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "나중에 다시 시도하거나, 관리자에게 문의 바랍니다."),

    // 파일 업로드 오류
    IMAGE_FORMAT_ERROR(HttpStatus.BAD_REQUEST, "지원하지 않는 이미지 포맷입니다."),

    // 사용자 탐색 오류
    MEMBER_NOT_FOUND(HttpStatus.NOT_FOUND, "회원 정보를 찾을 수 없습니다.");


    private final HttpStatus status;
    private final String message;
}


// HTTP 상태 코드와 메시지로 예외 상황 정의