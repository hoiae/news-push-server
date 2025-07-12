package com.hoiae.new_push_server.exception;

public class NewsNotFoundException extends BusinessException{
    public NewsNotFoundException() {
        super(ErrorCode.NEWS_NOT_FOUND.getMessage(), ErrorCode.NEWS_NOT_FOUND);
    }
}
