package com.example.demo.exception;

import java.net.URI;

import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import jakarta.servlet.http.HttpServletRequest;

@RestControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(BaseException.class)
    public ProblemDetail handleBaseException(BaseException ex, HttpServletRequest request) {
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(
                ex.getHttpStatus(), ex.getMessage());
        problemDetail.setTitle(ex.getHttpStatus().getReasonPhrase());
        problemDetail.setInstance(URI.create(request.getRequestURI()));
        return problemDetail;
    }

    @ExceptionHandler(Exception.class)
    public ProblemDetail handleGenericException(Exception ex, HttpServletRequest request) {
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(
                HttpStatus.INTERNAL_SERVER_ERROR,
                "An unexpected error occurred");
        problemDetail.setTitle(HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase());
        problemDetail.setInstance(URI.create(request.getRequestURI()));
        return problemDetail;
    }
}
