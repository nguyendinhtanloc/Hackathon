package com.wellness.academic.entity;

import com.wellness.core.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

/**
 * DEV 4 - Challenge Entity
 * Stores wellness challenges (7-day gratitude, 30-day meditation)
 */
@Entity
@Table(name = "challenges")
@Getter
@Setter
public class ChallengeEntity extends BaseEntity {
    
    @Column(name = "name", nullable = false)
    private String name;
    
    @Column(name = "description", columnDefinition = "TEXT")
    private String description;
    
    @Column(name = "duration_days", nullable = false)
    private Integer durationDays;
    
    @Column(name = "points_reward")
    private Integer pointsReward;
    
    @Column(name = "category", length = 50)
    private String category; // gratitude, meditation, exercise, social
    
    @Column(name = "is_active")
    private Boolean isActive;
}
