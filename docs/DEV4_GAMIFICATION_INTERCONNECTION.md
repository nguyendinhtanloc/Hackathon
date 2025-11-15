# DEV4 Gamification Interconnection System

## 📋 Tổng quan

Hệ thống gamification của DEV4 đã được nâng cấp để tạo sự liên kết cao giữa tất cả các tab (Tasks, Pomodoro, Challenges, Schedule, Leaderboard, Badges). Mọi hành động của người dùng đều ảnh hưởng đến nhiều phần khác nhau trong hệ thống.

## 🔗 Sơ đồ liên kết

```
┌─────────────┐
│   TASKS     │─┐
└─────────────┘ │
                │  Bắt đầu Pomodoro
                ▼
┌─────────────┐      +10 điểm/session
│  POMODORO   │──────────────────────┐
└─────────────┘                      │
       │                             │
       │ Hoàn thành                  │
       ▼                             ▼
┌─────────────┐                ┌─────────────┐
│ CHALLENGES  │◄───────────────│   POINTS    │
└─────────────┘   Cập nhật     └─────────────┘
       │          progress            │
       │                              │
       │ Đạt target                   │ Hiển thị
       ▼                              ▼
┌─────────────┐                ┌─────────────┐
│   BADGES    │                │ LEADERBOARD │
└─────────────┘                └─────────────┘
       ▲                              ▲
       │                              │
       └──────────────────────────────┘
              Cập nhật khi có badge mới

┌─────────────┐
│  SCHEDULE   │──► Vào học ──► +5 điểm/giờ ──► Cập nhật challenges
└─────────────┘
```

## 🎯 Chi tiết tính năng

### 1. Tasks → Pomodoro Integration

**Cách hoạt động:**
- Mỗi task hiện có nút **"Bắt đầu"** 
- Click nút tự động:
  - Chuyển sang tab Pomodoro
  - Hiển thị tên task trong timer
  - Bắt đầu đếm ngược 25 phút
  - Highlight task đang active

**Code:**
```javascript
function startPomodoroWithTask(taskId, taskTitle) {
    currentActiveTask = allTasks.find(t => t.id === taskId);
    if (currentActiveTask) {
        document.getElementById('taskNameDisplay').textContent = 
            `📚 ${taskTitle}`;
        showTab('pomodoroTab');
        startTimer();
    }
}
```

### 2. Pomodoro → Points/Challenges

**Khi hoàn thành Pomodoro:**
- ✅ +10 điểm
- ✅ +1 vào challenge "Focus Master" (cần 5 sessions)
- ✅ Tăng tiến độ task hiện tại (0.5h)
- ✅ Nếu task hoàn thành: +20 điểm bonus
- ✅ Cập nhật challenge "Task Crusher"
- ✅ Kiểm tra điều kiện nhận badges
- ✅ Refresh leaderboard

**Nguồn điểm:**
| Hành động | Điểm nhận |
|-----------|-----------|
| Hoàn thành Pomodoro | +10 |
| Hoàn thành task | +20 |
| Hoàn thành challenge | +50-150 |

### 3. Challenges System

**Danh sách challenges:**

1. **Focus Master**
   - Target: 5 Pomodoro sessions
   - Reward: 50 điểm
   - Trigger: Mỗi lần `completePomodoro()`

2. **Task Crusher**
   - Target: 10 tasks hoàn thành
   - Reward: 100 điểm
   - Trigger: Khi task đạt 100% progress

3. **Study Streak**
   - Target: 7 ngày liên tiếp
   - Reward: 150 điểm
   - Trigger: Mỗi lần `markScheduleAsCompleted()`

**Auto-update:**
```javascript
function updateChallengeProgress(type, amount) {
    activeChallenges.forEach(challenge => {
        if (challenge.type === type) {
            challenge.current += amount;
            if (challenge.current >= challenge.target && !challenge.completed) {
                challenge.completed = true;
                totalPoints += challenge.points;
                showToast(`Hoàn thành: ${challenge.name}! +${challenge.points} điểm`);
                saveGamificationState();
            }
        }
    });
}
```

### 4. Badges System

**Badges có thể nhận:**

| Badge | Điều kiện | Icon |
|-------|-----------|------|
| Focus Master | 20 Pomodoro sessions | ⭐ |
| Task Crusher | 10 tasks hoàn thành | 💪 |
| Study Streak | 7 ngày liên tiếp học | 🔥 |

**Auto-check:**
```javascript
function checkAndAwardBadges() {
    // Focus Master: 20 pomodoros
    if (todayPomodoroCount >= 20 && !earnedBadges.includes('focus_master')) {
        earnedBadges.push('focus_master');
        showToast('Nhận huy hiệu: Focus Master!', 'success');
        saveGamificationState();
    }
    
    // Task Crusher: 10 completed tasks
    const completedTasks = allTasks.filter(t => t.status === 'completed').length;
    if (completedTasks >= 10 && !earnedBadges.includes('task_crusher')) {
        earnedBadges.push('task_crusher');
        showToast('Nhận huy hiệu: Task Crusher!', 'success');
        saveGamificationState();
    }
}
```

### 5. Schedule → Points

**Khi click "Vào học":**
- ✅ +5 điểm/giờ học
- ✅ Cập nhật challenge "Study Streak"
- ✅ Lưu vào study sessions
- ✅ Refresh leaderboard
- ✅ Mark schedule block hoàn thành (màu xanh)

**Code:**
```javascript
function markScheduleAsCompleted(subject, date) {
    const scheduleItem = acceptedSchedule.find(s => 
        s.subject === subject && s.date === date
    );
    
    const pointsEarned = Math.floor((scheduleItem.suggestedHours || 2) * 5);
    totalPoints += pointsEarned;
    updateChallengeProgress('daily', 1);
    
    saveGamificationState();
    loadLeaderboard();
}
```

### 6. Leaderboard Real-time Updates

**Tính năng:**
- ✅ Hiển thị điểm thực từ biến `totalPoints`
- ✅ Highlight row của người dùng hiện tại (gradient background)
- ✅ Badge "BẠN" để nhận diện
- ✅ Sắp xếp theo điểm cao → thấp
- ✅ Auto refresh sau mọi hành động có điểm

**UI Highlight:**
```javascript
const rowStyle = row.isCurrentUser ? 
    'background: linear-gradient(135deg, rgba(102, 126, 234, 0.1) 0%, rgba(118, 75, 162, 0.1) 100%); font-weight: 600;' 
    : '';
```

## 💾 Persistence System

**LocalStorage Keys:**
- `totalPoints`: Tổng điểm tích lũy
- `todayPomodoroCount`: Số Pomodoro hôm nay
- `activeChallenges`: JSON array các challenges
- `earnedBadges`: JSON array badges đã nhận
- `lastSaveDate`: Ngày lưu cuối (để reset daily counters)

**Auto-save triggers:**
- ✅ Sau mỗi Pomodoro hoàn thành
- ✅ Khi challenge đạt target
- ✅ Khi nhận badge mới
- ✅ Sau khi hoàn thành schedule

**Load on startup:**
```javascript
window.onload = () => {
    loadGamificationState(); // Restore saved data
    // ... rest of initialization
}
```

**Daily reset:**
```javascript
function loadGamificationState() {
    const savedDate = localStorage.getItem('lastSaveDate');
    const today = new Date().toISOString().split('T')[0];
    
    if (savedDate !== today) {
        todayPomodoroCount = 0; // Reset daily counter
        localStorage.setItem('lastSaveDate', today);
    }
    // ... load other data
}
```

## 🎨 UI/UX Enhancements

### Visual Feedback
- **Toast notifications**: Hiện mỗi khi nhận điểm/badge
- **Progress bars**: Challenges có thanh progress động
- **Highlight effects**: Task đang active có border màu
- **Gradient backgrounds**: Current user trong leaderboard

### Color System
- **Primary Gradient**: `#667eea → #764ba2`
- **Success**: `#10b981` (green)
- **Points**: `#fa709a` (pink)
- **Active**: `#667eea` (purple)

## 📊 Data Flow Example

**Scenario: Người dùng hoàn thành 1 Pomodoro session**

```
1. User click "Bắt đầu" trên Task "Học Toán"
   └─► currentActiveTask = {id: 1, title: "Học Toán", ...}
   └─► Tab chuyển sang Pomodoro
   └─► Timer bắt đầu 25:00

2. Timer hết giờ, user click "Hoàn thành"
   └─► totalPoints += 10
   └─► todayPomodoroCount += 1
   └─► currentActiveTask.hoursCompleted += 0.5
   └─► updateChallengeProgress('pomodoro', 1)
       └─► activeChallenges[0].current += 1
       └─► Nếu current >= target:
           └─► totalPoints += 50
           └─► challenge.completed = true
           └─► Toast: "Hoàn thành: Focus Master! +50 điểm"
   └─► checkAndAwardBadges()
       └─► Nếu todayPomodoroCount >= 20:
           └─► earnedBadges.push('focus_master')
           └─► Toast: "Nhận huy hiệu: Focus Master!"
   └─► saveGamificationState()
   └─► loadTasks() // Refresh tasks list
   └─► renderChallenges() // Update challenge bars
   └─► loadLeaderboard() // Update ranking

3. UI updates:
   ✅ Điểm hiển thị mới trên leaderboard
   ✅ Challenge progress bar tăng
   ✅ Task "Học Toán" tiến độ +0.5h
   ✅ Badge mới hiển thị (nếu unlock)
```

## 🚀 Testing Guide

### Test Case 1: Task to Pomodoro Flow
1. Vào tab **Tasks**
2. Click **"Bắt đầu"** trên task bất kỳ
3. ✅ Kiểm tra: Chuyển sang tab Pomodoro
4. ✅ Kiểm tra: Tên task hiển thị trong timer
5. Click **"Hoàn thành"** sau khi timer chạy
6. ✅ Kiểm tra: +10 điểm
7. ✅ Kiểm tra: Challenge "Focus Master" +1
8. ✅ Kiểm tra: Leaderboard cập nhật

### Test Case 2: Schedule to Points
1. Vào tab **Schedule**
2. Tìm block chưa hoàn thành
3. Click **"Vào học"**
4. ✅ Kiểm tra: Block chuyển màu xanh
5. ✅ Kiểm tra: Điểm tăng (5 điểm/giờ)
6. ✅ Kiểm tra: Toast thông báo
7. ✅ Kiểm tra: Leaderboard update

### Test Case 3: Persistence
1. Hoàn thành vài Pomodoro sessions
2. Ghi nhớ tổng điểm hiện tại
3. **Refresh trang** (F5)
4. ✅ Kiểm tra: Điểm vẫn giữ nguyên
5. ✅ Kiểm tra: Challenge progress không mất
6. ✅ Kiểm tra: Badges vẫn còn

### Test Case 4: Badge Unlock
1. Hoàn thành 20 Pomodoro sessions
2. ✅ Kiểm tra: Toast "Nhận huy hiệu: Focus Master!"
3. Vào tab **Badges**
4. ✅ Kiểm tra: Badge "Focus Master" hiển thị với màu sắc

## 🔧 API Endpoints (Backend Integration)

Hiện tại đang dùng localStorage, có thể connect backend:

```javascript
// Example: Save points to backend
async function syncPointsToBackend() {
    try {
        const response = await fetch('/wellness-app/api/academic/points', {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({
                userId: 1,
                totalPoints,
                challenges: activeChallenges,
                badges: earnedBadges
            })
        });
        const data = await response.json();
        console.log('Points synced:', data);
    } catch (error) {
        console.error('Sync failed:', error);
    }
}
```

## 📝 Notes

- **Daily reset**: `todayPomodoroCount` reset về 0 mỗi ngày mới
- **Points persistence**: `totalPoints` tích lũy vĩnh viễn
- **Challenge reset**: Sau khi hoàn thành, có thể reset để chơi lại
- **Badge permanence**: Badges một khi unlock thì không mất

## 🎯 Future Enhancements

- [ ] Thêm weekly challenges (reset mỗi tuần)
- [ ] Leaderboard global (so với user khác)
- [ ] Rewards shop (đổi điểm lấy items)
- [ ] Achievement system (unlock milestones)
- [ ] Social features (chia sẻ badges)
- [ ] Push notifications (nhắc Pomodoro break)

---

**Last Updated**: 2025-11-15  
**Author**: Hackathon DEV4 Team  
**Version**: 2.0.0
