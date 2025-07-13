package com.hoiae.news_push_server.exception;

public class MissingTokenException extends BusinessException{
    public MissingTokenException(){
        super(ErrorCode.MISSING_TOKEN.getMessage(),ErrorCode.MISSING_TOKEN);
    }
}
