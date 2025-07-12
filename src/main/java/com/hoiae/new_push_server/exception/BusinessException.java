package com.hoiae.new_push_server.exception;

public class BusinessException extends RuntimeException{
    private final ErrorCode errorCode;

    public BusinessException(String message, ErrorCode errorCode){
        super(message);
        this.errorCode = errorCode;
    }

    public BusinessException(String message, Throwable cause, ErrorCode errorCode) {
        super(message, cause);
        this.errorCode = errorCode;
    }

    public ErrorCode getErrorCode() {
        return errorCode;
    }
}
