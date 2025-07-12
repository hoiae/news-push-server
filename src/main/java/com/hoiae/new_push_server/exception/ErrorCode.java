package com.hoiae.new_push_server.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum ErrorCode {
    // 뉴스
    NEWS_NOT_FOUND("N001", HttpStatus.NOT_FOUND, "뉴스를 찾을 수 없습니다."),

    // WebSocket
    MESSAGE_SEND_FAILED("W001", HttpStatus.INTERNAL_SERVER_ERROR, "메시지 전송에 실패했습니다."),
    SERIALIZATION_FAILED("W002", HttpStatus.INTERNAL_SERVER_ERROR, "JSON 변환에 실패했습니다."),

    // 인증
    MISSING_TOKEN("A001", HttpStatus.BAD_REQUEST, "인증 토큰이 누락되었습니다."),
    DUPLICATE_CONNECTION("A002", HttpStatus.CONFLICT, "해당 토큰으로 이미 연결된 클라이언트가 존재합니다."),

    // 기타
    INTERNAL_ERROR("C001", HttpStatus.INTERNAL_SERVER_ERROR, "서버 내부 오류가 발생했습니다.");

    private final String code;
    private final HttpStatus httpStatus;
    private final String message;

    ErrorCode(String code, HttpStatus httpStatus, String message) {
        this.code = code;
        this.httpStatus = httpStatus;
        this.message = message;
    }
}
