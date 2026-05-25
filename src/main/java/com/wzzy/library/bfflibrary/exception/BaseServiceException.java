package com.wzzy.library.bfflibrary.exception;

public class BaseServiceException extends RuntimeException {
    private String code;
    private int httpStatus;

    public BaseServiceException(String message) {
        super(message);
        this.code = "INTERNAL_ERROR";
        this.httpStatus = 500;
    }

    public BaseServiceException(String message, String code) {
        super(message);
        this.code = code;
        this.httpStatus = 500;
    }

    public BaseServiceException(String message, String code, int httpStatus) {
        super(message);
        this.code = code;
        this.httpStatus = httpStatus;
    }

    public String getCode() {
        return code;
    }

    public int getHttpStatus() {
        return httpStatus;
    }
}
