# 🏗️ Smart Mental Wellness - Phân Chia Module (4 Developers)

## 📋 Tổng Quan Kiến Trúc

```
smart-mental-wellness/
├── backend/
│   ├── src/main/java/com/wellness/
│   │   ├── core/              # Shared (Base entities, utils)
│   │   ├── wellness/          # 👤 DEV 1 - Wellness & Dashboard
│   │   ├── ai/                # 👤 DEV 2 - AI Companion & Library
│   │   ├── community/         # 👤 DEV 3 - Peer Support & Events
│   │   └── academic/          # 👤 DEV 4 - Academic Wellness & Gamification
│   ├── resources/
│   └── pom.xml
└── supabase/
    ├── migrations/
    └── functions/
```

---

## 👥 PHÂN CÔNG CHI TIẾT

### 🟦 **DEV 1 - WELLNESS MODULE** (Core User Experience)
**Package:** `com.wellness.wellness`

#### Chức năng:
1. **Wellness Dashboard** (Module 1)
   - Check-in cảm xúc hàng ngày
   - Wellness Score calculation
   - Biểu đồ xu hướng tâm trạng
   - Cảnh báo tâm trạng xuống dốc

#### Database Tables (Supabase):
```sql
- users (id, email, name, created_at)
- mood_checkins (id, user_id, date, emotion, note, wellness_score, heart_rate, face_analysis)
- wellness_scores (id, user_id, date, score, factors)
- mood_alerts (id, user_id, alert_type, severity, created_at)
```

#### API Endpoints:
```
POST   /api/wellness/checkin              # Check-in cảm xúc
GET    /api/wellness/dashboard/:userId    # Lấy dashboard data
GET    /api/wellness/mood-trend/:userId   # Biểu đồ xu hướng
POST   /api/wellness/face-analysis        # Upload ảnh phân tích
```

#### Key Classes:
- `WellnessServlet.java`
- `MoodCheckinEntity.java`
- `WellnessScoreCalculator.java`
- `MoodTrendAnalyzer.java`
- `FaceAnalysisService.java` (tích hợp ML API)

---

### 🟩 **DEV 2 - AI & CONTENT MODULE** (Intelligence Layer)
**Package:** `com.wellness.ai`

#### Chức năng:
1. **AI Companion** (Module 2)
   - Chat với AI tiếng Việt
   - Phát hiện khủng hoảng
   - Gợi ý hoạt động cá nhân hóa

2. **Wellness Library** (Module 3)
   - Quản lý content (thiền, breathing, podcast)
   - Recommendation engine

#### Database Tables:
```sql
- ai_conversations (id, user_id, message, response, sentiment, created_at)
- crisis_detections (id, user_id, message, keywords, hotline_triggered, timestamp)
- wellness_content (id, type, title, url, duration, tags, rating)
- user_recommendations (id, user_id, content_id, reason, created_at)
- user_content_progress (id, user_id, content_id, progress, completed)
```

#### API Endpoints:
```
POST   /api/ai/chat                       # Chat với AI
POST   /api/ai/crisis-check               # Kiểm tra khủng hoảng
GET    /api/library/content               # Lấy danh sách content
GET    /api/library/recommendations/:userId  # Gợi ý cá nhân hóa
POST   /api/library/progress              # Cập nhật tiến độ
```

#### Key Classes:
- `AiCompanionServlet.java`
- `ChatService.java` (tích hợp OpenAI/Gemini)
- `CrisisDetector.java`
- `ContentRepository.java`
- `RecommendationEngine.java`

---

### 🟨 **DEV 3 - COMMUNITY MODULE** (Social Features)
**Package:** `com.wellness.community`

#### Chức năng:
1. **Peer Support Network** (Module 4)
   - Diễn đàn ẩn danh
   - Nhóm hỗ trợ
   - Buddy System
   - Lịch sự kiện

#### Database Tables:
```sql
- forum_posts (id, user_id, content, tags, anonymous, created_at, likes)
- forum_comments (id, post_id, user_id, content, created_at)
- support_groups (id, name, topic, max_members, created_at)
- group_members (id, group_id, user_id, joined_at)
- buddy_pairs (id, user1_id, user2_id, status, created_at)
- buddy_checkins (id, pair_id, checker_id, date, message)
- events (id, title, type, datetime, link, organizer_id)
- event_participants (id, event_id, user_id, registered_at)
```

#### API Endpoints:
```
# Forum
POST   /api/community/posts               # Tạo bài viết
GET    /api/community/posts               # Lấy danh sách bài viết
POST   /api/community/posts/:id/comment   # Comment

# Groups
POST   /api/community/groups              # Tạo nhóm
GET    /api/community/groups/:id/join     # Tham gia nhóm

# Buddy System
POST   /api/community/buddy/request       # Tìm buddy
POST   /api/community/buddy/checkin       # Check-in với buddy

# Events
GET    /api/community/events              # Lấy lịch sự kiện
POST   /api/community/events/:id/register # Đăng ký event
```

#### Key Classes:
- `CommunityServlet.java`
- `ForumService.java`
- `SupportGroupManager.java`
- `BuddyMatchingService.java`
- `EventManager.java`

---

### 🟪 **DEV 4 - ACADEMIC & GAMIFICATION MODULE**
**Package:** `com.wellness.academic`

#### Chức năng:
1. **Academic Wellness** (Module 5)
   - Quản lý deadline
   - Pomodoro timer
   - Study rooms

2. **Gamification** (Module 6)
   - Challenges
   - Điểm & huy hiệu
   - Rewards

#### Database Tables:
```sql
# Academic
- user_tasks (id, user_id, title, deadline, priority, status, estimated_hours)
- pomodoro_sessions (id, user_id, task_id, duration, breaks, completed_at)
- study_rooms (id, name, topic, participants[], is_active)

# Gamification
- user_points (id, user_id, total_points, level)
- challenges (id, name, description, duration_days, points_reward)
- user_challenges (id, user_id, challenge_id, progress, completed)
- badges (id, name, icon, criteria)
- user_badges (id, user_id, badge_id, earned_at)
- rewards (id, title, points_cost, partner, type)
- reward_redemptions (id, user_id, reward_id, redeemed_at)
```

#### API Endpoints:
```
# Academic
POST   /api/academic/tasks                # Tạo task
GET    /api/academic/workload/:userId     # Tính toán workload
POST   /api/academic/pomodoro/start       # Bắt đầu Pomodoro
GET    /api/academic/study-rooms          # Danh sách study rooms

# Gamification
GET    /api/game/challenges               # Lấy challenges
POST   /api/game/challenges/:id/join      # Tham gia challenge
GET    /api/game/leaderboard              # Bảng xếp hạng
POST   /api/game/rewards/:id/redeem       # Đổi reward
```

#### Key Classes:
- `AcademicServlet.java`
- `TaskManager.java`
- `PomodoroTimer.java`
- `StudyRoomService.java`
- `GamificationEngine.java`
- `PointsCalculator.java`
- `BadgeAwardService.java`

---

## 🔗 SHARED MODULE (Core)
**Package:** `com.wellness.core`

### Ai sở hữu: **CẢ 4 DEVS** (thêm vào sau khi thống nhất)

#### Nội dung:
```java
core/
├── entity/
│   └── BaseEntity.java          # ID, timestamps
├── config/
│   ├── SupabaseConfig.java      # Supabase connection
│   └── CorsFilter.java          # CORS configuration
├── util/
│   ├── JsonUtil.java            # JSON parsing
│   ├── ValidationUtil.java      # Input validation
│   └── DateUtil.java            # Date helpers
└── exception/
    ├── BusinessException.java
    └── GlobalExceptionHandler.java
```

---

## 🗄️ SUPABASE SETUP

### Migration Files (Chia theo người):
```
supabase/migrations/
├── 001_dev1_wellness_tables.sql
├── 002_dev2_ai_content_tables.sql
├── 003_dev3_community_tables.sql
└── 004_dev4_academic_game_tables.sql
```

### Row Level Security (RLS):
- Mỗi dev tự config RLS cho tables của mình
- Shared function cho authentication

---

## 🚀 QUY TẮC LÀM VIỆC

### ✅ Git Workflow:
```bash
main (protected)
├── dev1-wellness
├── dev2-ai
├── dev3-community
└── dev4-academic
```

### ✅ Naming Convention:
- **Servlet**: `*Servlet.java` (VD: `WellnessServlet.java`)
- **Entity**: `*Entity.java` 
- **Service**: `*Service.java`
- **Repository**: `*Repository.java`

### ✅ API Response Format (Chuẩn hóa):
```json
{
  "success": true,
  "data": {},
  "message": "Success",
  "timestamp": "2025-11-15T10:30:00Z"
}
```

### ❌ Tránh Conflict:
1. **Không sửa code của module khác** trừ khi thống nhất
2. **Core module**: Tạo PR trước khi merge
3. **Database**: Review migrations trước khi chạy
4. **Dependencies**: Thêm vào `pom.xml` qua PR

---

## 📦 DEPENDENCIES CHUNG (pom.xml)

```xml
<!-- JPA & Database -->
<dependency>
    <groupId>org.hibernate</groupId>
    <artifactId>hibernate-core</artifactId>
    <version>6.2.7.Final</version>
</dependency>
<dependency>
    <groupId>org.postgresql</groupId>
    <artifactId>postgresql</artifactId>
    <version>42.6.0</version>
</dependency>

<!-- Servlet -->
<dependency>
    <groupId>javax.servlet</groupId>
    <artifactId>javax.servlet-api</artifactId>
    <version>4.0.1</version>
    <scope>provided</scope>
</dependency>

<!-- JSON -->
<dependency>
    <groupId>com.google.code.gson</groupId>
    <artifactId>gson</artifactId>
    <version>2.10.1</version>
</dependency>

<!-- Supabase Client (REST API) -->
<dependency>
    <groupId>com.squareup.okhttp3</groupId>
    <artifactId>okhttp</artifactId>
    <version>4.11.0</version>
</dependency>
```

---

## 📊 GANTT CHART (Thời gian ước tính)

| Dev | Module | Week 1 | Week 2 | Week 3 | Week 4 |
|-----|--------|--------|--------|--------|--------|
| DEV 1 | Wellness | 🟦 DB Setup | 🟦 API Dev | 🟦 Dashboard UI | 🟦 Testing |
| DEV 2 | AI & Content | 🟩 AI Integration | 🟩 Library CRUD | 🟩 Recommendations | 🟩 Testing |
| DEV 3 | Community | 🟨 Forum + Groups | 🟨 Buddy System | 🟨 Events | 🟨 Testing |
| DEV 4 | Academic + Game | 🟪 Task Manager | 🟪 Pomodoro | 🟪 Gamification | 🟪 Testing |

---

## 🔥 INTEGRATION POINTS (Điểm giao nhau)

### DEV 1 ↔️ DEV 2:
- **Wellness Score** → **AI Recommendations** (DEV 1 cung cấp score cho DEV 2)

### DEV 1 ↔️ DEV 4:
- **Mood Data** → **Points Calculation** (Check-in tâm trạng tăng điểm)

### DEV 3 ↔️ DEV 4:
- **Event Participation** → **Badge Awards** (Tham gia event được huy hiệu)

### DEV 2 ↔️ DEV 4:
- **Complete Content** → **Points** (Hoàn thành thiền/podcast được điểm)

---

## 📞 DAILY SYNC

- **Standup 15 phút**: Mỗi sáng báo cáo tiến độ
- **Code review**: Merge request cần 1 người approve
- **Integration test**: Cuối mỗi tuần test toàn bộ hệ thống

---

## 🎯 DELIVERABLES

### Week 1: Database + Core APIs
- ✅ Supabase tables created
- ✅ Basic CRUD APIs working

### Week 2: Business Logic
- ✅ AI integration functional
- ✅ Gamification engine ready

### Week 3: Frontend Integration
- ✅ All APIs connected to UI
- ✅ Real-time features tested

### Week 4: Testing + Polish
- ✅ End-to-end testing
- ✅ Performance optimization
- ✅ Documentation

---

**🚀 Good luck team!**
