package com.wellness.wellness.entity;

import com.wellness.core.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.time.LocalDate;

/**
 * DEV 1 - Wellness Score Entity
 * Stores daily wellness scores calculated from multiple factors
 */
@Entity
@Table(name = "wellness_scores")
@Getter
@Setter
public class WellnessScoreEntity extends BaseEntity {
    
    @Column(name = "user_id", nullable = false)
    private Long userId;
    
    @Column(name = "date", nullable = false)
    private LocalDate date;
    
    @Column(name = "score", nullable = false)
    private Integer score; // 0-100
    
    @Column(name = "factors", columnDefinition = "TEXT")
    private String factors; // JSON: {"mood": 70, "heartRate": 80, "stress": 30, "hrv": 85}
    
    @Column(name = "heart_rate")
    private Integer heartRate;
    
    @Column(name = "hrv")
    private Integer hrv; // Heart Rate Variability in ms
    
    @Column(name = "stress_score")
    private Integer stressScore; // 0-100 (lower is better)
}
