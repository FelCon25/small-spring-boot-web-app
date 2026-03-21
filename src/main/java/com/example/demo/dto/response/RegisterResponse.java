package com.example.demo.dto.response;

import java.time.LocalDateTime;
import java.util.UUID;

public record RegisterResponse(
    UUID id,
    String username,
    String email,
    LocalDateTime createTime,
    LocalDateTime updateTime
) {}