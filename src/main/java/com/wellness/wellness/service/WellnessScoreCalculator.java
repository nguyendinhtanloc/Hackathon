package com.wellness.wellness.service;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

/**
 * DEV 1 - Wellness Score Calculator
 * Calculates overall wellness score from multiple health factors
 */
public class WellnessScoreCalculator {
    
    private HeartRateService heartRateService;
    private StressAnalyzer stressAnalyzer;
    private Gson gson;
    
    public WellnessScoreCalculator() {
        this.heartRateService = new HeartRateService();
        this.stressAnalyzer = new StressAnalyzer();
        this.gson = new Gson();
    }
    
    /**
     * Calculate overall wellness score (0-100)
     * 
     * Factors (weighted):
     * - Heart Rate Health: 25%
     * - HRV: 30%
     * - Stress Level: 35%
     * - Consistency Bonus: 10% (daily check-ins)
     */
    public int calculateWellnessScore(int heartRate, int hrv, int stressScore, int consecutiveDays) {
        // Heart rate component (0-25 points)
        int hrScore = (int) (heartRateService.calculateHeartRateScore(heartRate) * 0.25);
        
        // HRV component (0-30 points)
        int hrvScore = (int) (heartRateService.calculateHRVScore(hrv) * 0.30);
        
        // Stress component (0-35 points) - inverse of stress score
        int stressComponent = (int) ((100 - stressScore) * 0.35);
        
        // Consistency bonus (0-10 points)
        int consistencyBonus = calculateConsistencyBonus(consecutiveDays);
        
        // Total wellness score
        int totalScore = hrScore + hrvScore + stressComponent + consistencyBonus;
        
        // Ensure within 0-100 range
        return Math.max(0, Math.min(100, totalScore));
    }
    
    /**
     * Calculate consistency bonus based on consecutive check-in days
     */
    private int calculateConsistencyBonus(int consecutiveDays) {
        if (consecutiveDays >= 30) return 10;
        if (consecutiveDays >= 14) return 8;
        if (consecutiveDays >= 7) return 6;
        if (consecutiveDays >= 3) return 4;
        if (consecutiveDays >= 1) return 2;
        return 0;
    }
    
    /**
     * Get wellness level category
     */
    public String getWellnessLevel(int wellnessScore) {
        if (wellnessScore >= 80) return "EXCELLENT";
        if (wellnessScore >= 60) return "GOOD";
        if (wellnessScore >= 40) return "FAIR";
        return "NEEDS_ATTENTION";
    }
    
    /**
     * Get wellness level in Vietnamese
     */
    public String getWellnessLevelVietnamese(int wellnessScore) {
        if (wellnessScore >= 80) return "Xuất sắc";
        if (wellnessScore >= 60) return "Tốt";
        if (wellnessScore >= 40) return "Trung bình";
        return "Cần chú ý";
    }
    
    /**
     * Generate wellness factors JSON
     */
    public String generateFactorsJson(int heartRate, int hrv, int stressScore, int moodScore) {
        Map<String, Integer> factors = new HashMap<>();
        factors.put("heartRate", heartRateService.calculateHeartRateScore(heartRate));
        factors.put("hrv", heartRateService.calculateHRVScore(hrv));
        factors.put("stress", 100 - stressScore); // Inverse for consistency
        factors.put("mood", moodScore);
        
        return gson.toJson(factors);
    }
    
    /**
     * Get comprehensive wellness report
     */
    public WellnessReport generateReport(int heartRate, int hrv, int stressScore, 
                                         int consecutiveDays, LocalDate date) {
        int wellnessScore = calculateWellnessScore(heartRate, hrv, stressScore, consecutiveDays);
        
        WellnessReport report = new WellnessReport();
        report.date = date;
        report.wellnessScore = wellnessScore;
        report.wellnessLevel = getWellnessLevelVietnamese(wellnessScore);
        
        // Heart rate analysis
        report.heartRate = heartRate;
        report.heartRateCategory = heartRateService.getHeartRateCategory(heartRate);
        report.heartRateRecommendation = heartRateService.getHeartRateRecommendation(heartRate);
        
        // HRV analysis
        report.hrv = hrv;
        report.hrvCategory = heartRateService.getHRVCategory(hrv);
        report.hrvRecommendation = heartRateService.getHRVRecommendation(hrv);
        
        // Stress analysis
        report.stressScore = stressScore;
        report.stressLevel = stressAnalyzer.getStressLevelVietnamese(stressScore);
        report.stressRecommendation = stressAnalyzer.getStressRecommendation(stressScore);
        report.recommendedActivities = stressAnalyzer.getRecommendedActivities(stressScore);
        
        // Critical alert
        report.isCritical = stressAnalyzer.isCriticalStress(stressScore, heartRate);
        
        return report;
    }
    
    /**
     * Wellness Report DTO
     */
    public static class WellnessReport {
        public LocalDate date;
        public int wellnessScore;
        public String wellnessLevel;
        
        public int heartRate;
        public String heartRateCategory;
        public String heartRateRecommendation;
        
        public int hrv;
        public String hrvCategory;
        public String hrvRecommendation;
        
        public int stressScore;
        public String stressLevel;
        public String stressRecommendation;
        public String[] recommendedActivities;
        
        public boolean isCritical;
    }
}
