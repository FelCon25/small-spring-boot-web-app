package com.example.demo.entity.base;

import jakarta.persistence.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.Instant;
import java.util.UUID;

@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
public abstract class BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @CreatedDate
    @Column(nullable = false, updatable = false)
    private Instant createTime;

    @LastModifiedDate
    @Column(nullable = false)
    private Instant updateTime;

    public UUID getId() {
        return id;
    }
    public Instant getCreateTime() {
        return createTime;
    }
    public Instant getUpdateTime() {
        return updateTime;
    }
}
