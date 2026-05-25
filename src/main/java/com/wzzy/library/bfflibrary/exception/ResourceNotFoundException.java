package com.wzzy.library.bfflibrary.exception;

public class ResourceNotFoundException extends BaseServiceException {
    public ResourceNotFoundException(String message) {
        super(message, "RESOURCE_NOT_FOUND", 404);
    }

    public ResourceNotFoundException(String resourceName, String identifier) {
        super(resourceName + " with identifier " + identifier + " not found",
              "RESOURCE_NOT_FOUND", 404);
    }
}
