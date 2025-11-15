package com.wellness.wellness.service;

/**
 * DEV 1 - Heart Rate Analysis Service
 * Analyzes heart rate data and provides health insights
 */
public class HeartRateService {
    
    /**
     * Validate heart rate is within normal range
     * Normal resting heart rate: 60-100 BPM
     * Athletes: 40-60 BPM
     * Tachycardia: >100 BPM
     */
    public boolean isHeartRateNormal(int heartRate) {
        return heartRate >= 40 && heartRate <= 100;
    }
    
    /**
     * Get heart rate category
     */
    public String getHeartRateCategory(int heartRate) {
        if (heartRate < 60) return "BRADYCARDIA"; // Slow
        if (heartRate <= 100) return "NORMAL";
        return "TACHYCARDIA"; // Fast
    }
    
    /**
     * Calculate heart rate score (0-100)
     * Optimal range: 60-80 BPM
     */
    public int calculateHeartRateScore(int heartRate) {
        if (heartRate >= 60 && heartRate <= 80) {
            return 100; // Optimal
        } else if (heartRate >= 50 && heartRate <= 90) {
            return 85; // Good
        } else if (heartRate >= 40 && heartRate <= 100) {
            return 70; // Acceptable
        } else if (heartRate >= 35 && heartRate <= 110) {
            return 50; // Below average
        } else {
            return 30; // Concerning
        }
    }
    
    /**
     * Validate HRV (Heart Rate Variability)
     * Higher HRV = Better cardiovascular health and stress resilience
     * 
     * HRV Ranges (SDNN in ms):
     * - Excellent: >100 ms
     * - Good: 50-100 ms
     * - Fair: 20-50 ms
     * - Poor: <20 ms
     */
    public boolean isHRVHealthy(int hrv) {
        return hrv >= 50;
    }
    
    /**
     * Get HRV category
     */
    public String getHRVCategory(int hrv) {
        if (hrv >= 100) return "EXCELLENT";
        if (hrv >= 50) return "GOOD";
        if (hrv >= 20) return "FAIR";
        return "POOR";
    }
    
    /**
     * Calculate HRV score (0-100)
     */
    public int calculateHRVScore(int hrv) {
        if (hrv >= 100) return 100;
        if (hrv >= 80) return 90;
        if (hrv >= 60) return 80;
        if (hrv >= 40) return 70;
        if (hrv >= 20) return 50;
        return 30;
    }
    
    /**
     * Get health recommendations based on heart rate
     */
    public String getHeartRateRecommendation(int heartRate) {
        if (heartRate < 60) {
            return "Nhịp tim hơi chậm. Nếu không phải vận động viên, hãy tham khảo ý kiến bác sĩ.";
        } else if (heartRate > 100) {
            return "Nhịp tim cao. Hãy thư giãn, thở sâu và tránh caffeine. Nếu kéo dài, hãy gặp bác sĩ.";
        } else if (heartRate >= 60 && heartRate <= 80) {
            return "Nhịp tim tuyệt vời! Hãy duy trì lối sống lành mạnh.";
        } else {
            return "Nhịp tim bình thường. Tiếp tục duy trì hoạt động thể chất đều đặn.";
        }
    }
    
    /**
     * Get HRV recommendations
     */
    public String getHRVRecommendation(int hrv) {
        if (hrv >= 100) {
            return "HRV tuyệt vời! Cơ thể bạn rất khỏe mạnh và khả năng chống stress cao.";
        } else if (hrv >= 50) {
            return "HRV tốt. Hãy duy trì tập luyện đều đặn và ngủ đủ giấc.";
        } else if (hrv >= 20) {
            return "HRV ở mức trung bình. Hãy tập yoga, thiền và giảm stress.";
        } else {
            return "HRV thấp. Cơ thể cần nghỉ ngơi nhiều hơn. Hãy giảm stress và ngủ đủ 7-8 tiếng/đêm.";
        }
    }
}
