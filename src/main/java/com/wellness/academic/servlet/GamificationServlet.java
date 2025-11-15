package com.wellness.academic.servlet;

import com.wellness.core.util.ApiResponse;
import com.wellness.core.util.JsonUtil;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * DEV 4 - Gamification Servlet
 * Handles: /api/game/*
 * 
 * Endpoints:
 * - GET /api/game/challenges - Get challenges
 * - POST /api/game/challenges/{id}/join - Join challenge
 * - GET /api/game/leaderboard - Get leaderboard
 * - POST /api/game/rewards/{id}/redeem - Redeem reward
 */
public class GamificationServlet extends HttpServlet {
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        
        String pathInfo = request.getPathInfo();
        
        if ("/challenges".equals(pathInfo)) {
            handleGetChallenges(response);
        } else if ("/leaderboard".equals(pathInfo)) {
            handleGetLeaderboard(response);
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
        
        if (pathInfo != null && pathInfo.contains("/join")) {
            handleJoinChallenge(request, response);
        } else if (pathInfo != null && pathInfo.contains("/redeem")) {
            handleRedeemReward(request, response);
        } else {
            sendError(response, "Unknown endpoint");
        }
    }
    
    /**
     * Get challenges
     * GET /api/game/challenges
     */
    private void handleGetChallenges(HttpServletResponse response) throws IOException {
        // TODO: DEV 4 - Implement get challenges
        // 1. Get active challenges
        // 2. Return challenge details
        
        ApiResponse<String> apiResponse = ApiResponse.success("List of challenges");
        response.getWriter().write(JsonUtil.toJson(apiResponse));
    }
    
    /**
     * Join challenge
     * POST /api/game/challenges/{id}/join
     */
    private void handleJoinChallenge(HttpServletRequest request, HttpServletResponse response) 
            throws IOException {
        // TODO: DEV 4 - Implement join challenge
        // 1. Create user_challenge record
        // 2. Start tracking progress
        
        ApiResponse<String> apiResponse = ApiResponse.success("Challenge joined");
        response.getWriter().write(JsonUtil.toJson(apiResponse));
    }
    
    /**
     * Get leaderboard
     * GET /api/game/leaderboard
     */
    private void handleGetLeaderboard(HttpServletResponse response) throws IOException {
        // TODO: DEV 4 - Implement leaderboard
        // 1. Get top users by points
        // 2. Return anonymous leaderboard
        
        ApiResponse<String> apiResponse = ApiResponse.success("Leaderboard");
        response.getWriter().write(JsonUtil.toJson(apiResponse));
    }
    
    /**
     * Redeem reward
     * POST /api/game/rewards/{id}/redeem
     */
    private void handleRedeemReward(HttpServletRequest request, HttpServletResponse response) 
            throws IOException {
        // TODO: DEV 4 - Implement redeem reward
        // 1. Check user points
        // 2. Deduct points
        // 3. Create redemption record
        
        ApiResponse<String> apiResponse = ApiResponse.success("Reward redeemed");
        response.getWriter().write(JsonUtil.toJson(apiResponse));
    }
    
    private void sendError(HttpServletResponse response, String message) throws IOException {
        response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
        ApiResponse<String> apiResponse = ApiResponse.error(message);
        response.getWriter().write(JsonUtil.toJson(apiResponse));
    }
}
