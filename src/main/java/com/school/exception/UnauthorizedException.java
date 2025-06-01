package com.school.exception;

public class UnauthorizedException extends SchoolManagementException {
    public UnauthorizedException(String message) {
        super(message, "UNAUTHORIZED", 401);
    }
}
