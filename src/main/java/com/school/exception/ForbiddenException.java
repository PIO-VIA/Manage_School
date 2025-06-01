package com.school.exception;

public class ForbiddenException extends SchoolManagementException {
    public ForbiddenException(String message) {
        super(message, "FORBIDDEN", 403);
    }
}
