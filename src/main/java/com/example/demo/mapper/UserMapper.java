package com.example.demo.mapper;

import com.example.demo.dto.request.RegisterRequest;
import com.example.demo.dto.response.RegisterResponse;
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

    public RegisterResponse toResponse(User user) {
        return new RegisterResponse(
            user.getId(),
            user.getUsername(),
            user.getEmail(),
            user.getCreateTime(),
            user.getUpdateTime()
        );
    }
 }
