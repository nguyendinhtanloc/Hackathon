# 🎯 DEV4 - FLOWCHART & DIAGRAMS

## 📊 OVERALL ARCHITECTURE

```
┌─────────────────────────────────────────────────────────────────┐
│                         FRONTEND                                 │
│                  academic-gamification.html                      │
├─────────────────────────────────────────────────────────────────┤
│                                                                  │
│  ┌──────────┐  ┌──────────┐  ┌──────────┐  ┌──────────┐       │
│  │  Tasks   │  │ Pomodoro │  │ Schedule │  │   Game   │       │
│  │   Tab    │  │   Tab    │  │   Tab    │  │   Tabs   │       │
│  └────┬─────┘  └────┬─────┘  └────┬─────┘  └────┬─────┘       │
│       │             │              │             │              │
└───────┼─────────────┼──────────────┼─────────────┼──────────────┘
        │             │              │             │
        ▼             ▼              ▼             ▼
┌─────────────────────────────────────────────────────────────────┐
│                      REST API LAYER                              │
│                   /api/academic/*                                │
├─────────────────────────────────────────────────────────────────┤
│                                                                  │
│  AcademicServlet.java                                           │
│  ├── POST   /tasks                                              │
│  ├── GET    /tasks/{userId}                                     │
│  ├── GET    /workload/{userId}                                  │
│  ├── POST   /pomodoro/start                                     │
│  ├── POST   /pomodoro/complete                                  │
│  ├── GET    /points/{userId}                                    │
│  ├── POST   /exam-schedule/upload                               │
│  ├── GET    /exam-schedule/{userId}                             │
│  └── GET    /suggested-schedule/{userId}                        │
│                                                                  │
└───────────────────────┬─────────────────────────────────────────┘
                        │
        ┌───────────────┼───────────────┐
        ▼               ▼               ▼
┌──────────────┐ ┌──────────────┐ ┌──────────────┐
│   Services   │ │  In-Memory   │ │   Database   │
│              │ │    Storage   │ │  (Supabase)  │
│ UserTask     │ │              │ │              │
│ Service      │ │ examSchedules│ │ user_tasks   │
│              │ │              │ │ pomodoro_    │
│ Pomodoro     │ │ busySlots    │ │  sessions    │
│ Service      │ │              │ │ user_points  │
│              │ │ (Fallback)   │ │ challenges   │
│ Challenge    │ │              │ │ badges       │
│ Service      │ │              │ │ ...          │
└──────────────┘ └──────────────┘ └──────────────┘
```

---

## 🔄 SCHEDULE PLANNER FLOW

```
┌─────────────────────────────────────────────────────────────────┐
│                    STEP 1: DATA INPUT                            │
└─────────────────────────────────────────────────────────────────┘

    User uploads Excel file (lịch thi)
            │
            ▼
    ┌──────────────────────┐
    │  File: exam.xlsx     │
    ├──────────────────────┤
    │ Math    │ 2025-11-20 │
    │ Physics │ 2025-11-25 │
    │ Chem    │ 2025-11-30 │
    └──────────────────────┘
            │
            ▼
    POST /api/academic/exam-schedule/upload
            │
            ▼
    ┌──────────────────────────────────────┐
    │ Backend parses Excel using Apache POI│
    │ Row → ExamEntry { subject, date }    │
    └──────────────────────────────────────┘
            │
            ▼
    Store in: inMemoryExamSchedules
    Map<userId, List<ExamEntry>>
    
    User sets subject metadata
            │
            ▼
    ┌──────────────────────────────────────┐
    │ Frontend: subjectMeta object         │
    │ {                                    │
    │   "Math": {                          │
    │     chapters: 5,                     │
    │     hoursPerChapter: 2               │
    │   }                                  │
    │ }                                    │
    └──────────────────────────────────────┘
    
    User adds busy slots
            │
            ▼
    ┌──────────────────────────────────────┐
    │ BusySlot:                            │
    │ - startDate: 2025-11-17              │
    │ - endDate: 2025-11-17                │
    │ - startTime: 09:00                   │
    │ - endTime: 17:00                     │
    └──────────────────────────────────────┘
            │
            ▼
    Store in: inMemoryBusySlots

┌─────────────────────────────────────────────────────────────────┐
│                STEP 2: GENERATE SUGGESTIONS                      │
└─────────────────────────────────────────────────────────────────┘

    User clicks "Tạo gợi ý lịch học"
            │
            ▼
    GET /api/academic/suggested-schedule/{userId}
            │
            ▼
    ┌──────────────────────────────────────┐
    │ Backend Algorithm:                   │
    │                                      │
    │ FOR each exam in examSchedules:      │
    │   totalHours = 6h                    │
    │   blockSize = 2h                     │
    │   currentDate = today                │
    │   examDate = exam.date               │
    │                                      │
    │   WHILE totalHours > 0 AND           │
    │         currentDate < examDate:      │
    │                                      │
    │     IF currentDate NOT in busySlots: │
    │       CREATE suggestion:             │
    │         subject = exam.subject       │
    │         date = currentDate           │
    │         startTime = 18:00            │
    │         endTime = 20:00              │
    │         hours = 2                    │
    │       totalHours -= 2                │
    │                                      │
    │     currentDate += 1 day             │
    └──────────────────────────────────────┘
            │
            ▼
    Return JSON:
    [
      {subject: "Math", date: "2025-11-16", 
       startTime: "18:00", endTime: "20:00", hours: 2},
      {subject: "Math", date: "2025-11-17", 
       startTime: "18:00", endTime: "20:00", hours: 2},
      ...
    ]

┌─────────────────────────────────────────────────────────────────┐
│               STEP 3: DISPLAY SUGGESTIONS                        │
└─────────────────────────────────────────────────────────────────┘

    Frontend receives data
            │
            ▼
    Transform to internal format
    {
      subject: "Math",
      date: "2025-11-16",
      time: "18:00-20:00",
      suggestedHours: 2,
      suggestedSlots: [{start: "18:00", end: "20:00"}]
    }
            │
            ▼
    renderSuggestions() → Display table
    
    ┌────────────────────────────────────────────────────┐
    │ Môn   │ Ngày     │ Giờ  │ Phiên │ Hạn  │ Thao tác │
    ├────────────────────────────────────────────────────┤
    │ Math  │15/11/25  │ 2h   │  1    │18:00 │ [✓] [✕]  │
    │ Math  │16/11/25  │ 2h   │  1    │18:00 │ [✓] [✕]  │
    │ Phys  │17/11/25  │ 2h   │  1    │18:00 │ [✓] [✕]  │
    └────────────────────────────────────────────────────┘
    
┌─────────────────────────────────────────────────────────────────┐
│              STEP 4: ACCEPT SUGGESTION                           │
└─────────────────────────────────────────────────────────────────┘

    User clicks "✓ Chấp nhận"
            │
            ▼
    acceptSuggestion(idx)
            │
            ├─→ Get suggestion from allSuggestions[idx]
            │
            ├─→ Check duplicate (subject + date)
            │
            ├─→ Add to acceptedSchedule[]
            │   acceptedSchedule.push({
            │     ...suggestion,
            │     acceptedAt: new Date().toISOString()
            │   })
            │
            ├─→ Save to localStorage
            │   localStorage.setItem(
            │     `acceptedSchedule_${userId}`,
            │     JSON.stringify(acceptedSchedule)
            │   )
            │
            ├─→ Update button UI
            │   - Disable "Chấp nhận"
            │   - Show "✓ Đã thêm"
            │   - Green border
            │
            ├─→ Show toast notification
            │
            ├─→ renderTimetable()
            │
            └─→ renderProgress()

┌─────────────────────────────────────────────────────────────────┐
│           STEP 5: DISPLAY ACCEPTED SCHEDULE                      │
└─────────────────────────────────────────────────────────────────┘

    renderTimetable()
            │
            ├─→ Check if acceptedSchedule is empty
            │   IF empty → hide cards
            │
            ├─→ Group by date
            │   byDate = {
            │     "2025-11-16": [Math, Physics],
            │     "2025-11-17": [Chemistry]
            │   }
            │
            ├─→ Create grid layout
            │   Grid: repeat(auto-fill, minmax(250px, 1fr))
            │
            └─→ For each date:
                └─→ Create day card
                    │
                    ├─→ Header: Thứ 2, 15/11/2025
                    │
                    └─→ For each subject:
                        └─→ Create block
                            │
                            ├─→ Color based on subject hash
                            │   colors[hash % 5]
                            │
                            ├─→ Content:
                            │   📘 Math
                            │   🕐 18:00-20:00
                            │   📚 2h
                            │
                            └─→ Click handler → confirm delete
    
    Result:
    ┌──────────────┐  ┌──────────────┐  ┌──────────────┐
    │  Thứ 2       │  │  Thứ 3       │  │  Thứ 4       │
    │  15/11/2025  │  │  16/11/2025  │  │  17/11/2025  │
    ├──────────────┤  ├──────────────┤  ├──────────────┤
    │ 📘 Math      │  │ 📕 Physics   │  │ 📗 Chemistry │
    │ 🕐 18:00-20:00│  │ 🕐 19:00-21:00│  │ 🕐 20:00-22:00│
    │ 📚 2h        │  │ 📚 2h        │  │ 📚 2h        │
    └──────────────┘  └──────────────┘  └──────────────┘

┌─────────────────────────────────────────────────────────────────┐
│              STEP 6: TRACK PROGRESS                              │
└─────────────────────────────────────────────────────────────────┘

    renderProgress(subjects, matrix)
            │
            ├─→ buildMatrix()
            │   │
            │   ├─→ From acceptedSchedule[]
            │   │
            │   └─→ Create matrix[subject][date] = hours
            │
            ├─→ For each subject:
            │   │
            │   ├─→ needed = chapters × hoursPerChapter
            │   │
            │   ├─→ scheduled = sum(matrix[subject][*])
            │   │
            │   ├─→ studied = sum(studySessions[subject])
            │   │
            │   └─→ progress % = (scheduled / needed) × 100
            │
            └─→ Render table with progress bars
    
    Result:
    ┌─────────────────────────────────────────────────┐
    │ Môn │ Dự định│ Đã học│ Cần học│ Tiến độ       │
    ├─────────────────────────────────────────────────┤
    │Math │ 6h     │ 2h    │ 10h    │[████░░] 60%   │
    │Phys │ 4h     │ 1h    │  8h    │[███░░░] 50%   │
    │Chem │ 2h     │ 0h    │  6h    │[██░░░░] 33%   │
    └─────────────────────────────────────────────────┘
```

---

## ⏱️ POMODORO TIMER FLOW

```
┌─────────────────────────────────────────────────────────────────┐
│                  POMODORO WORKFLOW                               │
└─────────────────────────────────────────────────────────────────┘

    User clicks "▶️ Bắt đầu"
            │
            ▼
    startPomodoro()
            │
            ├─→ timeRemaining = 25 * 60 (1500 seconds)
            │
            ├─→ isRunning = true
            │
            ├─→ timerInterval = setInterval(() => {
            │     timeRemaining--;
            │     updateTimerDisplay();
            │     
            │     if (timeRemaining === 0) {
            │       clearInterval(timerInterval);
            │       playSound(); // optional
            │       showToast("Pomodoro hoàn thành!");
            │     }
            │   }, 1000)
            │
            └─→ Update UI: disable start, enable pause

    User clicks "⏸️ Tạm dừng"
            │
            ▼
    pausePomodoro()
            │
            ├─→ clearInterval(timerInterval)
            │
            ├─→ isRunning = false
            │
            └─→ Update UI: enable start, disable pause

    User clicks "🔄 Thiết lập lại"
            │
            ▼
    resetPomodoro()
            │
            ├─→ clearInterval(timerInterval)
            │
            ├─→ timeRemaining = 25 * 60
            │
            ├─→ isRunning = false
            │
            └─→ updateTimerDisplay()

    Timer reaches 0:00
            │
            ▼
    User clicks "✅ Hoàn thành phiên làm việc"
            │
            ▼
    completePomodoro()
            │
            ├─→ POST /api/academic/pomodoro/start
            │   Body: {userId, taskId: null, durationMinutes: 25}
            │   Response: {success: true, data: {id: 123}}
            │
            ├─→ POST /api/academic/pomodoro/complete
            │   Body: {pomodoroId: 123, breaksTaken: 0}
            │   Response: {
            │     success: true,
            │     data: {
            │       sessionId: 123,
            │       pointsAwarded: 10,
            │       completedAt: "2025-11-15T14:25:00"
            │     }
            │   }
            │
            ├─→ Update points display
            │   totalPoints += 10
            │
            ├─→ Update pomodoros today
            │   pomodorosToday++
            │
            ├─→ Show toast: "+10 điểm! 🎉"
            │
            └─→ Reset timer for next session
```

---

## 🎮 GAMIFICATION DATA FLOW

```
┌─────────────────────────────────────────────────────────────────┐
│                  POINTS ACCUMULATION                             │
└─────────────────────────────────────────────────────────────────┘

    User Action
         │
         ├──→ Complete Pomodoro ──→ +10 pts ─┐
         │                                    │
         ├──→ Complete Challenge ─→ +50 pts ─┤
         │                                    │
         ├──→ Daily Check-in ─────→ +10 pts ─┤
         │        (from DEV1)                 │
         │                                    │
         └──→ Wellness Bonus ─────→ +5 pts ──┘
                                              │
                                              ▼
                              ┌───────────────────────────┐
                              │  Update user_points table │
                              │  totalPoints += points    │
                              └───────────────────────────┘
                                              │
                                              ▼
                              ┌───────────────────────────┐
                              │  Calculate new level      │
                              │  level = floor(pts/100)+1 │
                              └───────────────────────────┘
                                              │
                                              ▼
                              ┌───────────────────────────┐
                              │  Check badge criteria     │
                              │  Award if met             │
                              └───────────────────────────┘
                                              │
                                              ▼
                              ┌───────────────────────────┐
                              │  Update leaderboard       │
                              │  Sort by totalPoints DESC │
                              └───────────────────────────┘

┌─────────────────────────────────────────────────────────────────┐
│                    BADGE AWARDING                                │
└─────────────────────────────────────────────────────────────────┘

    Trigger event (Pomodoro complete, check-in, etc.)
                │
                ▼
    Check all badge criteria
                │
                ├──→ Focus Master
                │    IF pomodoroCount >= 20
                │    THEN award badge
                │
                ├──→ Streak Master
                │    IF consecutiveDays >= 7
                │    THEN award badge
                │
                ├──→ Stress-Free Week
                │    IF avgWellness > 70 AND days >= 7
                │    THEN award badge
                │
                └──→ Top Performer
                     IF rank <= 10
                     THEN award badge
                
                ▼
    IF badge earned:
        ├─→ Insert into user_badges
        ├─→ Show toast notification
        └─→ Update badges display
```

---

## 🏗️ STATE MANAGEMENT

```
┌─────────────────────────────────────────────────────────────────┐
│                    FRONTEND STATE                                │
└─────────────────────────────────────────────────────────────────┘

    Global Variables:
    
    currentUserId: 1
         │
         ├──→ Used in all API calls
         └──→ From URL param or default

    allTasks: []
         │
         ├──→ Loaded from: GET /api/academic/tasks/{userId}
         ├──→ Updated on: create task, delete task
         └──→ Pagination: currentTaskPage, TASKS_PER_PAGE

    acceptedSchedule: []
         │
         ├──→ Loaded from: localStorage
         ├──→ Updated on: accept/reject suggestion
         ├──→ Saved to: localStorage (persistent)
         └──→ Used by: renderTimetable(), renderProgress()

    studySessions: {}
         │
         ├──→ Structure: {subject: [{date, hours}, ...]}
         ├──→ Loaded from: localStorage
         ├──→ Updated on: addStudySession()
         └──→ Used by: renderProgress()

    subjectMeta: {}
         │
         ├──→ Structure: {subject: {chapters, hoursPerChapter}}
         ├──→ Updated on: applySubjectEdits()
         └──→ Used by: calculate needed hours

    allSuggestions: []
         │
         ├──→ Loaded from: GET /api/academic/suggested-schedule
         ├──→ Pagination: currentSuggestionsPage
         └──→ Used by: accept/reject handlers

┌─────────────────────────────────────────────────────────────────┐
│                PERSISTENCE STRATEGY                              │
└─────────────────────────────────────────────────────────────────┘

    LocalStorage (Client-side):
    ├─→ acceptedSchedule_{userId}
    ├─→ studySessions_{userId}
    └─→ tasks_{userId} (fallback)

    Database (Server-side):
    ├─→ user_tasks
    ├─→ pomodoro_sessions
    ├─→ user_points
    ├─→ user_challenges
    ├─→ user_badges
    └─→ leaderboard_entries

    In-Memory (Server-side):
    ├─→ inMemoryExamSchedules
    ├─→ inMemoryBusySlots
    └─→ inMemoryTasks (fallback)
```

---

**🎯 Use these diagrams to understand the complete DEV4 flow!**
