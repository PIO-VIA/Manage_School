package com.school.exception;

public class ConflictException extends SchoolManagementException {
    public ConflictException(String message) {
        super(message, "CONFLICT", 409);
    }
}
