package com.wellness.academic.entity;

import com.wellness.core.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.time.LocalDateTime;

@Entity
@Table(name = "points_histories")
@Getter
@Setter
public class PointsHistoryEntity extends BaseEntity {

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "source_type")
    private String sourceType;

    @Column(name = "source_id")
    private Long sourceId;

    @Column(name = "points_delta")
    private Integer pointsDelta;

    @Column(name = "reason", columnDefinition = "TEXT")
    private String reason;

    @Column(name = "created_at")
    private LocalDateTime createdAt;
}
