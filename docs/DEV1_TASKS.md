# 🎯 DEV 1 - WELLNESS MODULE - TASK LIST

## 📦 Deliverables

### Week 1: Database + Basic APIs
- [x] Create database schema (mood_checkins, wellness_scores, mood_alerts)
- [ ] Implement MoodCheckinEntity.java
- [ ] Create WellnessServlet.java skeleton
- [ ] POST /api/wellness/checkin - Save mood check-in
- [ ] GET /api/wellness/dashboard/:userId - Basic response

### Week 2: Business Logic
- [ ] WellnessScoreCalculator.java
  - [ ] Calculate wellness score from mood data
  - [ ] Consider factors: mood, frequency, consistency
- [ ] MoodTrendAnalyzer.java
  - [ ] Analyze mood patterns over 7/30 days
  - [ ] Detect downward trends
- [ ] Mood Alert System
  - [ ] Trigger alerts for 3+ consecutive low scores
  - [ ] Create mood_alerts records

### Week 3: Advanced Features
- [ ] Face Analysis Integration
  - [ ] Research ML API (Azure Face API / Google Vision)
  - [ ] Implement FaceAnalysisService.java
  - [ ] Store face_analysis_data in JSON format
- [ ] Heart Rate Processing
  - [ ] Parse heart rate from camera/device
  - [ ] Store in mood_checkins table
- [ ] Dashboard Enhancements
  - [ ] Weekly/monthly mood charts
  - [ ] Wellness score trends
  - [ ] Alert history

### Week 4: Testing & Integration
- [ ] Unit tests for WellnessScoreCalculator
- [ ] Integration test với DEV 2 (AI recommendations based on wellness score)
- [ ] Integration test với DEV 4 (Points awarded for daily check-ins)
- [ ] Performance optimization (database queries)

## 🔗 Integration Points

### With DEV 2 (AI):
- **Provide**: Wellness Score → AI uses this for personalized recommendations
- **API**: Share `GET /api/wellness/score/:userId` endpoint

### With DEV 4 (Gamification):
- **Provide**: Daily check-in status → Award points
- **API**: Trigger webhook when check-in completed

## 📝 Code Template

```java
// Example: Calculate wellness score
public class WellnessScoreCalculator {
    
    public int calculateScore(List<MoodCheckinEntity> recentCheckins) {
        // TODO: Implement scoring algorithm
        // Factors:
        // 1. Mood emotion (happy=100, sad=20)
        // 2. Consistency (daily check-ins bonus)
        // 3. Heart rate (optimal range)
        // 4. Face analysis sentiment
        
        return 75; // placeholder
    }
}
```

## 🧪 Testing Checklist

- [ ] Test check-in with all emotion types
- [ ] Test duplicate check-in (same day)
- [ ] Test invalid user_id
- [ ] Test face analysis with various images
- [ ] Test trend detection accuracy

## 📞 Need Help?

- Face API integration → Ask DEV 2 (AI expert)
- Database optimization → Review with team
- Points integration → Coordinate with DEV 4
