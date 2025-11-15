package com.wellness.academic.entity;

import com.wellness.core.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.time.LocalDateTime;

@Entity
@Table(name = "user_points")
@Getter
@Setter
public class UserPointsEntity extends BaseEntity {

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "total_points")
    private Integer totalPoints;

    @Column(name = "level")
    private Integer level;

    @Column(name = "points_to_next_level")
    private Integer pointsToNextLevel;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}
