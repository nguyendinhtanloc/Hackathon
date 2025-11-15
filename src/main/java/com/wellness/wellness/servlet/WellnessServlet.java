package com.wellness.wellness.servlet;

import com.wellness.core.config.JPAConfig;
import com.wellness.core.util.ApiResponse;
import com.wellness.core.util.JsonUtil;
import com.wellness.wellness.entity.MoodCheckinEntity;
import jakarta.persistence.EntityManager;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.LocalDate;

/**
 * DEV 1 - Wellness Servlet
 * Handles: /api/wellness/*
 * 
 * Endpoints:
 * - POST /api/wellness/checkin - Create mood check-in
 * - GET /api/wellness/dashboard/{userId} - Get dashboard data
 * - GET /api/wellness/mood-trend/{userId} - Get mood trend
 */
public class WellnessServlet extends HttpServlet {
    
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        
        String pathInfo = request.getPathInfo();
        
        if ("/checkin".equals(pathInfo)) {
            handleCheckin(request, response);
        } else {
            sendError(response, "Unknown endpoint");
        }
    }
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        
        String pathInfo = request.getPathInfo();
        
        if (pathInfo != null && pathInfo.startsWith("/dashboard/")) {
            String userId = pathInfo.substring("/dashboard/".length());
            handleGetDashboard(userId, response);
        } else {
            sendError(response, "Unknown endpoint");
        }
    }
    
    /**
     * Handle mood check-in
     * POST /api/wellness/checkin
     */
    private void handleCheckin(HttpServletRequest request, HttpServletResponse response) 
            throws IOException {
        
        try {
            // Parse request body
            String body = request.getReader().lines().reduce("", (acc, line) -> acc + line);
            MoodCheckinEntity checkin = JsonUtil.fromJson(body, MoodCheckinEntity.class);
            
            // Set default date if not provided
            if (checkin.getCheckinDate() == null) {
                checkin.setCheckinDate(LocalDate.now());
            }
            
            // Save to database using JPA
            EntityManager em = JPAConfig.getEntityManager();
            em.getTransaction().begin();
            em.persist(checkin);
            em.getTransaction().commit();
            em.close();
            
            // Return success response
            ApiResponse<MoodCheckinEntity> apiResponse = ApiResponse.success(checkin, "Check-in saved successfully");
            response.getWriter().write(JsonUtil.toJson(apiResponse));
            
        } catch (Exception e) {
            e.printStackTrace();
            sendError(response, "Failed to save check-in: " + e.getMessage());
        }
    }
    
    /**
     * Get dashboard data
     * GET /api/wellness/dashboard/{userId}
     */
    private void handleGetDashboard(String userId, HttpServletResponse response) 
            throws IOException {
        
        try {
            // TODO: Implement dashboard logic
            // 1. Get recent check-ins
            // 2. Calculate wellness score
            // 3. Get mood trends
            // 4. Check for alerts
            
            String dashboardData = "Dashboard data for user: " + userId;
            ApiResponse<String> apiResponse = ApiResponse.success(dashboardData);
            response.getWriter().write(JsonUtil.toJson(apiResponse));
            
        } catch (Exception e) {
            e.printStackTrace();
            sendError(response, "Failed to load dashboard: " + e.getMessage());
        }
    }
    
    /**
     * Send error response
     */
    private void sendError(HttpServletResponse response, String message) throws IOException {
        response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
        ApiResponse<String> apiResponse = ApiResponse.error(message);
        response.getWriter().write(JsonUtil.toJson(apiResponse));
    }
}
