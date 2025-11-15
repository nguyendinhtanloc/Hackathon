package com.wellness.wellness.entity;

import com.wellness.core.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.time.LocalDate;

/**
 * DEV 1 - Mood Check-in Entity
 * Stores daily emotional check-ins with facial analysis and heart rate
 */
@Entity
@Table(name = "mood_checkins")
@Getter
@Setter
public class MoodCheckinEntity extends BaseEntity {
    
    @Column(name = "user_id", nullable = false)
    private Long userId;
    
    @Column(name = "checkin_date", nullable = false)
    private LocalDate checkinDate;
    
    @Column(name = "emotion", length = 50)
    private String emotion; // happy, sad, anxious, stressed, neutral
    
    @Column(name = "note", length = 500)
    private String note;
    
    @Column(name = "wellness_score")
    private Integer wellnessScore; // 0-100
    
    @Column(name = "heart_rate")
    private Integer heartRate;
    
    @Column(name = "face_analysis_data", columnDefinition = "TEXT")
    private String faceAnalysisData; // JSON string from ML model
    
    @Column(name = "emoji")
    private String emoji; // 😊😢😰😡😐
}
