package com.wellness.academic.entity;

import com.wellness.core.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.time.LocalDateTime;

@Entity
@Table(name = "study_room_members")
@Getter
@Setter
public class StudyRoomMemberEntity extends BaseEntity {

    @Column(name = "study_room_id", nullable = false)
    private Long studyRoomId;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "role")
    private String role;

    @Column(name = "joined_at")
    private LocalDateTime joinedAt;
}
