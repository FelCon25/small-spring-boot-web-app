package com.example.demo.dto.response;

import java.time.Instant;
import java.util.UUID;

public record AuthResponse(
    UUID id,
    String username,
    String email,
    Instant createTime,
    Instant updateTime
) {}