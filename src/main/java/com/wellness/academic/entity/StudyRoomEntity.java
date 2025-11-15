package com.wellness.academic.entity;

import com.wellness.core.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "study_rooms")
@Getter
@Setter
public class StudyRoomEntity extends BaseEntity {

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "topic")
    private String topic;

    @Column(name = "max_participants")
    private Integer maxParticipants;

    @Column(name = "is_active")
    private Boolean isActive;

    @Column(name = "created_by")
    private Long createdBy;

    @Column(name = "created_at")
    private LocalDateTime createdAt;
}
