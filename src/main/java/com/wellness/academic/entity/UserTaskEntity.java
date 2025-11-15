package com.wellness.academic.entity;

import com.wellness.core.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.time.LocalDate;

/**
 * DEV 4 - User Task Entity
 * Stores academic tasks with deadlines
 */
@Entity
@Table(name = "user_tasks")
@Getter
@Setter
public class UserTaskEntity extends BaseEntity {
    
    @Column(name = "user_id", nullable = false)
    private Long userId;
    
    @Column(name = "title", nullable = false)
    private String title;
    
    @Column(name = "description", columnDefinition = "TEXT")
    private String description;
    
    @Column(name = "deadline")
    private LocalDate deadline;
    
    @Column(name = "priority", length = 20)
    private String priority; // high, medium, low
    
    @Column(name = "status", length = 20)
    private String status; // pending, in_progress, completed
    
    @Column(name = "estimated_hours")
    private Integer estimatedHours;
    
    @Column(name = "actual_hours")
    private Integer actualHours;
}
