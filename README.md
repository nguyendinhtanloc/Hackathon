# 🧠 Smart Mental Wellness & Academic Optimization Hub

Hệ thống quản lý và hỗ trợ sức khỏe tinh thần toàn diện cho sinh viên.

## 🏗️ Kiến Trúc Dự Án

### Backend Stack:
- **Java Servlet** (HTTP handling)
- **JPA/Hibernate** (ORM)
- **Supabase PostgreSQL** (Database)
- **Gson** (JSON processing)

### Phân Chia Module (4 Developers):

```
📦 smart-mental-wellness/
├── 🟦 DEV 1 - Wellness Module
│   ├── Wellness Dashboard
│   ├── Mood Check-in (Face + Heart Rate)
│   └── Wellness Score & Alerts
│
├── 🟩 DEV 2 - AI & Content Module
│   ├── AI Companion (Chat 24/7)
│   ├── Crisis Detection
│   └── Wellness Library (Meditation, Podcast)
│
├── 🟨 DEV 3 - Community Module
│   ├── Anonymous Forum
│   ├── Support Groups
│   ├── Buddy System
│   └── Events Management
│
└── 🟪 DEV 4 - Academic & Gamification
    ├── Task Management
    ├── Pomodoro Timer
    ├── Study Rooms
    ├── Challenges & Badges
    └── Rewards System
```

## 🚀 Setup Instructions

### 1. Cài đặt Dependencies

```bash
# Clone repository
git clone <repo-url>
cd hackathon

# Build project
mvn clean install
```

### 2. Cấu hình Supabase

1. Tạo project trên [Supabase](https://supabase.com)
2. Copy database URL và credentials
3. Cập nhật `src/main/resources/persistence.xml`:

```xml
<property name="javax.persistence.jdbc.url" value="jdbc:postgresql://YOUR_HOST:5432/postgres"/>
<property name="javax.persistence.jdbc.user" value="postgres"/>
<property name="javax.persistence.jdbc.password" value="YOUR_PASSWORD"/>
```

### 3. Chạy Migrations

```bash
# Connect to Supabase SQL Editor
# Copy & paste từng file trong supabase/migrations/ theo thứ tự:
# 001_dev1_wellness_tables.sql
# 002_dev2_ai_content_tables.sql
# 003_dev3_community_tables.sql
# 004_dev4_academic_game_tables.sql
```

### 4. Deploy lên Tomcat

```bash
# Build WAR file
mvn package

# Deploy to Tomcat
cp target/wellness-app.war /path/to/tomcat/webapps/
```

### 5. Test API

```bash
# Test Wellness API
curl http://localhost:8080/wellness-app/api/wellness/dashboard/1

# Test AI Chat
curl -X POST http://localhost:8080/wellness-app/api/ai/chat \
  -H "Content-Type: application/json" \
  -d '{"userId": 1, "message": "Tôi cảm thấy lo lắng"}'
```

## 📂 Cấu Trúc Thư Mục

```
src/main/java/com/wellness/
├── core/                    # Shared code (BaseEntity, Utils)
│   ├── entity/
│   ├── config/
│   └── util/
├── wellness/               # 🟦 DEV 1
│   ├── entity/
│   ├── servlet/
│   └── service/
├── ai/                     # 🟩 DEV 2
│   ├── entity/
│   ├── servlet/
│   └── service/
├── community/              # 🟨 DEV 3
│   ├── entity/
│   ├── servlet/
│   └── service/
└── academic/               # 🟪 DEV 4
    ├── entity/
    ├── servlet/
    └── service/
```

## 🔀 Git Workflow

### Branch Strategy:

```bash
main (protected)
├── dev1-wellness
├── dev2-ai
├── dev3-community
└── dev4-academic
```

### Daily Workflow:

```bash
# 1. Tạo branch từ main
git checkout -b dev1-wellness

# 2. Code trong module của bạn
# 3. Commit thường xuyên
git add .
git commit -m "feat: implement mood check-in API"

# 4. Push lên remote
git push origin dev1-wellness

# 5. Tạo Pull Request
# 6. Request review từ 1 người khác
# 7. Merge vào main
```

## 📋 API Endpoints

### DEV 1 - Wellness (`/api/wellness/*`)
- `POST /api/wellness/checkin` - Check-in cảm xúc
- `GET /api/wellness/dashboard/:userId` - Dashboard data
- `GET /api/wellness/mood-trend/:userId` - Biểu đồ xu hướng

### DEV 2 - AI & Content (`/api/ai/*`, `/api/library/*`)
- `POST /api/ai/chat` - Chat với AI
- `POST /api/ai/crisis-check` - Kiểm tra khủng hoảng
- `GET /api/library/content` - Danh sách content
- `GET /api/library/recommendations/:userId` - Gợi ý cá nhân hóa

### DEV 3 - Community (`/api/community/*`)
- `GET /api/community/posts` - Lấy bài viết forum
- `POST /api/community/posts` - Tạo bài viết
- `POST /api/community/buddy/request` - Tìm buddy
- `GET /api/community/events` - Lịch sự kiện

### DEV 4 - Academic & Game (`/api/academic/*`, `/api/game/*`)
- `POST /api/academic/tasks` - Tạo task
- `GET /api/academic/workload/:userId` - Tính workload
- `GET /api/game/challenges` - Danh sách challenges
- `POST /api/game/rewards/:id/redeem` - Đổi reward

## 🔒 Security

- **Row Level Security (RLS)** enabled on Supabase
- Users can only access their own data
- CORS enabled for cross-origin requests
- Input validation required

## 🧪 Testing

```bash
# Run tests
mvn test

# Test coverage
mvn jacoco:report
```

## 📚 Documentation

- [Full Project Structure](PROJECT_STRUCTURE.md)
- [Database Schema](supabase/migrations/)
- [API Documentation](docs/API.md) *(TODO)*

## 👥 Team

- **DEV 1** - Wellness Module
- **DEV 2** - AI & Content Module
- **DEV 3** - Community Module
- **DEV 4** - Academic & Gamification

## 📞 Support

- Standup meeting: Mỗi sáng 9:00 AM
- Code review: Required trước khi merge
- Integration test: Mỗi cuối tuần

## 🎯 Milestone

- **Week 1**: Database + Core APIs
- **Week 2**: Business Logic
- **Week 3**: Frontend Integration
- **Week 4**: Testing + Polish

---

**🚀 Let's build something amazing together!**
