package com.hoiae.news_push_server.exception;

public class MessageSendFailureException extends BusinessException{
    public MessageSendFailureException(Throwable cause){
        super(ErrorCode.MESSAGE_SEND_FAILED.getMessage(), cause, ErrorCode.MESSAGE_SEND_FAILED);
    }
}
