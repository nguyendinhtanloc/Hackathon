package com.wellness.wellness.service;

/**
 * DEV 1 - Stress Analyzer Service
 * Calculates stress score from heart rate and HRV data
 */
public class StressAnalyzer {
    
    private HeartRateService heartRateService;
    
    public StressAnalyzer() {
        this.heartRateService = new HeartRateService();
    }
    
    /**
     * Calculate stress score (0-100)
     * Lower score = less stress
     * 
     * Factors:
     * 1. Heart Rate (40% weight) - Higher resting HR = more stress
     * 2. HRV (60% weight) - Lower HRV = more stress
     */
    public int calculateStressScore(int heartRate, int hrv) {
        // Heart rate component (0-40 points)
        int hrStressPoints = calculateHeartRateStress(heartRate);
        
        // HRV component (0-60 points)
        int hrvStressPoints = calculateHRVStress(hrv);
        
        // Total stress score
        int totalStress = hrStressPoints + hrvStressPoints;
        
        // Ensure within 0-100 range
        return Math.max(0, Math.min(100, totalStress));
    }
    
    /**
     * Calculate stress points from heart rate (0-40)
     * Higher resting heart rate = more stress
     */
    private int calculateHeartRateStress(int heartRate) {
        if (heartRate < 60) {
            return 5; // Very low stress (athletic heart)
        } else if (heartRate <= 70) {
            return 10; // Low stress
        } else if (heartRate <= 80) {
            return 20; // Moderate stress
        } else if (heartRate <= 90) {
            return 30; // Elevated stress
        } else if (heartRate <= 100) {
            return 35; // High stress
        } else {
            return 40; // Very high stress
        }
    }
    
    /**
     * Calculate stress points from HRV (0-60)
     * Lower HRV = more stress
     */
    private int calculateHRVStress(int hrv) {
        if (hrv >= 100) {
            return 5; // Excellent - very low stress
        } else if (hrv >= 80) {
            return 15; // Good - low stress
        } else if (hrv >= 60) {
            return 25; // Fair - moderate stress
        } else if (hrv >= 40) {
            return 35; // Below average - elevated stress
        } else if (hrv >= 20) {
            return 50; // Poor - high stress
        } else {
            return 60; // Very poor - very high stress
        }
    }
    
    /**
     * Get stress level category
     */
    public String getStressLevel(int stressScore) {
        if (stressScore < 30) return "LOW";
        if (stressScore < 60) return "MODERATE";
        return "HIGH";
    }
    
    /**
     * Get stress level in Vietnamese
     */
    public String getStressLevelVietnamese(int stressScore) {
        if (stressScore < 30) return "Thấp";
        if (stressScore < 60) return "Trung bình";
        return "Cao";
    }
    
    /**
     * Get stress emoji
     */
    public String getStressEmoji(int stressScore) {
        if (stressScore < 30) return "😊";
        if (stressScore < 60) return "😐";
        return "😰";
    }
    
    /**
     * Get stress recommendations
     */
    public String getStressRecommendation(int stressScore) {
        if (stressScore < 30) {
            return "Mức stress thấp! Cơ thể bạn đang ở trạng thái tốt. Hãy duy trì lối sống lành mạnh.";
        } else if (stressScore < 60) {
            return "Mức stress trung bình. Hãy dành thời gian thư giãn: thiền, yoga, hoặc đi dạo. "
                 + "Đảm bảo ngủ đủ 7-8 tiếng mỗi đêm.";
        } else {
            return "Mức stress cao! Cơ thể cần nghỉ ngơi ngay. Hãy: "
                 + "1) Thở sâu 5 phút, 2) Nghe nhạc thư giãn, 3) Nói chuyện với bạn bè, "
                 + "4) Nếu stress kéo dài, hãy tham khảo chuyên gia tâm lý.";
        }
    }
    
    /**
     * Get recommended activities based on stress level
     */
    public String[] getRecommendedActivities(int stressScore) {
        if (stressScore < 30) {
            return new String[] {
                "Tiếp tục tập thể dục nhẹ nhàng",
                "Duy trì thói quen ngủ đủ giấc",
                "Thực hành biết ơn hàng ngày"
            };
        } else if (stressScore < 60) {
            return new String[] {
                "Thiền 10-15 phút mỗi ngày",
                "Tập yoga hoặc stretching",
                "Giảm caffeine và đường",
                "Nghe nhạc thư giãn",
                "Đi dạo ngoài trời 20 phút"
            };
        } else {
            return new String[] {
                "Nghỉ ngơi ngay lập tức",
                "Thở sâu 4-7-8 (hít vào 4s, giữ 7s, thở ra 8s)",
                "Tắt điện thoại, tránh màn hình",
                "Nói chuyện với người thân",
                "Cân nhắc gặp chuyên gia tâm lý",
                "Ngủ đủ 8-9 tiếng"
            };
        }
    }
    
    /**
     * Check if stress is critical and needs immediate attention
     */
    public boolean isCriticalStress(int stressScore, int heartRate) {
        return stressScore >= 80 || heartRate > 110;
    }
}
