package com.example.demo.service;

import com.example.demo.dto.request.RegisterRequest;
import com.example.demo.dto.response.RegisterResponse;
import com.example.demo.entity.User;
import com.example.demo.exception.ResourceAlreadyExistsException;
import com.example.demo.mapper.UserMapper;
import com.example.demo.repository.UserRepository;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;

    public AuthService(UserRepository userRepository, UserMapper userMapper) {
        this.userRepository = userRepository;
        this.userMapper = userMapper;
    }

    public RegisterResponse register(RegisterRequest request) {
        //check if user exists
        if (userRepository.existsByUsername(request.username())) {
            throw new ResourceAlreadyExistsException("User", "username", request.username());
        }
        if (userRepository.existsByEmail(request.email())) {
            throw new ResourceAlreadyExistsException("User", "email", request.email());
        }

        User user = userMapper.toEntity(request);
        user.setHashPassword(request.password());
        User savedUser = userRepository.save(user);
        return userMapper.toResponse(savedUser);

    }
}