package com.wzzy.library.bfflibrary.exception;

public class ValidationException extends BaseServiceException {
    public ValidationException(String message) {
        super(message, "VALIDATION_ERROR", 400);
    }

    public ValidationException(String message, String code) {
        super(message, code, 400);
    }
}
