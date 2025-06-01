package com.school.exception;

public class InternalServerErrorException extends SchoolManagementException {
    public InternalServerErrorException(String message) {
        super(message, "INTERNAL_SERVER_ERROR", 500);
    }

    public InternalServerErrorException(String message, Throwable cause) {
        super(message, "INTERNAL_SERVER_ERROR", 500, cause);
    }
}
