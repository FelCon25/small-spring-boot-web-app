package com.example.demo.exception;

import org.springframework.http.HttpStatus;

public class ResourceAlreadyExistsException extends BaseException {
    public ResourceAlreadyExistsException(String resource, String field, String value) {
        super(
                String.format("%s with %s '%s' already exists", resource, field, value),
                HttpStatus.CONFLICT);
    }
}
