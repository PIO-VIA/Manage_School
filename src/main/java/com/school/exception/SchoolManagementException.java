package com.school.exception;

import lombok.Getter;

@Getter
public class SchoolManagementException extends RuntimeException {
    private final String errorCode;
    private final int statusCode;

    public SchoolManagementException(String message, String errorCode, int statusCode) {
        super(message);
        this.errorCode = errorCode;
        this.statusCode = statusCode;
    }

    public SchoolManagementException(String message, String errorCode, int statusCode, Throwable cause) {
        super(message, cause);
        this.errorCode = errorCode;
        this.statusCode = statusCode;
    }
}

