package com.example.demo.repository;

import com.example.demo.entity.Session;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

public interface SessionRepository extends JpaRepository<Session, UUID> {

    List<Session> findByUserId(UUID userId);

}
