package com.wellness.wellness.servlet;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.wellness.core.config.JPAConfig;
import com.wellness.core.util.ApiResponse;
import com.wellness.core.util.JsonUtil;
import com.wellness.wellness.entity.MoodCheckinEntity;
import com.wellness.wellness.entity.WellnessScoreEntity;
import com.wellness.wellness.service.HeartRateService;
import com.wellness.wellness.service.StressAnalyzer;
import com.wellness.wellness.service.WellnessScoreCalculator;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * DEV 1 - Wellness Servlet
 * Handles: /api/wellness/*
 * 
 * Endpoints:
 * - POST /api/wellness/checkin - Create mood check-in with heart rate & stress analysis
 * - GET /api/wellness/dashboard/{userId} - Get comprehensive dashboard data
 * - GET /api/wellness/mood-trend/{userId} - Get mood trend over time
 * - GET /api/wellness/score/{userId} - Get latest wellness score
 */
public class WellnessServlet extends HttpServlet {
    
    private HeartRateService heartRateService;
    private StressAnalyzer stressAnalyzer;
    private WellnessScoreCalculator scoreCalculator;
    private Gson gson;
    
    @Override
    public void init() {
        heartRateService = new HeartRateService();
        stressAnalyzer = new StressAnalyzer();
        scoreCalculator = new WellnessScoreCalculator();
        gson = new Gson();
    }
    
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
        } else if (pathInfo != null && pathInfo.startsWith("/mood-trend/")) {
            String userId = pathInfo.substring("/mood-trend/".length());
            handleGetMoodTrend(userId, response);
        } else if (pathInfo != null && pathInfo.startsWith("/score/")) {
            String userId = pathInfo.substring("/score/".length());
            handleGetWellnessScore(userId, response);
        } else {
            sendError(response, "Unknown endpoint");
        }
    }
    
    /**
     * Handle mood check-in with heart rate & stress analysis
     * POST /api/wellness/checkin
     * Body: {userId, heartRate, hrv, stressScore, emotion, emoji, note}
     */
    private void handleCheckin(HttpServletRequest request, HttpServletResponse response) 
            throws IOException {
        
        EntityManager em = null;
        try {
            // Parse request body
            String body = request.getReader().lines().reduce("", (acc, line) -> acc + line);
            JsonObject json = gson.fromJson(body, JsonObject.class);
            
            Long userId = json.get("userId").getAsLong();
            int heartRate = json.has("heartRate") ? json.get("heartRate").getAsInt() : 0;
            int hrv = json.has("hrv") ? json.get("hrv").getAsInt() : 0;
            int stressScore = json.has("stressScore") ? json.get("stressScore").getAsInt() : 0;
            String emotion = json.has("emotion") ? json.get("emotion").getAsString() : "neutral";
            String emoji = json.has("emoji") ? json.get("emoji").getAsString() : "😐";
            String note = json.has("note") ? json.get("note").getAsString() : "";
            
            LocalDate today = LocalDate.now();
            
            // Get consecutive check-in days
            int consecutiveDays = getConsecutiveCheckinDays(userId);
            
            // Calculate wellness score
            int wellnessScore = scoreCalculator.calculateWellnessScore(
                heartRate, hrv, stressScore, consecutiveDays
            );
            
            em = JPAConfig.getEntityManager();
            em.getTransaction().begin();
            
            // Save mood check-in
            MoodCheckinEntity checkin = new MoodCheckinEntity();
            checkin.setUserId(userId);
            checkin.setCheckinDate(today);
            checkin.setEmotion(emotion);
            checkin.setEmoji(emoji);
            checkin.setNote(note);
            checkin.setHeartRate(heartRate);
            checkin.setWellnessScore(wellnessScore);
            
            // Store HRV and stress in face_analysis_data as JSON
            Map<String, Object> analysisData = new HashMap<>();
            analysisData.put("hrv", hrv);
            analysisData.put("stressScore", stressScore);
            analysisData.put("timestamp", System.currentTimeMillis());
            checkin.setFaceAnalysisData(gson.toJson(analysisData));
            
            em.persist(checkin);
            
            // Save/Update wellness score
            WellnessScoreEntity scoreEntity = getOrCreateWellnessScore(em, userId, today);
            scoreEntity.setScore(wellnessScore);
            scoreEntity.setHeartRate(heartRate);
            scoreEntity.setHrv(hrv);
            scoreEntity.setStressScore(stressScore);
            scoreEntity.setFactors(scoreCalculator.generateFactorsJson(heartRate, hrv, stressScore, 70));
            
            if (scoreEntity.getId() == null) {
                em.persist(scoreEntity);
            } else {
                em.merge(scoreEntity);
            }
            
            em.getTransaction().commit();
            
            // Generate wellness report
            WellnessScoreCalculator.WellnessReport report = scoreCalculator.generateReport(
                heartRate, hrv, stressScore, consecutiveDays, today
            );
            
            // Prepare response
            Map<String, Object> responseData = new HashMap<>();
            responseData.put("checkin", checkin);
            responseData.put("wellnessScore", wellnessScore);
            responseData.put("report", report);
            responseData.put("consecutiveDays", consecutiveDays);
            
            ApiResponse<Map<String, Object>> apiResponse = ApiResponse.success(
                responseData, 
                "Check-in saved successfully! Wellness Score: " + wellnessScore
            );
            response.getWriter().write(JsonUtil.toJson(apiResponse));
            
        } catch (Exception e) {
            if (em != null && em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            e.printStackTrace();
            sendError(response, "Failed to save check-in: " + e.getMessage());
        } finally {
            if (em != null) em.close();
        }
    }
    
    /**
     * Get comprehensive dashboard data
     * GET /api/wellness/dashboard/{userId}
     */
    private void handleGetDashboard(String userIdStr, HttpServletResponse response) 
            throws IOException {
        
        EntityManager em = null;
        try {
            Long userId = Long.parseLong(userIdStr);
            em = JPAConfig.getEntityManager();
            
            // Get latest check-in
            TypedQuery<MoodCheckinEntity> checkinQuery = em.createQuery(
                "SELECT m FROM MoodCheckinEntity m WHERE m.userId = :userId ORDER BY m.checkinDate DESC",
                MoodCheckinEntity.class
            );
            checkinQuery.setParameter("userId", userId);
            checkinQuery.setMaxResults(1);
            List<MoodCheckinEntity> latestCheckins = checkinQuery.getResultList();
            
            // Get latest wellness score
            TypedQuery<WellnessScoreEntity> scoreQuery = em.createQuery(
                "SELECT w FROM WellnessScoreEntity w WHERE w.userId = :userId ORDER BY w.date DESC",
                WellnessScoreEntity.class
            );
            scoreQuery.setParameter("userId", userId);
            scoreQuery.setMaxResults(1);
            List<WellnessScoreEntity> latestScores = scoreQuery.getResultList();
            
            // Get last 7 days check-ins for trend
            TypedQuery<MoodCheckinEntity> trendQuery = em.createQuery(
                "SELECT m FROM MoodCheckinEntity m WHERE m.userId = :userId " +
                "AND m.checkinDate >= :startDate ORDER BY m.checkinDate DESC",
                MoodCheckinEntity.class
            );
            trendQuery.setParameter("userId", userId);
            trendQuery.setParameter("startDate", LocalDate.now().minusDays(7));
            List<MoodCheckinEntity> weeklyCheckins = trendQuery.getResultList();
            
            // Calculate statistics
            int consecutiveDays = getConsecutiveCheckinDays(userId);
            double avgWellnessScore = weeklyCheckins.stream()
                .mapToInt(MoodCheckinEntity::getWellnessScore)
                .average()
                .orElse(0.0);
            
            // Prepare dashboard response
            Map<String, Object> dashboard = new HashMap<>();
            dashboard.put("latestCheckin", latestCheckins.isEmpty() ? null : latestCheckins.get(0));
            dashboard.put("latestWellnessScore", latestScores.isEmpty() ? null : latestScores.get(0));
            dashboard.put("weeklyCheckins", weeklyCheckins);
            dashboard.put("consecutiveDays", consecutiveDays);
            dashboard.put("avgWellnessScore", Math.round(avgWellnessScore));
            dashboard.put("totalCheckins", weeklyCheckins.size());
            
            // Add recommendations if we have latest data
            if (!latestScores.isEmpty()) {
                WellnessScoreEntity latest = latestScores.get(0);
                WellnessScoreCalculator.WellnessReport report = scoreCalculator.generateReport(
                    latest.getHeartRate(),
                    latest.getHrv(),
                    latest.getStressScore(),
                    consecutiveDays,
                    latest.getDate()
                );
                dashboard.put("recommendations", report);
            }
            
            ApiResponse<Map<String, Object>> apiResponse = ApiResponse.success(dashboard);
            response.getWriter().write(JsonUtil.toJson(apiResponse));
            
        } catch (Exception e) {
            e.printStackTrace();
            sendError(response, "Failed to load dashboard: " + e.getMessage());
        } finally {
            if (em != null) em.close();
        }
    }
    
    /**
     * Get mood trend over time
     * GET /api/wellness/mood-trend/{userId}?days=30
     */
    private void handleGetMoodTrend(String userIdStr, HttpServletResponse response) 
            throws IOException {
        
        EntityManager em = null;
        try {
            Long userId = Long.parseLong(userIdStr);
            em = JPAConfig.getEntityManager();
            
            // Get last 30 days by default
            int days = 30;
            LocalDate startDate = LocalDate.now().minusDays(days);
            
            TypedQuery<MoodCheckinEntity> query = em.createQuery(
                "SELECT m FROM MoodCheckinEntity m WHERE m.userId = :userId " +
                "AND m.checkinDate >= :startDate ORDER BY m.checkinDate ASC",
                MoodCheckinEntity.class
            );
            query.setParameter("userId", userId);
            query.setParameter("startDate", startDate);
            
            List<MoodCheckinEntity> checkins = query.getResultList();
            
            ApiResponse<List<MoodCheckinEntity>> apiResponse = ApiResponse.success(checkins);
            response.getWriter().write(JsonUtil.toJson(apiResponse));
            
        } catch (Exception e) {
            e.printStackTrace();
            sendError(response, "Failed to load mood trend: " + e.getMessage());
        } finally {
            if (em != null) em.close();
        }
    }
    
    /**
     * Get latest wellness score
     * GET /api/wellness/score/{userId}
     */
    private void handleGetWellnessScore(String userIdStr, HttpServletResponse response) 
            throws IOException {
        
        EntityManager em = null;
        try {
            Long userId = Long.parseLong(userIdStr);
            em = JPAConfig.getEntityManager();
            
            TypedQuery<WellnessScoreEntity> query = em.createQuery(
                "SELECT w FROM WellnessScoreEntity w WHERE w.userId = :userId ORDER BY w.date DESC",
                WellnessScoreEntity.class
            );
            query.setParameter("userId", userId);
            query.setMaxResults(1);
            
            List<WellnessScoreEntity> scores = query.getResultList();
            
            if (scores.isEmpty()) {
                sendError(response, "No wellness score found for user");
                return;
            }
            
            ApiResponse<WellnessScoreEntity> apiResponse = ApiResponse.success(scores.get(0));
            response.getWriter().write(JsonUtil.toJson(apiResponse));
            
        } catch (Exception e) {
            e.printStackTrace();
            sendError(response, "Failed to get wellness score: " + e.getMessage());
        } finally {
            if (em != null) em.close();
        }
    }
    
    /**
     * Get or create wellness score entity for today
     */
    private WellnessScoreEntity getOrCreateWellnessScore(EntityManager em, Long userId, LocalDate date) {
        TypedQuery<WellnessScoreEntity> query = em.createQuery(
            "SELECT w FROM WellnessScoreEntity w WHERE w.userId = :userId AND w.date = :date",
            WellnessScoreEntity.class
        );
        query.setParameter("userId", userId);
        query.setParameter("date", date);
        
        List<WellnessScoreEntity> results = query.getResultList();
        
        if (!results.isEmpty()) {
            return results.get(0);
        }
        
        WellnessScoreEntity newScore = new WellnessScoreEntity();
        newScore.setUserId(userId);
        newScore.setDate(date);
        return newScore;
    }
    
    /**
     * Calculate consecutive check-in days
     */
    private int getConsecutiveCheckinDays(Long userId) {
        EntityManager em = null;
        try {
            em = JPAConfig.getEntityManager();
            
            TypedQuery<MoodCheckinEntity> query = em.createQuery(
                "SELECT m FROM MoodCheckinEntity m WHERE m.userId = :userId " +
                "ORDER BY m.checkinDate DESC",
                MoodCheckinEntity.class
            );
            query.setParameter("userId", userId);
            query.setMaxResults(100);
            
            List<MoodCheckinEntity> checkins = query.getResultList();
            
            if (checkins.isEmpty()) return 0;
            
            int consecutive = 1;
            LocalDate expectedDate = LocalDate.now().minusDays(1);
            
            for (int i = 1; i < checkins.size(); i++) {
                if (checkins.get(i).getCheckinDate().equals(expectedDate)) {
                    consecutive++;
                    expectedDate = expectedDate.minusDays(1);
                } else {
                    break;
                }
            }
            
            return consecutive;
            
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        } finally {
            if (em != null) em.close();
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
