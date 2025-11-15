package com.wellness.academic.entity;

import com.wellness.core.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.time.LocalDateTime;

@Entity
@Table(name = "rewards")
@Getter
@Setter
public class RewardEntity extends BaseEntity {

    @Column(name = "title", nullable = false)
    private String title;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Column(name = "points_cost")
    private Integer pointsCost;

    @Column(name = "partner")
    private String partner;

    @Column(name = "reward_type")
    private String rewardType;

    @Column(name = "quantity_available")
    private Integer quantityAvailable;

    @Column(name = "is_active")
    private Boolean isActive;

    @Column(name = "created_at")
    private LocalDateTime createdAt;
}
