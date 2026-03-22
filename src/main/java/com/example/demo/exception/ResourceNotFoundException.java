package com.example.demo.exception;

import org.springframework.http.HttpStatus;

public class ResourceNotFoundException extends BaseException {

    public ResourceNotFoundException(String resource, String field, String value) {
        super(
                String.format("%s with %s '%s' was not found", resource, field, value),
                HttpStatus.NOT_FOUND);
    }
}
