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
    PARENT_REPLY_NOT_FOUND(HttpStatus.NOT_FOUND, "부모 댓글을 찾을 수 없습니다."),

    // 공통 오류
    INVALID_INPUT_VALUE(HttpStatus.BAD_REQUEST, "유효하지 않은 입력입니다."),
    UNAUTHORIZED(HttpStatus.UNAUTHORIZED, "접근 가능한 레벨이 아닙니다."),
    SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "나중에 다시 시도하거나, 관리자에게 문의 바랍니다."),
    NULL_OR_BLANK(HttpStatus.BAD_REQUEST, "필수 입력값을 입력해주세요."),       //title, content...
    INVALID_TYPE(HttpStatus.BAD_REQUEST, "유효하지 않은 타입입니다."),         //추천 {'게시물', '댓글'} 이외 ...
    PAGE_INDEX_INVALID(HttpStatus.BAD_REQUEST, "페이지 인덱스는 0보다 커야 합니다."), // 페이지 인덱스 유효성 검사
    PAGE_SIZE_INVALID(HttpStatus.BAD_REQUEST, "페이지 크기는 0보다 커야 합니다."),   // 페이지 크기 유효성 검사

    // 멤버 관련 오류
    MEMBER_NOT_FOUND(HttpStatus.NOT_FOUND, "회원 정보를 찾을 수 없습니다."),
    EMAIL_DUPLICATE(HttpStatus.BAD_REQUEST, "중복된 이메일입니다."),
    NICKNAME_DUPLICATE(HttpStatus.BAD_REQUEST, "중복된 닉네임입니다."),
    PW_NOT_FOUND(HttpStatus.BAD_REQUEST, "비밀번호가 일치하지 않습니다."),
    ACCESS_DENIED(HttpStatus.FORBIDDEN, "이 작업을 수행할 권한이 없습니다."),

    // 파일 업로드 오류
    IMAGE_FORMAT_ERROR(HttpStatus.BAD_REQUEST, "지원하지 않는 이미지 포맷입니다."),

    // 추천 관련 오류
    LIKES_COUNT_NEGATIVE(HttpStatus.BAD_REQUEST, "추천 수는 음수가 될 수 없습니다."),

    // jwt 관련 오류
    ACCESS_TOKEN_NOT_FOUND(HttpStatus.NOT_FOUND, "Access Token이 존재하지 않습니다."),
    REFRESH_TOKEN_NOT_FOUND(HttpStatus.NOT_FOUND, "Refresh Token이 존재하지 않습니다."),
    INVALID_STRING(HttpStatus.BAD_REQUEST, "Invalid String"),

    // 쪽지 관련 오류
    MESSAGE_NOT_FOUND(HttpStatus.NOT_FOUND, "쪽지를 찾을 수 없습니다."),
    MESSAGE_ALREADY_DELETED(HttpStatus.BAD_REQUEST, "이미 삭제된 쪽지입니다.");

    private final HttpStatus status;
    private final String message;
}


// HTTP 상태 코드와 메시지로 예외 상황 정의