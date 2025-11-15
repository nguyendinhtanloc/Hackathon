package com.wellness.ai.entity;

import com.wellness.core.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

/**
 * DEV 2 - AI Conversation Entity
 * Stores chat messages between user and AI companion
 */
@Entity
@Table(name = "ai_conversations")
@Getter
@Setter
public class ConversationEntity extends BaseEntity {
    
    @Column(name = "user_id", nullable = false)
    private Long userId;
    
    @Column(name = "message", columnDefinition = "TEXT", nullable = false)
    private String message;
    
    @Column(name = "response", columnDefinition = "TEXT")
    private String response;
    
    @Column(name = "sentiment", length = 50)
    private String sentiment; // positive, negative, neutral, crisis
    
    @Column(name = "is_crisis")
    private Boolean isCrisis;
    
    @Column(name = "keywords", columnDefinition = "TEXT")
    private String keywords; // JSON array of detected keywords
}
