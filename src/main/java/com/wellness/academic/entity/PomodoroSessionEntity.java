package com.wellness.academic.entity;

import com.wellness.core.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.time.LocalDateTime;

@Entity
@Table(name = "pomodoro_sessions")
@Getter
@Setter
public class PomodoroSessionEntity extends BaseEntity {

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "task_id")
    private Long taskId;

    @Column(name = "duration_minutes")
    private Integer durationMinutes;

    @Column(name = "breaks_taken")
    private Integer breaksTaken;

    @Column(name = "completed")
    private Boolean completed;

    @Column(name = "started_at")
    private LocalDateTime startedAt;

    @Column(name = "completed_at")
    private LocalDateTime completedAt;

    @Column(name = "points_awarded")
    private Integer pointsAwarded;
}
