# 🎯 DEV 2 - AI & CONTENT MODULE - TASK LIST

## 📦 Deliverables

### Week 1: Database + AI Setup
- [x] Create database schema (ai_conversations, wellness_content)
- [ ] Setup AI API (OpenAI/Gemini)
- [ ] Create ConversationEntity.java
- [ ] Create WellnessContentEntity.java
- [ ] AiCompanionServlet.java skeleton
- [ ] WellnessLibraryServlet.java skeleton

### Week 2: AI Companion
- [ ] ChatService.java
  - [ ] Integrate with OpenAI/Gemini API
  - [ ] Support Vietnamese language
  - [ ] Maintain conversation context
  - [ ] Handle empathetic responses
- [ ] CrisisDetector.java
  - [ ] Define crisis keywords (tự tử, chết, không muốn sống)
  - [ ] Implement keyword detection
  - [ ] Trigger hotline connection
  - [ ] Save to crisis_detections table
- [ ] POST /api/ai/chat implementation
- [ ] POST /api/ai/crisis-check implementation

### Week 3: Content Library & Recommendations
- [ ] Content Management
  - [ ] Seed database with sample content
  - [ ] Meditation videos (5-30 min)
  - [ ] Breathing exercises
  - [ ] Sleep podcasts
- [ ] RecommendationEngine.java
  - [ ] Get user's wellness score from DEV 1
  - [ ] Recommend content based on mood
  - [ ] Low score → relaxation content
  - [ ] High anxiety → breathing exercises
- [ ] GET /api/library/content (with filters)
- [ ] GET /api/library/recommendations/:userId
- [ ] POST /api/library/progress (track completion)

### Week 4: Testing & Integration
- [ ] Test AI responses in Vietnamese
- [ ] Test crisis detection accuracy
- [ ] Test recommendation algorithm
- [ ] Integration with DEV 1 (wellness score)
- [ ] Integration with DEV 4 (content completion → points)

## 🔗 Integration Points

### With DEV 1 (Wellness):
- **Receive**: Wellness Score → Use for recommendations
- **API**: Call `GET /api/wellness/score/:userId`

### With DEV 4 (Gamification):
- **Provide**: Content completion events → Award points
- **API**: Trigger when user completes meditation/podcast

## 🤖 AI Configuration

### OpenAI Setup:
```java
// Example configuration
String apiKey = System.getenv("OPENAI_API_KEY");
String model = "gpt-4"; // or gpt-3.5-turbo
String systemPrompt = "Bạn là trợ lý tâm lý thấu cảm, luôn lắng nghe và hỗ trợ sinh viên bằng tiếng Việt.";
```

### Crisis Keywords (Vietnamese):
```java
String[] crisisKeywords = {
    "tự tử", "tự sát", "chết", "không muốn sống",
    "kết thúc cuộc đời", "tự hại", "tự làm hại bản thân"
};
```

## 📝 Sample Content (Wellness Library)

```sql
-- Insert sample content
INSERT INTO wellness_content (type, title, url, duration, tags) VALUES
('meditation', 'Thiền 5 phút cho sinh viên', 'https://youtube.com/...', 300, '["stress", "quick"]'),
('breathing', 'Hít thở 4-7-8', 'https://youtube.com/...', 120, '["anxiety", "breathing"]'),
('podcast', 'Âm thanh ru ngủ', 'https://spotify.com/...', 1800, '["sleep", "relaxation"]');
```

## 🧪 Testing Checklist

- [ ] Test AI chat with normal messages
- [ ] Test crisis detection (trigger keywords)
- [ ] Test hotline connection flow
- [ ] Test recommendation accuracy
- [ ] Test content filtering by type/tags
- [ ] Test progress tracking (completion)

## 📞 Need Help?

- AI API costs → Discuss budget with team
- Crisis response protocol → Consult mental health professional
- Content sourcing → Find free meditation resources
