package com.hoiae.news_push_server.exception;

public class DuplicateConnectionException extends BusinessException{

    public DuplicateConnectionException() {
        super(ErrorCode.DUPLICATE_CONNECTION.getMessage(), ErrorCode.DUPLICATE_CONNECTION);
    }
}
