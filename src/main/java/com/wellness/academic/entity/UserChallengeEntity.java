package com.wellness.academic.entity;

import com.wellness.core.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.time.LocalDateTime;

@Entity
@Table(name = "user_challenges")
@Getter
@Setter
public class UserChallengeEntity extends BaseEntity {

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "challenge_id", nullable = false)
    private Long challengeId;

    @Column(name = "progress")
    private Integer progress;

    @Column(name = "completed")
    private Boolean completed;

    @Column(name = "started_at")
    private LocalDateTime startedAt;

    @Column(name = "completed_at")
    private LocalDateTime completedAt;
}
