package com.wzzy.library.bfflibrary.exception;

public class ServiceUnavailableException extends BaseServiceException {
    public ServiceUnavailableException(String serviceName) {
        super("Service " + serviceName + " is unavailable", "SERVICE_UNAVAILABLE", 503);
    }

    public ServiceUnavailableException(String message, String code) {
        super(message, code, 503);
    }
}
