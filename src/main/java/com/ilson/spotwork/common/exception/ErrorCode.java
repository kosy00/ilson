package com.ilson.spotwork.common.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum ErrorCode {

    // Common
    INVALID_INPUT(HttpStatus.BAD_REQUEST, "잘못된 입력입니다."),
    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "서버 오류가 발생했습니다."),

    // Auth
    UNAUTHORIZED(HttpStatus.UNAUTHORIZED, "인증이 필요합니다."),
    INVALID_TOKEN(HttpStatus.UNAUTHORIZED, "유효하지 않은 토큰입니다."),
    EXPIRED_TOKEN(HttpStatus.UNAUTHORIZED, "만료된 토큰입니다."),
    FORBIDDEN(HttpStatus.FORBIDDEN, "접근 권한이 없습니다."),

    // User
    USER_NOT_FOUND(HttpStatus.NOT_FOUND, "존재하지 않는 사용자입니다."),
    DUPLICATE_EMAIL(HttpStatus.CONFLICT, "이미 사용 중인 이메일입니다."),
    INVALID_PASSWORD(HttpStatus.UNAUTHORIZED, "비밀번호가 일치하지 않습니다."),
    LOCKED_USER(HttpStatus.FORBIDDEN, "정지된 계정입니다."),

    // Job
    JOB_NOT_FOUND(HttpStatus.NOT_FOUND, "존재하지 않는 공고입니다."),
    JOB_CLOSED(HttpStatus.BAD_REQUEST, "마감된 공고입니다."),

    // Application
    APPLICATION_NOT_FOUND(HttpStatus.NOT_FOUND, "존재하지 않는 지원입니다."),
    DUPLICATE_APPLICATION(HttpStatus.CONFLICT, "이미 지원한 공고입니다."),
    INVALID_STATUS_TRANSITION(HttpStatus.BAD_REQUEST, "유효하지 않은 상태 전이입니다."),
    OPTIMISTIC_LOCK_CONFLICT(HttpStatus.CONFLICT, "동시 요청으로 인한 충돌입니다. 다시 시도해주세요."),
    INVALID_APPLICATION_STATUS(HttpStatus.BAD_REQUEST,"유효하지 않은 지원 상태 전이입니다."),

    // Review
    REVIEW_NOT_FOUND(HttpStatus.NOT_FOUND, "존재하지 않는 후기입니다."),
    DUPLICATE_REVIEW(HttpStatus.CONFLICT, "이미 후기를 작성했습니다.");

    private final HttpStatus httpStatus;
    private final String message;
}
