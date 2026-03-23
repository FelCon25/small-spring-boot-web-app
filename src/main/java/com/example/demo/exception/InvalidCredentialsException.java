package com.example.demo.exception;

import org.springframework.http.HttpStatus;

/**
 * Thrown when authentication fails due to invalid email or password.
 * Returns HTTP 401 Unauthorized. Uses a generic message to avoid
 * revealing whether the email or password is specifically incorrect.
 */
public class InvalidCredentialsException extends BaseException {
    public InvalidCredentialsException() {
        super("Invalid email or password", HttpStatus.UNAUTHORIZED);
    }
}
