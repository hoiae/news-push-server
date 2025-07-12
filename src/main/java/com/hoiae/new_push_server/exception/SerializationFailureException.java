package com.hoiae.new_push_server.exception;

public class SerializationFailureException extends BusinessException{
    public SerializationFailureException(Throwable cause) {
        super(ErrorCode.SERIALIZATION_FAILED.getMessage(), cause, ErrorCode.SERIALIZATION_FAILED);
    }
}
