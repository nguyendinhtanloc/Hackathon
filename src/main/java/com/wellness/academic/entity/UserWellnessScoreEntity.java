package com.wellness.academic.entity;

import com.wellness.core.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "user_wellness_scores")
@Getter
@Setter
public class UserWellnessScoreEntity extends BaseEntity {

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "date")
    private LocalDate date;

    @Column(name = "wellness_score")
    private Integer wellnessScore;

    @Column(name = "mood_entries", columnDefinition = "TEXT")
    private String moodEntries; // JSON string

    @Column(name = "created_at")
    private LocalDateTime createdAt;
}
