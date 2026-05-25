package com.wzzy.library.bfflibrary.exception;

public class UnauthorizedException extends BaseServiceException {
    public UnauthorizedException(String message) {
        super(message, "UNAUTHORIZED", 401);
    }
}
