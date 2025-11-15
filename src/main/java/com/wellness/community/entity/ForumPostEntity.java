package com.wellness.community.entity;

import com.wellness.core.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

/**
 * DEV 3 - Forum Post Entity
 * Stores anonymous forum posts
 */
@Entity
@Table(name = "forum_posts")
@Getter
@Setter
public class ForumPostEntity extends BaseEntity {
    
    @Column(name = "user_id", nullable = false)
    private Long userId;
    
    @Column(name = "content", columnDefinition = "TEXT", nullable = false)
    private String content;
    
    @Column(name = "tags", length = 500)
    private String tags; // JSON array: ["anxiety", "stress"]
    
    @Column(name = "is_anonymous")
    private Boolean isAnonymous;
    
    @Column(name = "likes_count")
    private Integer likesCount;
    
    @Column(name = "comments_count")
    private Integer commentsCount;
}
