package com.school.exception;

// Exceptions spécifiques
public class NotFoundException extends SchoolManagementException {
    public NotFoundException(String message) {
        super(message, "NOT_FOUND", 404);
    }
}
