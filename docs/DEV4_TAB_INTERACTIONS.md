# DEV4 Tab Interactions - Luồng tương tác giữa các trang

## 🔄 Sơ đồ tương tác tổng quan

```
┌──────────────────────────────────────────────────────────────────┐
│                      TASKS (Công việc học tập)                    │
│  - Tạo/xem/sửa tasks                                              │
│  - Mỗi task có: title, subject, hours, deadline, status          │
└────────────┬─────────────────────────────────────────────────────┘
             │
             │ [Nút "Bắt đầu"]
             ▼
┌──────────────────────────────────────────────────────────────────┐
│                    POMODORO TIMER                                 │
│  - Nhận task từ Tasks tab                                        │
│  - Hiển thị tên task đang làm                                    │
│  - Timer 25 phút                                                 │
└────────────┬─────────────────────────────────────────────────────┘
             │
             │ [Click "Hoàn thành"]
             ▼
┌──────────────────────────────────────────────────────────────────┐
│                      REWARDS SYSTEM                               │
│  ┌──────────────┐  ┌──────────────┐  ┌──────────────┐           │
│  │  +10 ĐIỂM    │  │  CHALLENGES  │  │    BADGES    │           │
│  │  mỗi session │  │  cập nhật    │  │  kiểm tra    │           │
│  └──────┬───────┘  └──────┬───────┘  └──────┬───────┘           │
│         │                  │                  │                   │
│         └──────────────────┴──────────────────┘                   │
│                            │                                      │
└────────────────────────────┼──────────────────────────────────────┘
                             │
                             ▼
┌──────────────────────────────────────────────────────────────────┐
│                      LEADERBOARD                                  │
│  - Cập nhật điểm real-time                                       │
│  - Highlight current user                                        │
│  - Sắp xếp theo điểm                                             │
└──────────────────────────────────────────────────────────────────┘

┌──────────────────────────────────────────────────────────────────┐
│                    SCHEDULE (Lịch học)                           │
│  - Upload lịch thi (Excel/CSV)                                   │
│  - Thêm thời gian bận                                            │
│  - Tạo gợi ý lịch học                                            │
│  - Xem timetable                                                 │
└────────────┬─────────────────────────────────────────────────────┘
             │
             │ [Click "Vào học"]
             ▼
┌──────────────────────────────────────────────────────────────────┐
│                    +5 ĐIỂM/GIỜ HỌC                               │
│  - Cập nhật challenge "Study Streak"                             │
│  - Refresh leaderboard                                           │
│  - Mark schedule completed                                       │
└──────────────────────────────────────────────────────────────────┘
```

## 📋 Chi tiết tương tác từng tab

### 1. TASKS ↔ POMODORO

**Cách hoạt động:**

```javascript
// Tab TASKS
User click "Bắt đầu" trên task
  ↓
startPomodoroWithTask(taskId, taskTitle)
  ↓
- Lưu task vào currentActiveTask
- Hiển thị tên task trong Pomodoro
- Chuyển sang tab Pomodoro
- Tự động start timer 25 phút
```

**Dữ liệu truyền:**
- `taskId`: ID của task
- `taskTitle`: Tên task để hiển thị
- `currentActiveTask`: Object chứa toàn bộ thông tin task

**UI Changes:**
- Task đang active có **border gradient tím**
- Pomodoro timer hiển thị "📚 [Tên task]"
- Button "Bắt đầu" → "Đang làm..." (disabled)

---

### 2. POMODORO → POINTS/CHALLENGES/BADGES

**Khi user hoàn thành Pomodoro:**

```javascript
completePomodoro()
  ↓
1. Cộng điểm cơ bản
   totalPoints += 10
   
2. Update task progress
   if (currentActiveTask) {
       currentActiveTask.hoursCompleted += 0.5
   }
   
3. Check task completion
   if (hoursCompleted >= estimatedHours) {
       totalPoints += 20  // Bonus
       task.status = 'completed'
       updateChallengeProgress('task', 1)
   }
   
4. Update challenges
   updateChallengeProgress('pomodoro', 1)
   Focus Master: current++
   
5. Check badges
   checkAndAwardBadges()
   - Focus Master: 20 pomodoros
   - Task Crusher: 10 tasks
   
6. Save & Refresh
   saveGamificationState()
   loadTasks()
   renderChallenges()
   loadLeaderboard()
```

**Dữ liệu cập nhật:**
- `totalPoints`: Tổng điểm tích lũy
- `todayPomodoroCount`: Đếm số session hôm nay
- `activeChallenges[].current`: Tiến độ challenges
- `earnedBadges[]`: Danh sách badges đã nhận
- `allTasks[]`: Cập nhật progress task

**UI Changes:**
- Toast notification: "+10 điểm"
- Progress bar challenges tăng
- Badge unlock animation (nếu đủ điều kiện)
- Leaderboard ranking update
- Task progress bar tăng

---

### 3. CHALLENGES → BADGES

**Auto-check khi challenge hoàn thành:**

```javascript
updateChallengeProgress(type, amount)
  ↓
activeChallenges.forEach(challenge => {
    if (challenge.type === type) {
        challenge.current += amount
        
        if (challenge.current >= challenge.target) {
            // Hoàn thành challenge
            challenge.completed = true
            totalPoints += challenge.points  // +50-150 điểm
            
            showToast(`Hoàn thành: ${challenge.name}! +${challenge.points} điểm`)
            
            // Trigger badge check
            checkAndAwardBadges()
        }
    }
})
```

**Challenges hiện có:**

| ID | Tên | Target | Trigger | Reward |
|----|-----|--------|---------|--------|
| 1 | Focus Master | 5 Pomodoro | Mỗi Pomodoro hoàn thành | 50 điểm |
| 2 | Task Crusher | 10 Tasks | Mỗi task hoàn thành 100% | 100 điểm |
| 3 | Study Streak | 7 Ngày | Mỗi ngày vào học | 150 điểm |

**Badges trigger:**

```javascript
// Focus Master Badge
if (todayPomodoroCount >= 20 && !earnedBadges.includes('focus_master')) {
    earnedBadges.push('focus_master')
    showToast('Nhận huy hiệu: Focus Master!')
}

// Task Crusher Badge
const completedTasks = allTasks.filter(t => t.status === 'completed').length
if (completedTasks >= 10 && !earnedBadges.includes('task_crusher')) {
    earnedBadges.push('task_crusher')
    showToast('Nhận huy hiệu: Task Crusher!')
}
```

---

### 4. SCHEDULE → POINTS/CHALLENGES

**Khi user click "Vào học" trên timetable:**

```javascript
markScheduleAsCompleted(subject, date)
  ↓
1. Tìm schedule item
   scheduleItem = acceptedSchedule.find(...)
   
2. Mark completed
   scheduleItem.completed = true
   scheduleItem.completedAt = new Date()
   
3. Award points
   const hours = scheduleItem.suggestedHours || 2
   const points = hours * 5  // 5 điểm/giờ
   totalPoints += points
   
4. Update challenge
   updateChallengeProgress('daily', 1)
   Study Streak: current++
   
5. Save & Refresh
   saveAcceptedSchedule()
   saveGamificationState()
   renderTimetable()
   loadLeaderboard()
```

**Dữ liệu truyền:**
- `subject`: Tên môn học
- `date`: Ngày học (YYYY-MM-DD)
- `hours`: Số giờ học
- `points`: Điểm nhận được

**UI Changes:**
- Timetable block chuyển màu xanh (completed)
- Toast: "Hoàn thành: Toán - 2h (+10 điểm)"
- Progress bar Study Streak tăng
- Leaderboard update

---

### 5. ALL TABS → LEADERBOARD

**Real-time leaderboard updates:**

```javascript
// Được gọi sau mọi hành động có điểm:
loadLeaderboard()
  ↓
1. Create rows array
   rows = [
       { rank: 1, username: 'Bạn', points: totalPoints, isCurrentUser: true },
       ... sample users with random points ...
   ]
   
2. Sort by points
   rows.sort((a, b) => b.points - a.points)
   
3. Update ranks
   rows.forEach((row, idx) => row.rank = idx + 1)
   
4. Render HTML
   - Current user: gradient background + "BẠN" badge
   - Top 3: gold/silver/bronze stars
   - Others: normal style
```

**Triggers:**
- ✅ Hoàn thành Pomodoro (+10 điểm)
- ✅ Hoàn thành task (+20 điểm)
- ✅ Hoàn thành challenge (+50-150 điểm)
- ✅ Vào học từ schedule (+5 điểm/giờ)

---

## 💾 Data Persistence Flow

**LocalStorage Keys:**

```javascript
// Gamification state
'totalPoints' → Tổng điểm
'todayPomodoroCount' → Số Pomodoro hôm nay
'activeChallenges' → JSON challenges
'earnedBadges' → JSON badges
'lastSaveDate' → Ngày lưu cuối

// Schedule state
'acceptedSchedule' → JSON lịch học đã chấp nhận
'studySessions' → JSON buổi học đã hoàn thành

// Busy slots
'busySlots' → JSON thời gian bận
```

**Save triggers:**

```javascript
saveGamificationState() {
    localStorage.setItem('totalPoints', totalPoints)
    localStorage.setItem('todayPomodoroCount', todayPomodoroCount)
    localStorage.setItem('activeChallenges', JSON.stringify(activeChallenges))
    localStorage.setItem('earnedBadges', JSON.stringify(earnedBadges))
    localStorage.setItem('lastSaveDate', today)
}

// Được gọi từ:
- completePomodoro()
- updateChallengeProgress() (khi complete)
- checkAndAwardBadges() (khi unlock badge)
- markScheduleAsCompleted()
```

**Load on startup:**

```javascript
window.onload = () => {
    loadGamificationState()  // Restore points, challenges, badges
    loadAcceptedSchedule()   // Restore timetable
    loadStudySessionsFromStorage()  // Restore study history
    loadInitialData()        // Fetch từ backend
}
```

---

## 🎯 User Journey Examples

### Scenario 1: Học bài và nhận thưởng

```
1. User vào tab TASKS
   → Thấy task "Học Toán - Chương 1" (4h, deadline: 2025-11-20)
   
2. Click nút "Bắt đầu"
   → Chuyển sang POMODORO
   → Timer hiển thị "📚 Học Toán - Chương 1"
   → Bắt đầu đếm ngược 25:00
   
3. Làm việc 25 phút
   → Timer hết giờ
   → Click "Hoàn thành phiên làm việc"
   
4. Nhận thưởng tức thì:
   ✅ +10 điểm (Pomodoro)
   ✅ Task progress: 0.5h/4h (12.5%)
   ✅ Challenge "Focus Master": 1/5
   ✅ Toast: "Phiên Pomodoro hoàn thành! +10 điểm"
   
5. Kiểm tra tabs:
   → TASKS: Task có progress bar 12.5%
   → CHALLENGES: Focus Master progress bar 20%
   → LEADERBOARD: Điểm tăng từ 0 → 10
   → BADGES: Focus Master (0/20 pomodoros)
   
6. Lặp lại 19 lần nữa:
   → Sau 20 Pomodoros:
   ✅ +200 điểm (20 × 10)
   ✅ Unlock badge "Focus Master"
   ✅ Challenge "Focus Master" hoàn thành: +50 điểm bonus
   ✅ Tổng: 250 điểm
```

### Scenario 2: Học theo lịch

```
1. User vào tab SCHEDULE
   → Upload file lịch thi (exam.csv)
   → Thấy: Toán (2025-11-25), Lý (2025-11-28)
   
2. Thêm thời gian bận:
   → Thứ 2-6: 8:00-17:00 (đi làm)
   → Thứ 7: 9:00-12:00 (gym)
   
3. Nhập thông tin môn học:
   → Toán: 5 chương, 2h/chương = 10h total
   → Lý: 4 chương, 1.5h/chương = 6h total
   
4. Click "Tạo gợi ý lịch học"
   → Hệ thống suggest:
     • Thứ 2 (18:00-20:00): Toán - Chương 1 (2h)
     • Thứ 3 (18:00-20:00): Toán - Chương 2 (2h)
     • Thứ 4 (19:00-21:00): Lý - Chương 1 (1.5h)
     ... (tránh giờ bận)
   
5. Chấp nhận gợi ý
   → Timetable hiển thị dạng blocks màu
   → Mỗi block: subject, time, duration, nút "Vào học"
   
6. Đến giờ học:
   → Click "Vào học" trên block Toán
   → Block chuyển màu xanh (completed)
   ✅ +10 điểm (2h × 5 điểm/h)
   ✅ Challenge "Study Streak": 1/7
   ✅ Toast: "Hoàn thành: Toán - 2h (+10 điểm)"
   
7. Sau 7 ngày liên tiếp:
   ✅ Challenge "Study Streak" complete: +150 điểm
   ✅ Badge "Streak Master" unlocked
```

### Scenario 3: Hoàn thành task

```
1. Task "Làm bài tập Toán" (2h)
   → Start Pomodoro 4 lần (4 × 0.5h = 2h)
   
2. Sau Pomodoro thứ 4:
   → Task progress: 2h/2h (100%)
   → Task status: 'completed'
   ✅ +10 điểm (Pomodoro)
   ✅ +20 điểm (Task completion bonus)
   ✅ Challenge "Task Crusher": 1/10
   ✅ Toast: "Hoàn thành công việc: Làm bài tập Toán! +20 điểm"
   
3. Hoàn thành 10 tasks:
   ✅ Challenge "Task Crusher" complete: +100 điểm
   ✅ Badge "Task Crusher" unlocked
   ✅ Tổng điểm: 10×10 + 10×20 + 100 = 400 điểm
```

---

## 🎨 UI Improvements - Schedule Tab

### Trước (Old):

```
[Date] [Date] [Time] [Time] [Xóa]
```

### Sau (New - Tham khảo từ Tasks deadline):

```
┌─────────────────────────────────────────────────────────┐
│  Từ ngày                    Đến ngày                    │
│  ┌─────────┬─────────┐     ┌─────────┬─────────┐       │
│  │ Date    │ Time    │     │ Date    │ Time    │ [Xóa] │
│  │ picker  │ picker  │     │ picker  │ picker  │       │
│  └─────────┴─────────┘     └─────────┴─────────┘       │
└─────────────────────────────────────────────────────────┘

Features:
✅ Gradient background (purple)
✅ Rounded corners (12px)
✅ Labels rõ ràng "Từ ngày" / "Đến ngày"
✅ Grid layout 2 columns
✅ Default time: 08:00 - 17:00
✅ Hover effect: translateY(-2px) + shadow
✅ Better spacing & padding
```

**Code Implementation:**

```javascript
row.style.cssText = `
    background: linear-gradient(135deg, rgba(102, 126, 234, 0.05) 0%, rgba(118, 75, 162, 0.05) 100%);
    border: 1px solid rgba(102, 126, 234, 0.2);
    border-radius: 12px;
    padding: 16px;
    margin-bottom: 12px;
    display: grid;
    grid-template-columns: 1fr 1fr auto;
    gap: 12px;
`;

// Hover animation
row.addEventListener('mouseenter', () => {
    row.style.transform = 'translateY(-2px)';
    row.style.boxShadow = '0 8px 20px rgba(102, 126, 234, 0.15)';
});
```

---

## 📊 Data Flow Diagram

```
┌─────────────────────────────────────────────────────────────┐
│                     USER ACTIONS                             │
└────────┬────────────────────────────────────────────────────┘
         │
         ├─► Click "Bắt đầu" task ────────► Pomodoro tab
         │                                        │
         ├─► Complete Pomodoro ───────► Points + Challenges
         │                                        │
         ├─► Click "Vào học" ─────────► Points + Schedule
         │                                        │
         └─► View tabs ──────────────► Render UI updates
                                               │
         ┌─────────────────────────────────────┘
         │
         ▼
┌─────────────────────────────────────────────────────────────┐
│                   STATE MANAGEMENT                           │
│  ┌──────────────┐  ┌──────────────┐  ┌──────────────┐      │
│  │ totalPoints  │  │  challenges  │  │    badges    │      │
│  │ pomodoroCount│  │  allTasks[]  │  │   schedule   │      │
│  └──────┬───────┘  └──────┬───────┘  └──────┬───────┘      │
│         │                  │                  │              │
│         └──────────────────┴──────────────────┘              │
│                            │                                 │
│                            ▼                                 │
│                    saveGamificationState()                   │
│                            │                                 │
└────────────────────────────┼─────────────────────────────────┘
                             │
                             ▼
┌─────────────────────────────────────────────────────────────┐
│                     LOCALSTORAGE                             │
│  - totalPoints                                               │
│  - todayPomodoroCount                                        │
│  - activeChallenges (JSON)                                   │
│  - earnedBadges (JSON)                                       │
│  - acceptedSchedule (JSON)                                   │
│  - lastSaveDate                                              │
└─────────────────────────────────────────────────────────────┘
         │
         │ window.onload
         ▼
┌─────────────────────────────────────────────────────────────┐
│               RESTORE STATE ON RELOAD                        │
│  loadGamificationState() → Restore all data                  │
└─────────────────────────────────────────────────────────────┘
```

---

## 🔧 API Integration Points

### Current Status:
- ✅ Frontend: All features working with localStorage
- ⚠️ Backend: Some endpoints return 400 (OK - fallback to localStorage)

### Backend Endpoints (Optional):

```
GET  /api/academic/tasks?userId=1          → Fetch tasks from DB
POST /api/academic/tasks                   → Create task
PUT  /api/academic/tasks/:id               → Update task

GET  /api/academic/points?userId=1         → Fetch points
POST /api/academic/pomodoro/complete       → Log pomodoro session

GET  /api/academic/busy-slots?userId=1     → Fetch busy slots
POST /api/academic/busy-slots              → Save busy slots

GET  /api/academic/challenges?userId=1     → Fetch challenges
POST /api/academic/challenges/progress     → Update challenge

GET  /api/academic/badges?userId=1         → Fetch badges
```

### Fallback Strategy:
```javascript
async function loadTasks() {
    try {
        const res = await fetch(`${API_BASE}/academic/tasks?userId=${currentUserId}`);
        if (!res.ok) throw new Error('API not available');
        const data = await res.json();
        allTasks = data.data;
    } catch (error) {
        // Fallback: Load from localStorage
        const saved = localStorage.getItem('tasks');
        allTasks = saved ? JSON.parse(saved) : [];
    }
    renderTasksPage();
}
```

---

## 📝 Summary

**6 Tabs tương tác:**

1. **TASKS** → Tạo tasks, click "Bắt đầu" → POMODORO
2. **POMODORO** → Hoàn thành → Points, Challenges, Badges
3. **CHALLENGES** → Track progress → Award points & badges
4. **SCHEDULE** → "Vào học" → Points & Study Streak
5. **LEADERBOARD** → Real-time ranking update
6. **BADGES** → Display unlocked achievements

**Key Features:**
- ✅ Real-time updates across all tabs
- ✅ Points economy (10/session, 20/task, 5/hour, 50-150/challenge)
- ✅ Persistent state (localStorage)
- ✅ Visual feedback (toasts, progress bars, highlights)
- ✅ Modern UI (gradient, glassmorphism, animations)

**Data Flow:**
```
User Action → State Update → Save to LocalStorage → Render UI → Toast Notification
```

---

**Last Updated**: 2025-11-15  
**Version**: 2.0.0
