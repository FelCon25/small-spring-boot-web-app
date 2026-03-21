package com.example.demo.entity;

import com.example.demo.entity.base.BaseEntity;
import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "users")
public class User extends BaseEntity {

    @Column(unique = true,  nullable = false)
    private String username;
    @Column(unique = true,  nullable = false)
    private String email;
    @Column(nullable = false)
    private String hashPassword;

    public String getUsername() {
        return username;
    }
    public String getEmail() {
        return email;
    }
    public String getHashPassword() {
        return hashPassword;
    }
    public void setUsername(String username) {
        this.username = username;
    }
    public void setEmail(String email) {
        this.email = email;
    }
    public void setHashPassword(String hashPassword) {
        this.hashPassword = hashPassword;
    }
}
