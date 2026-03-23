package com.example.demo.dto.request;

import jakarta.validation.constraints.*;

public record RegisterRequest(
        @NotBlank(message = "Username cannot be blank")
        @Size(min = 6, message = "Username must be at least 6 characters long")
        @Size(max = 16, message = "Username must be at most 16 characters long")
        String username,

        @Email(message = "Invalid email format")
        @NotBlank(message = "Email cannot be blank")
        String email,

        @NotBlank(message = "Password cannot be blank")
        @Size(min = 8, message = "Password must be at least 8 characters long")
        @Size(max = 200, message = "Password must be at most 200 characters long")
        String password
) {}
