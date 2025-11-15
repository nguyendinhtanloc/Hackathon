package com.wellness.academic.entity;

import com.wellness.core.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.time.LocalDateTime;

@Entity
@Table(name = "leaderboard_entries")
@Getter
@Setter
public class LeaderboardEntryEntity extends BaseEntity {

    @Column(name = "period")
    private String period;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "score")
    private Integer score;

    @Column(name = "rank")
    private Integer rank;

    @Column(name = "computed_at")
    private LocalDateTime computedAt;
}
