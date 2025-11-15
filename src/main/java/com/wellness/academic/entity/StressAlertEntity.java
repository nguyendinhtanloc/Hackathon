package com.wellness.academic.entity;

import com.wellness.core.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.time.LocalDateTime;

@Entity
@Table(name = "stress_alerts")
@Getter
@Setter
public class StressAlertEntity extends BaseEntity {

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "detected_at")
    private LocalDateTime detectedAt;

    @Column(name = "workload_summary", columnDefinition = "TEXT")
    private String workloadSummary; // JSON string

    @Column(name = "severity")
    private String severity;

    @Column(name = "resolved")
    private Boolean resolved;

    @Column(name = "resolved_at")
    private LocalDateTime resolvedAt;
}
