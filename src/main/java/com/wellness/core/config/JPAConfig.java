package com.wellness.core.config;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;

/**
 * JPA Configuration for Supabase PostgreSQL
 * Singleton pattern for EntityManagerFactory
 */
public class JPAConfig {
    
    private static final String PERSISTENCE_UNIT_NAME = "WellnessPU";
    private static EntityManagerFactory emf;
    
    static {
        try {
            emf = Persistence.createEntityManagerFactory(PERSISTENCE_UNIT_NAME);
        } catch (Exception e) {
            e.printStackTrace();
            throw new ExceptionInInitializerError("Failed to initialize JPA");
        }
    }
    
    public static EntityManager getEntityManager() {
        return emf.createEntityManager();
    }
    
    public static void close() {
        if (emf != null && emf.isOpen()) {
            emf.close();
        }
    }
}
