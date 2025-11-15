package com.wellness.ai.servlet;

import com.wellness.core.util.ApiResponse;
import com.wellness.core.util.JsonUtil;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * DEV 2 - AI Companion Servlet
 * Handles: /api/ai/*
 * 
 * Endpoints:
 * - POST /api/ai/chat - Chat with AI
 * - POST /api/ai/crisis-check - Check for crisis keywords
 */
public class AiCompanionServlet extends HttpServlet {
    
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        
        String pathInfo = request.getPathInfo();
        
        if ("/chat".equals(pathInfo)) {
            handleChat(request, response);
        } else if ("/crisis-check".equals(pathInfo)) {
            handleCrisisCheck(request, response);
        } else {
            sendError(response, "Unknown endpoint");
        }
    }
    
    /**
     * Handle AI chat
     * POST /api/ai/chat
     * Body: { "userId": 1, "message": "Tôi cảm thấy lo lắng" }
     */
    private void handleChat(HttpServletRequest request, HttpServletResponse response) 
            throws IOException {
        
        try {
            // TODO: DEV 2 - Implement AI chat logic
            // 1. Parse user message
            // 2. Call AI API (OpenAI/Gemini)
            // 3. Check for crisis keywords
            // 4. Save conversation to database
            // 5. Return AI response
            
            String mockResponse = "Tôi hiểu bạn đang cảm thấy lo lắng. Hãy thử hít thở sâu nhé.";
            ApiResponse<String> apiResponse = ApiResponse.success(mockResponse);
            response.getWriter().write(JsonUtil.toJson(apiResponse));
            
        } catch (Exception e) {
            sendError(response, "AI chat error: " + e.getMessage());
        }
    }
    
    /**
     * Check for crisis keywords
     */
    private void handleCrisisCheck(HttpServletRequest request, HttpServletResponse response) 
            throws IOException {
        
        // TODO: DEV 2 - Implement crisis detection
        // Keywords: "tự tử", "chết", "không muốn sống", etc.
        
        ApiResponse<Boolean> apiResponse = ApiResponse.success(false, "No crisis detected");
        response.getWriter().write(JsonUtil.toJson(apiResponse));
    }
    
    private void sendError(HttpServletResponse response, String message) throws IOException {
        response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
        ApiResponse<String> apiResponse = ApiResponse.error(message);
        response.getWriter().write(JsonUtil.toJson(apiResponse));
    }
}
