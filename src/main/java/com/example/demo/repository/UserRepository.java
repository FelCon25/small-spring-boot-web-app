package com.example.demo.repository;

import com.example.demo.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

/**
 * Spring Data JPA repository for the {@link User} entity.
 * Provides standard CRUD and pagination operations, plus custom query methods.
 */
public interface UserRepository extends JpaRepository<User, UUID> {
    
    /**
     * Checks if a user with the given username already exists in the database.
     *
     * @param username the username to check for existence
     * @return true if a user with the username exists, false otherwise
     */
    boolean existsByUsername(String username);

    /**
     * Checks if a user with the given email already exists in the database.
     *
     * @param email the email address to check for existence
     * @return true if a user with the email exists, false otherwise
     */
    boolean existsByEmail(String email);
}
