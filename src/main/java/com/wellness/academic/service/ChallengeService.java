package com.wellness.academic.service;

import com.wellness.academic.entity.ChallengeEntity;
import com.wellness.academic.entity.UserChallengeEntity;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Service for managing challenges and user challenge participation
 */
public class ChallengeService {
    
    private EntityManager entityManager;
    
    public ChallengeService(EntityManager entityManager) {
        this.entityManager = entityManager;
    }
    
    public List<ChallengeEntity> getActiveChallenges() {
        TypedQuery<ChallengeEntity> query = entityManager.createQuery(
            "SELECT c FROM ChallengeEntity c WHERE c.isActive = true ORDER BY c.createdAt DESC",
            ChallengeEntity.class
        );
        return query.getResultList();
    }
    
    public UserChallengeEntity startChallenge(Long userId, Long challengeId) {
        // Check if already participating
        TypedQuery<UserChallengeEntity> checkQuery = entityManager.createQuery(
            "SELECT uc FROM UserChallengeEntity uc WHERE uc.userId = :userId AND uc.challengeId = :challengeId",
            UserChallengeEntity.class
        );
        checkQuery.setParameter("userId", userId);
        checkQuery.setParameter("challengeId", challengeId);
        
        List<UserChallengeEntity> existing = checkQuery.getResultList();
        if (!existing.isEmpty()) {
            return existing.get(0);
        }
        
        // Create new participation
        UserChallengeEntity userChallenge = new UserChallengeEntity();
        userChallenge.setUserId(userId);
        userChallenge.setChallengeId(challengeId);
        userChallenge.setProgress(0);
        userChallenge.setCompleted(false);
        userChallenge.setStartedAt(LocalDateTime.now());
        userChallenge.setCreatedAt(LocalDateTime.now());
        userChallenge.setUpdatedAt(LocalDateTime.now());
        
        entityManager.persist(userChallenge);
        return userChallenge;
    }
    
    public UserChallengeEntity updateProgress(Long userChallengeId, Integer progress) {
        UserChallengeEntity userChallenge = entityManager.find(UserChallengeEntity.class, userChallengeId);
        if (userChallenge != null) {
            userChallenge.setProgress(progress);
            userChallenge.setUpdatedAt(LocalDateTime.now());
            userChallenge = entityManager.merge(userChallenge);
        }
        return userChallenge;
    }
    
    public UserChallengeEntity completeChallenge(Long userChallengeId) {
        UserChallengeEntity userChallenge = entityManager.find(UserChallengeEntity.class, userChallengeId);
        if (userChallenge != null) {
            userChallenge.setCompleted(true);
            userChallenge.setCompletedAt(LocalDateTime.now());
            userChallenge.setUpdatedAt(LocalDateTime.now());
            userChallenge = entityManager.merge(userChallenge);
        }
        return userChallenge;
    }
    
    public List<UserChallengeEntity> getUserActiveChallenges(Long userId) {
        TypedQuery<UserChallengeEntity> query = entityManager.createQuery(
            "SELECT uc FROM UserChallengeEntity uc WHERE uc.userId = :userId AND uc.completed = false",
            UserChallengeEntity.class
        );
        query.setParameter("userId", userId);
        return query.getResultList();
    }
}
