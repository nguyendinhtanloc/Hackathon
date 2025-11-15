package com.wellness.academic.servlet;

import com.wellness.core.util.ApiResponse;
import com.wellness.core.util.JsonUtil;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * DEV 4 - Academic Servlet
 * Handles: /api/academic/*
 * 
 * Endpoints:
 * - POST /api/academic/tasks - Create task
 * - GET /api/academic/workload/{userId} - Calculate workload
 * - POST /api/academic/pomodoro/start - Start Pomodoro
 * - GET /api/academic/study-rooms - Get study rooms
 */
public class AcademicServlet extends HttpServlet {
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        
        String pathInfo = request.getPathInfo();
        
        if (pathInfo != null && pathInfo.startsWith("/workload/")) {
            String userId = pathInfo.substring("/workload/".length());
            handleCalculateWorkload(userId, response);
        } else if ("/study-rooms".equals(pathInfo)) {
            handleGetStudyRooms(response);
        } else {
            sendError(response, "Unknown endpoint");
        }
    }
    
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        
        String pathInfo = request.getPathInfo();
        
        if ("/tasks".equals(pathInfo)) {
            handleCreateTask(request, response);
        } else if (pathInfo != null && pathInfo.startsWith("/pomodoro/")) {
            handlePomodoro(request, response);
        } else {
            sendError(response, "Unknown endpoint");
        }
    }
    
    /**
     * Create task
     * POST /api/academic/tasks
     */
    private void handleCreateTask(HttpServletRequest request, HttpServletResponse response) 
            throws IOException {
        // TODO: DEV 4 - Implement create task
        // 1. Parse request body
        // 2. Save task to database
        // 3. Calculate workload impact
        
        ApiResponse<String> apiResponse = ApiResponse.success("Task created");
        response.getWriter().write(JsonUtil.toJson(apiResponse));
    }
    
    /**
     * Calculate workload
     * GET /api/academic/workload/{userId}
     */
    private void handleCalculateWorkload(String userId, HttpServletResponse response) 
            throws IOException {
        // TODO: DEV 4 - Implement workload calculation
        // 1. Get all pending tasks
        // 2. Calculate total hours needed
        // 3. Check if workload is reasonable
        // 4. Suggest rescheduling if overloaded
        
        ApiResponse<String> apiResponse = ApiResponse.success("Workload data for user: " + userId);
        response.getWriter().write(JsonUtil.toJson(apiResponse));
    }
    
    /**
     * Start Pomodoro session
     * POST /api/academic/pomodoro/start
     */
    private void handlePomodoro(HttpServletRequest request, HttpServletResponse response) 
            throws IOException {
        // TODO: DEV 4 - Implement Pomodoro timer
        // 1. Create Pomodoro session
        // 2. Track time
        // 3. Award points for completion
        
        ApiResponse<String> apiResponse = ApiResponse.success("Pomodoro started");
        response.getWriter().write(JsonUtil.toJson(apiResponse));
    }
    
    /**
     * Get study rooms
     * GET /api/academic/study-rooms
     */
    private void handleGetStudyRooms(HttpServletResponse response) throws IOException {
        // TODO: DEV 4 - Implement study rooms
        // 1. Get active study rooms
        // 2. Return room details (topic, participants)
        
        ApiResponse<String> apiResponse = ApiResponse.success("List of study rooms");
        response.getWriter().write(JsonUtil.toJson(apiResponse));
    }
    
    private void sendError(HttpServletResponse response, String message) throws IOException {
        response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
        ApiResponse<String> apiResponse = ApiResponse.error(message);
        response.getWriter().write(JsonUtil.toJson(apiResponse));
    }
}
