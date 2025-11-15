package com.wellness.community.servlet;

import com.wellness.core.util.ApiResponse;
import com.wellness.core.util.JsonUtil;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * DEV 3 - Community Servlet
 * Handles: /api/community/*
 * 
 * Endpoints:
 * - GET /api/community/posts - Get forum posts
 * - POST /api/community/posts - Create forum post
 * - POST /api/community/buddy/request - Find buddy
 * - GET /api/community/events - Get events
 */
public class CommunityServlet extends HttpServlet {
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        
        String pathInfo = request.getPathInfo();
        
        if ("/posts".equals(pathInfo)) {
            handleGetPosts(response);
        } else if ("/events".equals(pathInfo)) {
            handleGetEvents(response);
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
        
        if ("/posts".equals(pathInfo)) {
            handleCreatePost(request, response);
        } else if (pathInfo != null && pathInfo.startsWith("/buddy/")) {
            handleBuddyRequest(request, response);
        } else {
            sendError(response, "Unknown endpoint");
        }
    }
    
    /**
     * Get forum posts
     * GET /api/community/posts
     */
    private void handleGetPosts(HttpServletResponse response) throws IOException {
        // TODO: DEV 3 - Implement get posts
        // 1. Get posts from database
        // 2. Support filtering by tags
        // 3. Support pagination
        
        ApiResponse<String> apiResponse = ApiResponse.success("List of posts");
        response.getWriter().write(JsonUtil.toJson(apiResponse));
    }
    
    /**
     * Create forum post
     * POST /api/community/posts
     */
    private void handleCreatePost(HttpServletRequest request, HttpServletResponse response) 
            throws IOException {
        // TODO: DEV 3 - Implement create post
        // 1. Parse request body
        // 2. Save to database
        // 3. Return created post
        
        ApiResponse<String> apiResponse = ApiResponse.success("Post created");
        response.getWriter().write(JsonUtil.toJson(apiResponse));
    }
    
    /**
     * Find buddy
     * POST /api/community/buddy/request
     */
    private void handleBuddyRequest(HttpServletRequest request, HttpServletResponse response) 
            throws IOException {
        // TODO: DEV 3 - Implement buddy matching
        // 1. Find available buddy
        // 2. Create buddy pair
        // 3. Return buddy info
        
        ApiResponse<String> apiResponse = ApiResponse.success("Buddy found");
        response.getWriter().write(JsonUtil.toJson(apiResponse));
    }
    
    /**
     * Get events
     * GET /api/community/events
     */
    private void handleGetEvents(HttpServletResponse response) throws IOException {
        // TODO: DEV 3 - Implement get events
        
        ApiResponse<String> apiResponse = ApiResponse.success("List of events");
        response.getWriter().write(JsonUtil.toJson(apiResponse));
    }
    
    private void sendError(HttpServletResponse response, String message) throws IOException {
        response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
        ApiResponse<String> apiResponse = ApiResponse.error(message);
        response.getWriter().write(JsonUtil.toJson(apiResponse));
    }
}
