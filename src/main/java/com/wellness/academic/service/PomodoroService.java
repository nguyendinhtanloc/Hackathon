package com.wellness.academic.service;

import com.wellness.academic.entity.PomodoroSessionEntity;
import com.wellness.academic.entity.PointsHistoryEntity;
import com.wellness.academic.entity.UserPointsEntity;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Service for managing Pomodoro sessions and points
 */
public class PomodoroService {
    
    private EntityManager entityManager;
    private final int POINTS_PER_POMODORO = 10;
    
    public PomodoroService(EntityManager entityManager) {
        this.entityManager = entityManager;
    }
    
    public PomodoroSessionEntity startPomodoro(Long userId, Long taskId, Integer durationMinutes) {
        PomodoroSessionEntity session = new PomodoroSessionEntity();
        session.setUserId(userId);
        session.setTaskId(taskId);
        session.setDurationMinutes(durationMinutes != null ? durationMinutes : 25);
        session.setBreaksTaken(0);
        session.setCompleted(false);
        session.setStartedAt(LocalDateTime.now());
        session.setPointsAwarded(0);
        session.setCreatedAt(LocalDateTime.now());
        session.setUpdatedAt(LocalDateTime.now());
        
        entityManager.persist(session);
        return session;
    }
    
    public PomodoroSessionEntity completePomodoro(Long pomodoroId, Integer breaksTaken) {
        PomodoroSessionEntity session = entityManager.find(PomodoroSessionEntity.class, pomodoroId);
        if (session != null) {
            session.setCompleted(true);
            session.setCompletedAt(LocalDateTime.now());
            session.setBreaksTaken(breaksTaken != null ? breaksTaken : 0);
            session.setPointsAwarded(POINTS_PER_POMODORO);
            session.setUpdatedAt(LocalDateTime.now());
            session = entityManager.merge(session);
            
            // Award points to user
            addPoints(session.getUserId(), POINTS_PER_POMODORO, "pomodoro", pomodoroId, 
                     "Completed Pomodoro session: " + session.getDurationMinutes() + " minutes");
        }
        return session;
    }
    
    public List<PomodoroSessionEntity> getUserPomodoros(Long userId) {
        TypedQuery<PomodoroSessionEntity> query = entityManager.createQuery(
            "SELECT p FROM PomodoroSessionEntity p WHERE p.userId = :userId ORDER BY p.startedAt DESC",
            PomodoroSessionEntity.class
        );
        query.setParameter("userId", userId);
        return query.getResultList();
    }
    
    public Integer getTotalPomodorosToday(Long userId) {
        TypedQuery<Long> query = entityManager.createQuery(
            "SELECT COUNT(p) FROM PomodoroSessionEntity p WHERE p.userId = :userId AND p.completed = true " +
            "AND FUNCTION('DATE', p.completedAt) = CURRENT_DATE",
            Long.class
        );
        query.setParameter("userId", userId);
        return query.getSingleResult().intValue();
    }
    
    public void addPoints(Long userId, Integer points, String sourceType, Long sourceId, String reason) {
        // Create history record
        PointsHistoryEntity history = new PointsHistoryEntity();
        history.setUserId(userId);
        history.setSourceType(sourceType);
        history.setSourceId(sourceId);
        history.setPointsDelta(points);
        history.setReason(reason);
        history.setCreatedAt(LocalDateTime.now());
        entityManager.persist(history);
        
        // Update user total points
        UserPointsEntity userPoints = entityManager.createQuery(
            "SELECT p FROM UserPointsEntity p WHERE p.userId = :userId",
            UserPointsEntity.class
        ).setParameter("userId", userId).getResultStream().findFirst().orElse(null);
        
        if (userPoints == null) {
            userPoints = new UserPointsEntity();
            userPoints.setUserId(userId);
            userPoints.setTotalPoints(points);
            userPoints.setLevel(1);
            userPoints.setPointsToNextLevel(100);
            userPoints.setUpdatedAt(LocalDateTime.now());
            userPoints.setCreatedAt(LocalDateTime.now());
            entityManager.persist(userPoints);
        } else {
            userPoints.setTotalPoints(userPoints.getTotalPoints() + points);
            userPoints.setUpdatedAt(LocalDateTime.now());
            entityManager.merge(userPoints);
        }
    }
    
    public Integer getUserTotalPoints(Long userId) {
        TypedQuery<UserPointsEntity> query = entityManager.createQuery(
            "SELECT p FROM UserPointsEntity p WHERE p.userId = :userId",
            UserPointsEntity.class
        );
        query.setParameter("userId", userId);
        List<UserPointsEntity> results = query.getResultList();
        return results.isEmpty() ? 0 : results.get(0).getTotalPoints();
    }
}
