# 🚀 QUICK START GUIDE

## Bước 1: Clone Repository

```bash
git clone <your-repo-url>
cd hackathon
```

## Bước 2: Cấu hình Environment

```bash
# Copy file .env.example thành .env
cp .env.example .env

# Chỉnh sửa .env với thông tin Supabase của bạn
notepad .env  # Windows
```

## Bước 3: Setup Supabase

1. Đăng ký tài khoản tại [Supabase](https://supabase.com)
2. Tạo project mới
3. Vào **SQL Editor**, copy & paste từng file migration:
   - `supabase/migrations/001_dev1_wellness_tables.sql`
   - `supabase/migrations/002_dev2_ai_content_tables.sql`
   - `supabase/migrations/003_dev3_community_tables.sql`
   - `supabase/migrations/004_dev4_academic_game_tables.sql`
4. Click **Run** để tạo tables

## Bước 4: Cập nhật Database Config

Mở file `src/main/resources/persistence.xml` và cập nhật:

```xml
<property name="javax.persistence.jdbc.url" 
          value="jdbc:postgresql://db.YOUR_PROJECT.supabase.co:5432/postgres"/>
<property name="javax.persistence.jdbc.password" 
          value="YOUR_PASSWORD"/>
```

## Bước 5: Build Project

```bash
mvn clean install
```

## Bước 6: Deploy lên Tomcat

### Option A: Copy WAR file

```bash
mvn package
cp target/wellness-app.war C:/apache-tomcat-9.0.XX/webapps/
```

### Option B: Run với Maven Plugin

```bash
mvn tomcat7:run
```

## Bước 7: Test API

Mở Postman hoặc curl:

```bash
# Test Wellness Check-in
curl -X POST http://localhost:8080/wellness-app/api/wellness/checkin \
  -H "Content-Type: application/json" \
  -d '{
    "userId": 1,
    "emotion": "happy",
    "emoji": "😊",
    "note": "Hôm nay vui vẻ!"
  }'

# Test Dashboard
curl http://localhost:8080/wellness-app/api/wellness/dashboard/1
```

## Bước 8: Setup Git Branches

```bash
# Mỗi dev tạo branch riêng
git checkout -b dev1-wellness    # DEV 1
git checkout -b dev2-ai          # DEV 2
git checkout -b dev3-community   # DEV 3
git checkout -b dev4-academic    # DEV 4
```

---

## 🔍 Troubleshooting

### Lỗi: "Cannot connect to database"
- Kiểm tra `persistence.xml` có đúng credentials
- Kiểm tra Supabase project có đang chạy

### Lỗi: "ClassNotFoundException"
- Chạy `mvn clean install` lại
- Kiểm tra `pom.xml` có đầy đủ dependencies

### Lỗi: "Port 8080 already in use"
- Đổi port trong Tomcat `server.xml`
- Hoặc kill process đang dùng port 8080

---

## 📚 Tài Liệu Tham Khảo

- [JPA Documentation](https://docs.oracle.com/javaee/7/tutorial/persistence-intro.htm)
- [Supabase Docs](https://supabase.com/docs)
- [Servlet Tutorial](https://www.javatpoint.com/servlet-tutorial)

---

## 👥 Phân Công Module

| Developer | Module | Package |
|-----------|--------|---------|
| DEV 1 | Wellness Dashboard | `com.wellness.wellness` |
| DEV 2 | AI & Content | `com.wellness.ai` |
| DEV 3 | Community | `com.wellness.community` |
| DEV 4 | Academic & Game | `com.wellness.academic` |

Xem chi tiết task trong `docs/DEV*_TASKS.md`

---

**Good luck! 🚀**
