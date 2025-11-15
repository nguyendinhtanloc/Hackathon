package com.wellness.academic.entity;

import com.wellness.core.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.time.LocalDateTime;

@Entity
@Table(name = "user_badges")
@Getter
@Setter
public class UserBadgeEntity extends BaseEntity {

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "badge_id", nullable = false)
    private Long badgeId;

    @Column(name = "earned_at")
    private LocalDateTime earnedAt;
}
