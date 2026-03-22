package com.example.demo.entity;

import com.example.demo.entity.base.BaseEntity;
import jakarta.persistence.*;

/**
 * Represents a registered user in the system.
 * This entity is mapped to the 'users' table and holds the core identity
 * and credential information required for authentication.
 */
@Entity
@Table(name = "users")
public class User extends BaseEntity {

    /**
     * The unique username chosen during registration.
     */
    @Column(unique = true, nullable = false)
    private String username;

    /**
     * The unique email address used for contact and potentially login.
     */
    @Column(unique = true, nullable = false)
    private String email;

    /**
     * The hashed version of the user's password.
     * Plain text passwords must never be stored in this field.
     */
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
