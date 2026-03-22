package com.example.demo.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.Instant;
import java.util.UUID;

@Schema(description = "The response object returned upon successful registration")
public record RegisterResponse(
    @Schema(description = "Unique identifier of the user", example = "123e4567-e89b-12d3-a456-426614174000")
    UUID id,
    
    @Schema(description = "The registered username", example = "johndoe123")
    String username,
    
    @Schema(description = "The registered email", example = "john.doe@example.com")
    String email,
    
    @Schema(description = "The exact time the user was created")
    Instant createTime,
    
    @Schema(description = "The last time the user record was updated")
    Instant updateTime
) {}