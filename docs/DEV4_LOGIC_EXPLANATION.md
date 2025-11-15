# 📚 DEV4 - ACADEMIC WELLNESS & GAMIFICATION - GIẢI THÍCH LOGIC

## 🎯 TỔNG QUAN

DEV4 quản lý:
1. **Công việc học tập** (Tasks)
2. **Pomodoro Timer** (Focus sessions)
3. **Lịch học tự động** (Schedule planner)
4. **Gamification** (Points, Challenges, Badges, Leaderboard)

---

## 📋 PHẦN 1: LỊCH HỌC TỰ ĐỘNG (Schedule Planner)

### 🔄 FLOW HOẠT ĐỘNG

```
┌──────────────────────────────────────────────────────────────┐
│  BƯỚC 1: NHẬP DỮ LIỆU ĐẦU VÀO                               │
└──────────────────────────────────────────────────────────────┘
1. User upload file Excel lịch thi (.xlsx, .xls)
   Cấu trúc file:
   | Môn học | Ngày thi   | Giờ thi (optional) |
   | Math    | 2025-11-20 | 08:00              |
   | Physics | 2025-11-25 | 10:00              |
   
2. Backend parse Excel → lưu vào `inMemoryExamSchedules`
   Structure: Map<userId, List<ExamEntry>>
   ExamEntry: { subject, date, time }

3. User thiết lập metadata cho môn học:
   - Số chương (chapters)
   - Giờ/chương (hoursPerChapter)
   → Lưu vào `subjectMeta` (client-side)
   Example: { "Math": { chapters: 5, hoursPerChapter: 2 } }
   → Total hours needed = 5 × 2 = 10h

4. User nhập busy slots (thời gian bận):
   - Start date, End date
   - Start time, End time
   → Lưu vào `inMemoryBusySlots`
   
┌──────────────────────────────────────────────────────────────┐
│  BƯỚC 2: TẠO GỢI Ý LỊCH HỌC                                 │
└──────────────────────────────────────────────────────────────┘
User click "Tạo gợi ý lịch học"
→ Frontend gọi: GET /api/academic/suggested-schedule/{userId}

Backend logic (AcademicServlet.java):
1. Đọc exam schedule của user
2. Đọc busy slots của user
3. Với mỗi môn thi:
   - Total hours = 6h (mặc định)
   - Block size = 2h
   - Start from: LocalDate.now()
   - End before: exam date - 1 day
   
4. Lặp qua từng ngày:
   - Check xem ngày có trùng busy slot không
   - Nếu không bận → tạo suggestion:
     * subject
     * date
     * startTime: 18:00 (default)
     * endTime: 20:00 (18:00 + 2h)
     * hours: 2
   - Giảm totalHours đi 2h
   - Lặp cho đến khi đủ 6h hoặc hết ngày

5. Trả về JSON:
   {
     "success": true,
     "data": [
       {
         "subject": "Math",
         "date": "2025-11-16",
         "startTime": "18:00",
         "endTime": "20:00",
         "hours": 2
       },
       ...
     ]
   }

┌──────────────────────────────────────────────────────────────┐
│  BƯỚC 3: HIỂN THỊ GỢI Ý (Frontend)                          │
└──────────────────────────────────────────────────────────────┘
Frontend nhận data → `renderSuggestions()`

1. Chuyển đổi data format:
   API response → Frontend format:
   {
     subject: "Math",
     date: "2025-11-16",
     time: "18:00-20:00",
     suggestedHours: 2,
     suggestedSlots: [{ start: "18:00", end: "20:00" }]
   }

2. Render table với pagination (5 items/page):
   ┌────────────────────────────────────────────────────┐
   │ Môn   │ Ngày     │ Giờ  │ Phiên │ Hạn  │ Thao tác │
   ├────────────────────────────────────────────────────┤
   │ Math  │15/11/25  │ 2h   │  1    │18:00 │ ✓  ✕     │
   │ Math  │16/11/25  │ 2h   │  1    │18:00 │ ✓  ✕     │
   │ Math  │17/11/25  │ 2h   │  1    │18:00 │ ✓  ✕     │
   └────────────────────────────────────────────────────┘

3. User có thể:
   - ✓ Chấp nhận → thêm vào `acceptedSchedule[]`
   - ✕ Từ chối → bỏ qua

┌──────────────────────────────────────────────────────────────┐
│  BƯỚC 4: CHẤP NHẬN GỢI Ý                                     │
└──────────────────────────────────────────────────────────────┘
User click "✓ Chấp nhận" → `acceptSuggestion(idx)`

1. Lấy suggestion từ `allSuggestions[idx]`
2. Check duplicate: subject + date
3. Add vào `acceptedSchedule[]` với timestamp
4. Lưu vào localStorage: `acceptedSchedule_{userId}`
5. Update UI:
   - Disable button "Chấp nhận"
   - Hiển thị "✓ Đã thêm"
   - Green border
6. Trigger render:
   - `renderTimetable()` → Hiển thị blocks/cards
   - `renderProgress()` → Hiển thị tiến độ

┌──────────────────────────────────────────────────────────────┐
│  BƯỚC 5: HIỂN THỊ LỊCH HỌC ĐÃ CHẤP NHẬN                     │
└──────────────────────────────────────────────────────────────┘
`renderTimetable()`

1. Group by date:
   {
     "2025-11-16": [Math, Physics],
     "2025-11-17": [Chemistry],
     ...
   }

2. Render grid of day cards:
   ┌──────────────┐  ┌──────────────┐  ┌──────────────┐
   │  Thứ 2       │  │  Thứ 3       │  │  Thứ 4       │
   │  15/11/2025  │  │  16/11/2025  │  │  17/11/2025  │
   ├──────────────┤  ├──────────────┤  ├──────────────┤
   │ 📘 Math      │  │ 📕 Physics   │  │ 📗 Chemistry │
   │ 🕐 18:00-20:00│  │ 🕐 19:00-21:00│  │ 🕐 20:00-22:00│
   │ 📚 2h        │  │ 📚 2h        │  │ 📚 2h        │
   └──────────────┘  └──────────────┘  └──────────────┘

3. Mỗi block có:
   - Màu sắc dựa trên hash subject name
   - Hover effect (nổi lên + shadow)
   - Click → confirm xóa

┌──────────────────────────────────────────────────────────────┐
│  BƯỚC 6: THEO DÕI TIẾN ĐỘ                                   │
└──────────────────────────────────────────────────────────────┘
`renderProgress(subjects, matrix)`

1. Build matrix từ acceptedSchedule:
   matrix[subject][date] = total hours

2. Calculate per subject:
   - Needed: chapters × hoursPerChapter
   - Scheduled: sum of matrix[subject][*]
   - Studied: từ studySessions (user tự ghi nhận)
   - Progress %: (scheduled / needed) × 100

3. Render progress table với bars:
   ┌─────────────────────────────────────────────────┐
   │ Môn │ Dự định│ Đã học│ Cần học│ Tiến độ       │
   ├─────────────────────────────────────────────────┤
   │Math │ 6h     │ 2h    │ 10h    │[████░░] 60%   │
   │Phys │ 4h     │ 1h    │  8h    │[███░░░] 50%   │
   └─────────────────────────────────────────────────┘

```

---

## 🎮 PHẦN 2: GAMIFICATION SYSTEM

### 💎 POINTS SYSTEM

```javascript
// Points được trao khi:
1. Hoàn thành Pomodoro session: +10 điểm
2. Hoàn thành challenge: +50 điểm (configurable)
3. Daily check-in (từ DEV1): +10 điểm
4. Wellness bonus: +5 điểm (khi mood tốt)

// Level progression:
Level = Math.floor(totalPoints / 100) + 1
- Level 1: 0-99 pts
- Level 2: 100-199 pts
- Level 3: 200-299 pts
...
```

### 🏆 BADGES

```javascript
// Badge criteria (example):
{
  "Focus Master": {
    criteria: "Complete 20 Pomodoro sessions",
    check: pomodoroCount >= 20
  },
  "Streak Master": {
    criteria: "7 consecutive check-ins",
    check: consecutiveDays >= 7
  },
  "Stress-Free Week": {
    criteria: "Wellness score > 70 for 7 days",
    check: avgWellnessScore > 70 && days >= 7
  }
}
```

### 🎯 CHALLENGES

```javascript
// Challenge structure:
{
  id: 1,
  name: "Pomodoro Weekly",
  description: "Complete 10 Pomodoro sessions this week",
  duration_days: 7,
  points_reward: 50,
  category: "focus",
  
  // User progress:
  progress: 5/10,
  completed: false
}
```

---

## 📊 PHẦN 3: DATA STRUCTURES

### Frontend (JavaScript)

```javascript
// Global variables
let currentUserId = 1;
let allTasks = []; // All tasks from backend
let acceptedSchedule = []; // Accepted suggestions
let studySessions = {}; // User-reported study hours
let subjectMeta = {}; // Subject metadata (chapters, hours)

// Task structure
{
  id: 1,
  userId: 1,
  title: "Complete Math homework",
  description: "Chapter 5 exercises",
  deadline: "2025-11-20",
  priority: "high", // high, medium, low
  status: "pending", // pending, in_progress, completed
  estimatedHours: 3,
  subject: "Math" // optional
}

// Pomodoro session
{
  id: 1,
  userId: 1,
  taskId: 1,
  durationMinutes: 25,
  breaksTaken: 2,
  completed: true,
  startedAt: "2025-11-15T14:00:00",
  completedAt: "2025-11-15T14:25:00"
}

// User points
{
  userId: 1,
  totalPoints: 120,
  pomodorosToday: 3,
  level: 2
}
```

### Backend (Java)

```java
// UserTaskEntity
@Entity
@Table(name = "user_tasks")
public class UserTaskEntity {
    @Id @GeneratedValue
    private Long id;
    private Long userId;
    private String title;
    private String description;
    private LocalDate deadline;
    private String priority; // high, medium, low
    private String status; // pending, in_progress, completed
    private Integer estimatedHours;
    private Integer actualHours;
}

// PomodoroSessionEntity
@Entity
@Table(name = "pomodoro_sessions")
public class PomodoroSessionEntity {
    @Id @GeneratedValue
    private Long id;
    private Long userId;
    private Long taskId;
    private Integer durationMinutes;
    private Integer breaksTaken;
    private Boolean completed;
    private LocalDateTime startedAt;
    private LocalDateTime completedAt;
}

// UserPointsEntity
@Entity
@Table(name = "user_points")
public class UserPointsEntity {
    @Id @GeneratedValue
    private Long id;
    private Long userId;
    private Integer totalPoints;
    private Integer level;
}
```

---

## 🔌 PHẦN 4: API ENDPOINTS

### Tasks Management
```
POST   /api/academic/tasks
Body:  {userId, title, description, deadline, priority, estimatedHours}
Response: {success: true, data: TaskEntity}

GET    /api/academic/tasks/{userId}
Response: {success: true, data: [TaskEntity, ...]}

GET    /api/academic/workload/{userId}
Response: {
  success: true,
  data: {
    userId: 1,
    totalHoursNeeded: 15,
    pendingTasksCount: 5,
    isOverloaded: false,
    stressLevel: "MEDIUM"
  }
}
```

### Pomodoro
```
POST   /api/academic/pomodoro/start
Body:  {userId, taskId, durationMinutes}
Response: {success: true, data: PomodoroSessionEntity}

POST   /api/academic/pomodoro/complete
Body:  {pomodoroId, breaksTaken}
Response: {
  success: true,
  data: {
    sessionId: 1,
    pointsAwarded: 10,
    completedAt: "2025-11-15T14:25:00"
  }
}
```

### Schedule Planner
```
POST   /api/academic/exam-schedule/upload (multipart)
FormData: file (Excel), userId
Response: {success: true, data: [ExamEntry, ...]}

GET    /api/academic/exam-schedule/{userId}
Response: {success: true, data: [ExamEntry, ...]}

GET    /api/academic/suggested-schedule/{userId}
Response: {
  success: true,
  data: [
    {subject, date, startTime, endTime, hours},
    ...
  ]
}
```

### Points & Stats
```
GET    /api/academic/points/{userId}
Response: {
  success: true,
  data: {
    totalPoints: 120,
    pomodorosToday: 3,
    level: 2
  }
}
```

---

## 🐛 COMMON BUGS & FIXES

### Bug 1: "renderScheduleChart is not defined"
**Nguyên nhân:** Hàm đã bị xóa khi refactor
**Fix:** Xóa dòng `renderScheduleChart(data.data)` trong `requestSuggestions()`

### Bug 2: Suggestions table empty
**Nguyên nhân:** 
- Chưa upload lịch thi
- Backend trả về data khác format
**Fix:** 
- Upload file Excel trước
- Transform data trong `requestSuggestions()`

### Bug 3: Accepted schedule không hiển thị
**Nguyên nhân:** localStorage chưa load
**Fix:** Thêm `loadAcceptedSchedule()` vào `window.onload`

### Bug 4: Progress chart sai
**Nguyên nhân:** `buildMatrix()` chưa được gọi
**Fix:** Call `buildMatrix()` trước khi render progress

---

## 📝 CHECKLIST TESTING

### Lịch học
- [ ] Upload Excel lịch thi thành công
- [ ] Hiển thị danh sách môn học
- [ ] Thiết lập metadata môn học (chapters, hours)
- [ ] Nhập busy slots
- [ ] Click "Tạo gợi ý" → hiển thị table
- [ ] Chấp nhận gợi ý → hiển thị timetable blocks
- [ ] Xóa từng block trong timetable
- [ ] Xóa toàn bộ lịch
- [ ] Refresh page → lịch vẫn còn (localStorage)

### Pomodoro
- [ ] Start timer → countdown chạy
- [ ] Pause → timer dừng
- [ ] Reset → timer về 25:00
- [ ] Complete → +10 điểm
- [ ] Stats cập nhật (pomodorosToday)

### Gamification
- [ ] Leaderboard hiển thị top users
- [ ] Badges hiển thị đúng
- [ ] Challenges progress update
- [ ] Points tính đúng
- [ ] Level progression đúng

---

## 🚀 DEPLOYMENT CHECKLIST

1. Build project: `mvn clean package`
2. Check logs: `tail -f tomcat/logs/catalina.out`
3. Test APIs: Postman/curl
4. Test frontend: Chrome DevTools → Console
5. Clear localStorage if needed
6. Test on mobile (responsive)

---

**Happy Coding! 🎉**
