package com.example.demo.mapper;

import com.example.demo.dto.request.RegisterRequest;
import com.example.demo.dto.response.AuthResponse;
import com.example.demo.entity.User;
import org.springframework.stereotype.Component;

@Component
public class UserMapper {
    public User toEntity(RegisterRequest request) {
        User user = new User();
        user.setUsername(request.username());
        user.setEmail(request.email());
        return user;
    }

    public AuthResponse toResponse(User user) {
        return new AuthResponse(
            user.getId(),
            user.getUsername(),
            user.getEmail(),
            user.getCreateTime(),
            user.getUpdateTime()
        );
    }
 }
