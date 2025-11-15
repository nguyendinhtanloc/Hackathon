package com.wellness.academic.repository;

import com.wellness.academic.entity.UserTaskEntity;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import java.util.List;
import java.util.Optional;

/**
 * DAO for UserTaskEntity
 */
public class UserTaskRepository {
    
    private EntityManager entityManager;
    
    public UserTaskRepository(EntityManager entityManager) {
        this.entityManager = entityManager;
    }
    
    public UserTaskEntity save(UserTaskEntity task) {
        if (task.getId() == null) {
            entityManager.persist(task);
        } else {
            task = entityManager.merge(task);
        }
        return task;
    }
    
    public Optional<UserTaskEntity> findById(Long id) {
        return Optional.ofNullable(entityManager.find(UserTaskEntity.class, id));
    }
    
    public List<UserTaskEntity> findByUserId(Long userId) {
        TypedQuery<UserTaskEntity> query = entityManager.createQuery(
            "SELECT t FROM UserTaskEntity t WHERE t.userId = :userId ORDER BY t.deadline ASC",
            UserTaskEntity.class
        );
        query.setParameter("userId", userId);
        return query.getResultList();
    }
    
    public List<UserTaskEntity> findByUserIdAndStatus(Long userId, String status) {
        TypedQuery<UserTaskEntity> query = entityManager.createQuery(
            "SELECT t FROM UserTaskEntity t WHERE t.userId = :userId AND t.status = :status",
            UserTaskEntity.class
        );
        query.setParameter("userId", userId);
        query.setParameter("status", status);
        return query.getResultList();
    }
    
    public void delete(Long id) {
        UserTaskEntity task = entityManager.find(UserTaskEntity.class, id);
        if (task != null) {
            entityManager.remove(task);
        }
    }
}
