package com.example.demo.entity.base;

import jakarta.persistence.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.util.UUID;

@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
public abstract class BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @CreatedDate
    @Column(nullable = false, updatable = false)
    private LocalDateTime createTime;

    @LastModifiedDate
    @Column(nullable = false)
    private LocalDateTime updateTime;

    public UUID getId() {
        return id;
    }
    public LocalDateTime getCreateTime() {
        return createTime;
    }
    public LocalDateTime getUpdateTime() {
        return updateTime;
    }
}
