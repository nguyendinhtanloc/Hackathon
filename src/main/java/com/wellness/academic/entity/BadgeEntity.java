package com.wellness.academic.entity;

import com.wellness.core.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.time.LocalDateTime;

@Entity
@Table(name = "badges")
@Getter
@Setter
public class BadgeEntity extends BaseEntity {

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Column(name = "icon_url")
    private String iconUrl;

    @Column(name = "criteria", columnDefinition = "TEXT")
    private String criteria; // JSON string

    @Column(name = "rarity")
    private String rarity;

    @Column(name = "created_at")
    private LocalDateTime createdAt;
}
