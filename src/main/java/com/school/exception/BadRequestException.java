package com.school.exception;

public class BadRequestException extends SchoolManagementException {
    public BadRequestException(String message) {
        super(message, "BAD_REQUEST", 400);
    }
}
