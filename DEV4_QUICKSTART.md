# 🎯 Hướng Dẫn Sử Dụng DEV4 - Academic Wellness & Gamification

## 🚀 Truy Cập Nhanh

### **Từ Homepage:**
1. Mở: `http://localhost:8080/wellness-app/index.html`
2. Nhập User ID: `1` (hoặc bất kỳ ID nào)
3. Click button **"🎯 Bắt đầu học tập"** trong card DEV4

### **Trực Tiếp:**
`http://localhost:8080/wellness-app/academic-gamification.html?userId=1`

---

## 📋 Hướng Dẫn Từng Tính Năng

### **1️⃣ Công Việc Học Tập (Tasks)**
- **Xem danh sách**: Tất cả công việc chưa làm
- **Tạo mới**: 
  - Nhập tên công việc
  - Chọn ngày hạn chót
  - Chọn mức độ ưu tiên (Cao/Trung bình/Thấp)
  - Nhập giờ ước tính
  - Click "✅ Thêm công việc"
- **Thống kê**:
  - 📊 Số công việc chưa làm
  - ⏰ Tổng giờ cần thiết
  - 🎯 Điểm toàn bộ
  - 📍 Phiên Pomodoro hôm nay

### **2️⃣ Pomodoro Timer (⏱️)**
- **Bắt đầu**: Click "▶️ Bắt đầu"
- **Tạm dừng**: Click "⏸️ Tạm dừng"
- **Thiết lập lại**: Click "🔄 Thiết lập lại"
- **Hoàn thành**: 
  - Khi hết 25 phút, app thông báo
  - Click "✅ Hoàn thành phiên làm việc" để ghi nhận
  - **Nhận +10 điểm**

**💡 Mẹo**: 
- Mỗi phiên Pomodoro = 25 phút
- Hoàn thành phiên = +10 điểm
- Tích lũy điểm để lên level

### **3️⃣ Thử Thách (🏆 Challenges)**
- **Danh sách thử thách**:
  - 🎯 Pomodoro Weekly (10 phiên)
  - 💪 Streak Master (5 ngày check-in)
  - 🌟 Task Warrior (5 công việc)
- **Tham gia**: Click "Bắt đầu thử thách"
- **Theo dõi tiến độ**: Thanh tiến bộ trực quan
- **Hoàn thành**: Nhận phần thưởng + huy hiệu

### **4️⃣ Bảng Xếp Hạng (🥇 Leaderboard)**
- **Xem top 5 người dùng**
- **Hạng**: 🥇 (1st), 🥈 (2nd), 🥉 (3rd)
- **Điểm**: Tổng điểm từ Pomodoro + Thử thách + Wellness

### **5️⃣ Huy Hiệu (🎖️ Badges)**
- **Huy hiệu có thể kiếm:**
  - 🌟 **Focus Master**: Hoàn thành 20 Pomodoro
  - 💯 **Stress-Free Week**: Wellness score > 70 cả tuần
  - 🔥 **Streak Master**: Check-in 7 ngày liên tiếp
  - 🏆 **Top Performer**: Top 10 bảng xếp hạng
- **Hoàn thành điều kiện → Tự động nhận huy hiệu**

---

## 🎮 Hệ Thống Điểm

| Hành động | Điểm | Ghi chú |
|-----------|------|--------|
| Hoàn thành Pomodoro | +10 | Sau mỗi phiên 25 phút |
| Hoàn thành Thử thách | +50 | Khi đạt mục tiêu |
| Bonus Wellness | +5 | Khi mood & wellness tốt |
| Streak Bonus | +2 | Mỗi ngày check-in liên tiếp |

**Level System:**
```
Level 1: 0-99 điểm
Level 2: 100-199 điểm  
Level 3: 200-299 điểm
... (100 điểm/level)
```

---

## 📱 API Endpoints (Nếu muốn test trực tiếp)

### **1. Tạo công việc:**
```bash
curl -X POST http://localhost:8080/wellness-app/api/academic/tasks \
  -H "Content-Type: application/json" \
  -d '{
    "userId": 1,
    "title": "Hoàn thành bài tập",
    "description": "Bài tập lập trình Java",
    "deadline": "2025-11-20",
    "priority": "high",
    "estimatedHours": 3
  }'
```

### **2. Lấy danh sách công việc:**
```bash
curl http://localhost:8080/wellness-app/api/academic/tasks/1
```

### **3. Tính khối lượng công việc:**
```bash
curl http://localhost:8080/wellness-app/api/academic/workload/1
```

**Response:**
```json
{
  "userId": 1,
  "totalHoursNeeded": 8,
  "pendingTasksCount": 3,
  "isOverloaded": false,
  "stressLevel": "MEDIUM"
}
```

### **4. Bắt đầu Pomodoro:**
```bash
curl -X POST http://localhost:8080/wellness-app/api/academic/pomodoro/start \
  -H "Content-Type: application/json" \
  -d '{"userId": 1, "taskId": 1, "durationMinutes": 25}'
```

### **5. Hoàn thành Pomodoro:**
```bash
curl -X POST http://localhost:8080/wellness-app/api/academic/pomodoro/complete \
  -H "Content-Type: application/json" \
  -d '{"pomodoroId": 1, "breaksTaken": 2}'
```

**Response:**
```json
{
  "sessionId": 1,
  "pointsAwarded": 10,
  "completedAt": "2025-11-15T14:35:00"
}
```

### **6. Lấy điểm người dùng:**
```bash
curl http://localhost:8080/wellness-app/api/academic/points/1
```

**Response:**
```json
{
  "userId": 1,
  "totalPoints": 120,
  "pomodorosToday": 3,
  "level": 2
}
```

---

## ⚡ Troubleshooting

### **Vấn đề: Page không load**
- ✅ Kiểm tra Tomcat đang chạy: `curl http://localhost:8080`
- ✅ Xem logs: `tail -f tomcat/logs/catalina.out`

### **Vấn đề: API 404 Not Found**
- ✅ Kiểm tra URL: `/wellness-app/api/academic/...`
- ✅ Kiểm tra servlet mapping trong `web.xml`

### **Vấn đề: Dữ liệu không lưu**
- ✅ Kiểm tra database connection (Supabase)
- ✅ Kiểm tra migration SQL đã run
- ✅ Xem console logs lỗi database

### **Vấn đề: Timer không chạy**
- ✅ Check browser console: F12 → Console tab
- ✅ Kiểm tra JavaScript errors
- ✅ Try reload trang: Ctrl+R

---

## 📚 Tài Liệu Thêm

- **DB Schema**: `docs/DEV4_IMPLEMENTATION.md` - Chi tiết bảng database
- **API Docs**: Comments trong `AcademicServlet.java`
- **Code**: `src/main/java/com/wellness/academic/`

---

## 🎯 Tips để Tận Dụng Tối Đa

1. **Tạo 5-10 công việc** để thấy workload analytics
2. **Chạy 3-5 Pomodoro** để kiếm điểm và lên level
3. **Kiểm tra bảng xếp hạng** xem mình xếp mấy
4. **Hoàn thành thử thách** để nhận huy hiệu
5. **Duy trì streak** bằng check-in hàng ngày (DEV1)

---

**Happy Learning! 🚀**

*DEV4 Team | Hackathon Project | November 2025*
