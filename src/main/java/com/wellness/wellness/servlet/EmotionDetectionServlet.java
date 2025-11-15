package com.wellness.wellness.servlet;

import com.google.gson.JsonObject;
import com.wellness.core.util.JsonUtil;
import com.wellness.wellness.service.FacialEmotionService;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.IOException;

/**
 * Servlet for facial emotion detection using AWS Rekognition
 */
@WebServlet("/api/wellness/detect-emotion")
public class EmotionDetectionServlet extends HttpServlet {
    
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        
        try {
            // Read request body
            StringBuilder sb = new StringBuilder();
            BufferedReader reader = request.getReader();
            String line;
            while ((line = reader.readLine()) != null) {
                sb.append(line);
            }
            
            // Parse JSON
            JsonObject jsonRequest = JsonUtil.fromJson(sb.toString(), JsonObject.class);
            String base64Image = jsonRequest.get("image").getAsString();
            
            // Detect emotion using AWS Rekognition
            FacialEmotionService.EmotionResult result = 
                    FacialEmotionService.detectEmotion(base64Image);
            
            // Build response
            JsonObject jsonResponse = new JsonObject();
            jsonResponse.addProperty("success", result.isSuccess());
            jsonResponse.addProperty("message", result.getMessage());
            
            if (result.isSuccess()) {
                jsonResponse.addProperty("emotion", result.getEmotion());
                jsonResponse.addProperty("confidence", result.getConfidence());
                jsonResponse.addProperty("faceConfidence", result.getFaceConfidence());
                
                // Add all emotions
                if (result.getAllEmotions() != null) {
                    JsonObject emotionsObj = new JsonObject();
                    result.getAllEmotions().forEach((emotion, confidence) -> 
                        emotionsObj.addProperty(emotion, confidence)
                    );
                    jsonResponse.add("allEmotions", emotionsObj);
                }
            }
            
            response.getWriter().write(jsonResponse.toString());
            
        } catch (Exception e) {
            e.printStackTrace();
            
            JsonObject errorResponse = new JsonObject();
            errorResponse.addProperty("success", false);
            errorResponse.addProperty("message", "Error: " + e.getMessage());
            
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().write(errorResponse.toString());
        }
    }
    
    @Override
    protected void doOptions(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        // Handle CORS preflight
        response.setHeader("Access-Control-Allow-Origin", "*");
        response.setHeader("Access-Control-Allow-Methods", "POST, OPTIONS");
        response.setHeader("Access-Control-Allow-Headers", "Content-Type");
        response.setStatus(HttpServletResponse.SC_OK);
    }
}
