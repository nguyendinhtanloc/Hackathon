# 🚀 HƯỚNG DẪN SETUP & CHẠY THỬ - DEV 1 MODULE

## Bước 1: Cấu hình Database

### 1.1. Tạo Supabase Project
1. Đăng nhập [Supabase](https://supabase.com)
2. Tạo project mới
3. Vào **SQL Editor**
4. Copy nội dung file `supabase/migrations/001_dev1_wellness_tables.sql`
5. Click **Run** để tạo tables

### 1.2. Lấy Database Credentials
```
Settings → Database → Connection Info:
- Host: db.YOUR_PROJECT.supabase.co
- Port: 5432
- Database: postgres
- User: postgres
- Password: [your-password]
```

### 1.3. Update persistence.xml
File: `src/main/resources/persistence.xml`

```xml
<property name="javax.persistence.jdbc.url" 
          value="jdbc:postgresql://db.YOUR_PROJECT.supabase.co:5432/postgres"/>
<property name="javax.persistence.jdbc.password" 
          value="YOUR_PASSWORD"/>
```

---

## Bước 2: Build Project

```bash
# Mở terminal tại thư mục dự án
cd D:\hackathon

# Build với Maven
mvn clean install

# Nếu thành công, sẽ tạo file: target/wellness-app.war
```

---

## Bước 3: Deploy lên Tomcat

### Option A: Copy WAR file
```bash
# Copy WAR vào Tomcat webapps
copy target\wellness-app.war C:\apache-tomcat-9.0.XX\webapps\

# Start Tomcat
cd C:\apache-tomcat-9.0.XX\bin
startup.bat
```

### Option B: Maven Tomcat Plugin
```bash
mvn tomcat7:run
```

---

## Bước 4: Test Backend API

### 4.1. Mở trình duyệt test tool
```
http://localhost:8080/wellness-app/test-api.html
```

### 4.2. Test từng API:
1. Click **"POST /api/wellness/checkin"** → Kiểm tra response có `success: true`
2. Click **"GET /api/wellness/dashboard/1"** → Xem dashboard data
3. Click **"Create 7 Days of Data"** → Tạo data mẫu
4. Click **"GET /api/wellness/mood-trend/1"** → Xem trend chart data

### 4.3. Test với Postman (Optional)
```bash
# Check-in
POST http://localhost:8080/wellness-app/api/wellness/checkin
Content-Type: application/json

{
  "userId": 1,
  "heartRate": 72,
  "hrv": 65,
  "stressScore": 35,
  "emotion": "happy",
  "emoji": "😊",
  "note": "Test check-in"
}

# Dashboard
GET http://localhost:8080/wellness-app/api/wellness/dashboard/1
```

---

## Bước 5: Test Heart Rate Measurement (Mobile)

### 5.1. Trên Desktop (Test camera laptop)
```
http://localhost:8080/wellness-app/heart-rate-measurement.html
```

### 5.2. Trên Mobile (Recommend)
```bash
# 1. Tìm IP máy tính
ipconfig
# Ví dụ: 192.168.1.100

# 2. Đảm bảo mobile cùng WiFi với máy tính

# 3. Truy cập từ mobile
http://192.168.1.100:8080/wellness-app/heart-rate-measurement.html

# 4. Cho phép truy cập camera
# 5. Đặt ngón tay lên camera sau
# 6. Nhấn "Bắt Đầu Đo"
# 7. Giữ yên 30 giây
```

### 5.3. Kiểm tra data đã lưu
```
# Refresh dashboard
http://192.168.1.100:8080/wellness-app/test-api.html

# Click "GET /api/wellness/dashboard/1"
# Kiểm tra latestCheckin có heartRate, hrv, stressScore
```

---

## Bước 6: Verify Database

### Kiểm tra data trong Supabase
```sql
-- Vào Supabase SQL Editor

-- Check mood check-ins
SELECT * FROM mood_checkins ORDER BY created_at DESC LIMIT 10;

-- Check wellness scores
SELECT * FROM wellness_scores ORDER BY date DESC LIMIT 10;

-- Check user stats
SELECT 
    user_id,
    COUNT(*) as total_checkins,
    AVG(wellness_score) as avg_wellness,
    MAX(checkin_date) as last_checkin
FROM mood_checkins
GROUP BY user_id;
```

---

## 🔍 Troubleshooting

### Lỗi 1: "Cannot connect to database"
**Giải pháp:**
```bash
# Kiểm tra credentials trong persistence.xml
# Test connection với psql
psql -h db.YOUR_PROJECT.supabase.co -U postgres -d postgres
```

### Lỗi 2: "Camera not accessible"
**Giải pháp:**
- Trên Mobile: Cho phép browser truy cập camera
- Desktop: Chỉ hoạt động trên localhost hoặc HTTPS
- iOS: Safari hỗ trợ tốt hơn Chrome

### Lỗi 3: "Torch not supported"
**Giải pháp:**
- Đèn flash chỉ hỗ trợ một số thiết bị
- Sử dụng ánh sáng mạnh thay thế
- Test trên Android thường hỗ trợ tốt hơn iOS

### Lỗi 4: "ClassNotFoundException: jakarta.persistence"
**Giải pháp:**
```bash
# Rebuild project
mvn clean install -U

# Kiểm tra pom.xml có dependency jakarta.persistence
```

### Lỗi 5: Heart rate không chính xác
**Giải pháp:**
- Đảm bảo ngón tay **hoàn toàn phủ kín** camera
- Giữ yên, không cử động
- Đo trong môi trường yên tĩnh
- Thử đo lại 2-3 lần lấy trung bình

---

## 📊 Expected Results

### Check-in Response:
```json
{
  "success": true,
  "data": {
    "wellnessScore": 78,
    "consecutiveDays": 5,
    "report": {
      "wellnessLevel": "Tốt",
      "heartRateCategory": "NORMAL",
      "hrvCategory": "GOOD",
      "stressLevel": "Trung bình",
      "recommendedActivities": [
        "Thiền 10-15 phút mỗi ngày",
        "Tập yoga hoặc stretching"
      ],
      "isCritical": false
    }
  }
}
```

### Dashboard Response:
```json
{
  "success": true,
  "data": {
    "consecutiveDays": 5,
    "avgWellnessScore": 75,
    "totalCheckins": 7,
    "latestCheckin": {...},
    "weeklyCheckins": [...]
  }
}
```

---

## 🎯 Success Checklist

- [ ] Database tables created successfully
- [ ] Backend APIs returning 200 OK
- [ ] Check-in saves to database
- [ ] Dashboard shows correct data
- [ ] Mood trend chart data available
- [ ] Camera access works on mobile
- [ ] Heart rate detection working
- [ ] Wellness score calculated correctly
- [ ] Stress level categorized correctly
- [ ] Recommendations displayed

---

## 🚀 Next Steps

1. **Frontend Dashboard**: Tạo UI dashboard với charts
2. **Mobile App**: Build React Native / Flutter app
3. **Notifications**: Nhắc nhở check-in hàng ngày
4. **Analytics**: Xuất báo cáo tuần/tháng
5. **Integration**: Kết nối với DEV 2 (AI) và DEV 4 (Gamification)

---

## 📞 Support

Nếu gặp vấn đề:
1. Check console logs (F12 → Console)
2. Check Tomcat logs (`logs/catalina.out`)
3. Test API với curl/Postman
4. Verify database connection
5. Ask team for help!

**✅ DONE! Module DEV 1 sẵn sàng!** 🎉
