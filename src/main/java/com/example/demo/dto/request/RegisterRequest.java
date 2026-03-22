package com.example.demo.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;

@Schema(description = "Object containing the data to register a new user")
public record RegisterRequest(
        @Schema(description = "The chosen username for the account", example = "johndoe123")
        @NotBlank(message = "Username cannot be blank")
        @Size(min = 6, message = "Username must be at least 6 characters long")
        @Size(max = 16, message = "Username must be at most 16 characters long")
        String username,

        @Schema(description = "The email address of the user", example = "john.doe@example.com")
        @Email(message = "Invalid email format")
        @NotBlank(message = "Email cannot be blank")
        String email,

        @Schema(description = "The plain text password", example = "MySecretPassword!2024")
        @NotBlank(message = "Password cannot be blank")
        @Size(min = 8, message = "Password must be at least 8 characters long")
        @Size(max = 200, message = "Password must be at most 200 characters long")
        String password
) {}
