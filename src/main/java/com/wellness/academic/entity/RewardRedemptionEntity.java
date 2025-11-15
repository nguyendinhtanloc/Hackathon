package com.wellness.academic.entity;

import com.wellness.core.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.time.LocalDateTime;

@Entity
@Table(name = "reward_redemptions")
@Getter
@Setter
public class RewardRedemptionEntity extends BaseEntity {

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "reward_id", nullable = false)
    private Long rewardId;

    @Column(name = "code")
    private String code;

    @Column(name = "redeemed_at")
    private LocalDateTime redeemedAt;

    @Column(name = "used")
    private Boolean used;
}
