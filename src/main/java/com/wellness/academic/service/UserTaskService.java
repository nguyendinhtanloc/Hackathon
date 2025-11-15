package com.wellness.academic.service;

import com.wellness.academic.entity.UserTaskEntity;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Service for managing academic tasks
 */
public class UserTaskService {
    
    private EntityManager entityManager;
    
    public UserTaskService(EntityManager entityManager) {
        this.entityManager = entityManager;
    }
    
    public UserTaskEntity createTask(Long userId, String title, String description, 
                                     LocalDate deadline, String priority, Integer estimatedHours) {
        UserTaskEntity task = new UserTaskEntity();
        task.setUserId(userId);
        task.setTitle(title);
        task.setDescription(description);
        task.setDeadline(deadline);
        task.setPriority(priority != null ? priority : "medium");
        task.setStatus("pending");
        task.setEstimatedHours(estimatedHours);
        task.setActualHours(0);
        task.setCreatedAt(LocalDateTime.now());
        task.setUpdatedAt(LocalDateTime.now());
        
        entityManager.persist(task);
        return task;
    }
    
    public UserTaskEntity updateTask(Long taskId, String title, String description, 
                                     LocalDate deadline, String priority, String status) {
        UserTaskEntity task = entityManager.find(UserTaskEntity.class, taskId);
        if (task != null) {
            task.setTitle(title);
            task.setDescription(description);
            task.setDeadline(deadline);
            task.setPriority(priority);
            task.setStatus(status);
            task.setUpdatedAt(LocalDateTime.now());
            task = entityManager.merge(task);
        }
        return task;
    }
    
    public Optional<UserTaskEntity> getTask(Long taskId) {
        UserTaskEntity task = entityManager.find(UserTaskEntity.class, taskId);
        return Optional.ofNullable(task);
    }
    
    public List<UserTaskEntity> getUserTasks(Long userId) {
        TypedQuery<UserTaskEntity> query = entityManager.createQuery(
            "SELECT t FROM UserTaskEntity t WHERE t.userId = :userId ORDER BY t.deadline ASC",
            UserTaskEntity.class
        );
        query.setParameter("userId", userId);
        return query.getResultList();
    }
    
    public List<UserTaskEntity> getUserTasksByStatus(Long userId, String status) {
        TypedQuery<UserTaskEntity> query = entityManager.createQuery(
            "SELECT t FROM UserTaskEntity t WHERE t.userId = :userId AND t.status = :status ORDER BY t.deadline ASC",
            UserTaskEntity.class
        );
        query.setParameter("userId", userId);
        query.setParameter("status", status);
        return query.getResultList();
    }
    
    public Integer calculateWorkload(Long userId) {
        List<UserTaskEntity> tasks = getUserTasksByStatus(userId, "pending");
        int totalHours = 0;
        for (UserTaskEntity task : tasks) {
            if (task.getEstimatedHours() != null) {
                totalHours += task.getEstimatedHours();
            }
        }
        return totalHours;
    }
    
    public void deleteTask(Long taskId) {
        UserTaskEntity task = entityManager.find(UserTaskEntity.class, taskId);
        if (task != null) {
            entityManager.remove(task);
        }
    }
}
