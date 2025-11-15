# AWS Rekognition Setup Guide

## 🎯 Tại sao dùng AWS Rekognition?

- ✅ **Độ chính xác cao** - 95%+ accuracy
- ✅ **Nhiều cảm xúc hơn** - Phát hiện 8 cảm xúc: HAPPY, SAD, ANGRY, CONFUSED, DISGUSTED, SURPRISED, CALM, FEAR
- ✅ **Phát hiện nhanh** - < 1 giây
- ✅ **Không cần model training** - AWS đã train sẵn
- ✅ **Confidence score** - Độ tin cậy cho từng cảm xúc

## 📋 Hướng dẫn Setup

### Bước 1: Tạo AWS Account
1. Truy cập https://aws.amazon.com/
2. Click "Create an AWS Account"
3. Điền thông tin (cần thẻ tín dụng nhưng free tier miễn phí 12 tháng)

### Bước 2: Tạo IAM User với quyền Rekognition
1. Đăng nhập AWS Console: https://console.aws.amazon.com/
2. Tìm kiếm "IAM" trong thanh search
3. Click **Users** → **Create user**
4. Nhập tên user (ví dụ: `wellness-app-rekognition`)
5. Click **Next**
6. Chọn **Attach policies directly**
7. Tìm và chọn policy: `AmazonRekognitionFullAccess`
8. Click **Next** → **Create user**

### Bước 3: Tạo Access Key
1. Click vào user vừa tạo
2. Chọn tab **Security credentials**
3. Scroll xuống **Access keys** → Click **Create access key**
4. Chọn **Application running outside AWS**
5. Click **Next** → **Create access key**
6. **QUAN TRỌNG**: Lưu lại cả 2 thông tin:
   - `Access key ID` (ví dụ: AKIAIOSFODNN7EXAMPLE)
   - `Secret access key` (ví dụ: wJalrXUtnFEMI/K7MDENG/bPxRfiCYEXAMPLEKEY)
7. Click **Download .csv file** để backup

### Bước 4: Cấu hình trong Project

**Cách đơn giản - Dùng properties file (Khuyến nghị):**

1. Mở file `src/main/resources/aws.properties`
2. Thay đổi:
```properties
aws.access.key.id=AKIAIOSFODNN7EXAMPLE
aws.secret.access.key=wJalrXUtnFEMI/K7MDENG/bPxRfiCYEXAMPLEKEY
aws.region=us-east-1
```

3. Save file
4. **Lưu ý**: File `aws.properties` đã được thêm vào `.gitignore` nên sẽ không commit credentials lên Git

**Alternative - Environment Variables (Optional):**

**IntelliJ IDEA:**
1. Run → Edit Configurations
2. Chọn Tomcat server configuration
3. Trong tab **Startup/Connection**, tìm **Environment variables**
4. Click icon **+** và thêm:
```
AWS_ACCESS_KEY_ID=AKIAIOSFODNN7EXAMPLE
AWS_SECRET_ACCESS_KEY=wJalrXUtnFEMI/K7MDENG/bPxRfiCYEXAMPLEKEY
AWS_REGION=us-east-1
```

**Standalone Tomcat:**

Windows - Tạo file `TOMCAT_HOME/bin/setenv.bat`:
```batch
set AWS_ACCESS_KEY_ID=AKIAIOSFODNN7EXAMPLE
set AWS_SECRET_ACCESS_KEY=wJalrXUtnFEMI/K7MDENG/bPxRfiCYEXAMPLEKEY
set AWS_REGION=us-east-1
```

Linux/Mac - Tạo file `TOMCAT_HOME/bin/setenv.sh`:
```bash
export AWS_ACCESS_KEY_ID="AKIAIOSFODNN7EXAMPLE"
export AWS_SECRET_ACCESS_KEY="wJalrXUtnFEMI/K7MDENG/bPxRfiCYEXAMPLEKEY"
export AWS_REGION="us-east-1"
```

#### Option 2: AWS Credentials File

Tạo file `~/.aws/credentials`:
```ini
[default]
aws_access_key_id = AKIAIOSFODNN7EXAMPLE
aws_secret_access_key = wJalrXUtnFEMI/K7MDENG/bPxRfiCYEXAMPLEKEY
region = us-east-1
```

### Bước 5: Build và Deploy

```bash
# Rebuild project
mvn clean package

# Restart Tomcat
# IntelliJ: Stop server → Run lại
# Standalone: ./catalina.sh stop && ./catalina.sh start
```

### Bước 6: Test

1. Mở http://localhost:8080/wellness-app/heart-rate-measurement.html
2. Cho phép camera trước
3. Nhìn vào camera
4. Kiểm tra console log:
   - `✅ Using AWS Rekognition for emotion detection`
   - `✅ AWS Rekognition detected: happy 0.95`

## 💰 Chi phí

**AWS Free Tier (12 tháng đầu):**
- 5,000 images/tháng miễn phí
- Sau đó: $1.00 per 1,000 images

**Ước tính cho app:**
- 1 user check-in/ngày = 1 image
- 100 users = 100 images/ngày = 3,000/tháng
- **Chi phí: $0** (trong free tier)

## 🔒 Bảo mật

⚠️ **QUAN TRỌNG:**
- ❌ **KHÔNG BAO GIỜ** commit AWS credentials vào Git
- ✅ Thêm `.env` vào `.gitignore`
- ✅ Dùng Environment Variables
- ✅ Rotate access keys định kỳ (3-6 tháng)
- ✅ Xóa access keys không dùng

## 🐛 Troubleshooting

**Lỗi: "AWS credentials not found"**
```
Kiểm tra:
1. Environment variables đã set chưa?
2. Restart Tomcat sau khi set env vars
3. Check console log khi Tomcat start
```

**Lỗi: "Access Denied"**
```
Kiểm tra:
1. IAM user có policy AmazonRekognitionFullAccess chưa?
2. Access key còn active không?
3. Region có đúng không? (mặc định: us-east-1)
```

**Lỗi: "No face detected"**
```
Giải pháp:
1. Nhìn thẳng vào camera
2. Đảm bảo ánh sáng đủ
3. Khuôn mặt nằm trong khung hình
```

## 📊 So sánh: Face-api.js vs AWS Rekognition

| Feature | Face-api.js | AWS Rekognition |
|---------|-------------|-----------------|
| Độ chính xác | ~75-80% | ~95%+ |
| Số cảm xúc | 7 | 8 |
| Tốc độ | ~500ms | ~300ms |
| Chi phí | Miễn phí | $1/1000 images |
| Cần Internet | Không | Có |
| Setup | Dễ (CDN) | Trung bình (AWS account) |

## ✅ Checklist

- [ ] Tạo AWS account
- [ ] Tạo IAM user với AmazonRekognitionFullAccess
- [ ] Tạo Access Key
- [ ] Set Environment Variables (AWS_ACCESS_KEY_ID, AWS_SECRET_ACCESS_KEY, AWS_REGION)
- [ ] Rebuild project (mvn clean package)
- [ ] Restart Tomcat
- [ ] Test trên browser

## 🎉 Kết quả

Sau khi setup xong, hệ thống sẽ:
1. ✅ Tự động phát hiện khuôn mặt qua camera trước
2. ✅ Nhận diện cảm xúc với AWS Rekognition (độ chính xác cao)
3. ✅ Hiển thị cảm xúc + % confidence
4. ✅ Chuyển sang camera sau để đo nhịp tim
5. ✅ Tính điểm stress tổng hợp (cảm xúc + nhịp tim + HRV)
