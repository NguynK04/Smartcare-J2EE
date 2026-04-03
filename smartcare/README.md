# 🏥 SmartCare - Quick Start Guide

## 🎯 Tổng Quan Dự Án

SmartCare là **nền tảng khám bệnh trực tuyến**.  
Cho phép **Bệnh Nhân** đặt lịch khám, **Bác Sĩ** quản lý lịch, **Admin** quản lý hệ thống.

---

## 📁 Cấu Trúc Project

```
smartcare/
├── src/
│   ├── main/java/com/example/smartcare/
│   │   ├── controller/        (8 controllers)
│   │   ├── service/           (9 services)
│   │   ├── repository/        (6 repositories)
│   │   ├── entity/            (6 entities)
│   │   ├── dto/              (18+ DTOs)
│   │   ├── security/         (JWT & Auth config)
│   │   ├── enums/            (Role, AppointmentStatus)
│   │   └── exception/        (Error handling)
│   └── main/resources/
│       ├── templates/         (15+ HTML views)
│       ├── static/
│       │   ├── css/style.css
│       │   └── js/main.js
│       └── application.properties
├── pom.xml                    (Maven dependencies)
├── SMARTCARE_DOCUMENTATION.md (Chi tiết đầy đủ)
└── README.md                  (File này)
```

---

## 🚀 Bắt Đầu Nhanh (5 phút)

### Step 1: Chuẩn Bị Database
```bash
# Tạo database
mysql -u root -p123456

# SQL:
CREATE DATABASE smartcare_db;
```

### Step 2: Config Email (Optional)
Sửa file `src/main/resources/application.properties`:
```properties
spring.mail.username=your-email@gmail.com
spring.mail.password=your-app-password
```

### Step 3: Build & Run
```bash
cd smartcare

# Compile
mvn clean compile

# Run
mvn spring-boot:run

# ✅ Server chạy tại: http://localhost:8080/
```

### Step 4: Truy Cập
```
🌐 Trang chủ:        http://localhost:8080/
🔓 Đăng nhập:        http://localhost:8080/login
📝 Đăng ký:         http://localhost:8080/register
```

---

## 📋 Các Vai Trò & Tính Năng

### 👥 Bệnh Nhân (PATIENT)
| Tính Năng | Endpoint | Trang |
|-----------|----------|-------|
| Tìm bác sĩ | `GET /api/v1/doctors/search` | `/patient/search-doctors` |
| Đặt lịch | `POST /api/v1/appointments` | (từ search-doctors) |
| Xem lịch sử | `GET /api/v1/appointments/my-history` | `/patient/appointments` |
| Đánh giá | `POST /api/v1/reviews` | `/patient/review` |

**Test User**: `patient1` / `pass123`

---

### 👨‍⚕️ Bác Sĩ (DOCTOR)
| Tính Năng | Endpoint | Trang |
|-----------|----------|-------|
| Thêm lịch | `POST /api/v1/schedules` | `/doctor/schedules` |
| Xem bệnh nhân | `GET /api/v1/appointments/doctor-requests` | `/doctor/dashboard` |
| Ghi chép | `POST /api/v1/medical-records` | `/doctor/medical-record` |

**Test User**: `doctor1` / `pass123`

---

### 🔐 Quản Trị (ADMIN)
| Tính Năng | Endpoint | Trang |
|-----------|----------|-------|
| Dashboard | `GET /api/v1/admin/dashboard` | `/admin/dashboard` |
| Duyệt bác sĩ | `PUT /api/v1/admin/doctors/{id}/approve` | (Dashboard) |
| Chuyên khoa | `GET/POST /api/v1/specialties` | `/admin/specialties` |
| Thống kê | `GET /api/v1/admin/stats` | `/admin/stats` |

**Test User**: `admin1` / `pass123`

---

## 🔐 Quy Trình Xác Thực

```
1. User nhập Username + Password
   ↓
2. API: POST /auth/login
   ↓
3. Server kiểm tra password (BCrypt)
   ↓
4. Phát hành JWT Token (24 giờ)
   ↓
5. Client lưu: localStorage.setItem('token', token)
   ↓
6. Mỗi request: Authorization: Bearer <token>
```

### Cách Tạo Tài Khoản Test:
```javascript
// Trong browser console, gọi API register:
fetch('/api/v1/auth/register', {
  method: 'POST',
  headers: { 'Content-Type': 'application/json' },
  body: JSON.stringify({
    username: 'testuser',
    password: 'pass123',
    email: 'test@smartcare.com',
    fullName: 'Test User',
    role: 'PATIENT'
  })
})
.then(r => r.json())
.then(d => console.log(d))
```

---

## 🎨 Giao Diện Responsive

Tất cả pages được thiết kế **Mobile-Friendly**:
- ✅ Desktop (1200px+)
- ✅ Tablet (768px-1199px)
- ✅ Mobile (< 768px)

### CSS Framework
- **Custom CSS** (không dùng Bootstrap CDN)
- **Flexbox & Grid** untuk layout
- **Color Scheme**: Blue Primary, Green Secondary

---

## 📞 API Base URL

```
http://localhost:8080/api/v1/
```

### Ví Dụ Request:
```javascript
// GET /api/v1/doctors/search?specialty=1&name=nguyen
const response = await fetch('/api/v1/doctors/search?specialty=1', {
  headers: {
    'Authorization': `Bearer ${localStorage.getItem('token')}`
  }
});
const data = await response.json();
```

---

## ⚙️ Application Properties

File: `src/main/resources/application.properties`

```properties
# Database
spring.datasource.url=jdbc:mysql://localhost:3306/smartcare_db
spring.datasource.username=root
spring.datasource.password=123456

# Server
server.port=8080

# JWT
jwt.secret=404E635266556A586E3272357538782F413F4428472B4B6250645367566B5970
jwt.expiration=86400000  # 24 hours in milliseconds

# Email (Gmail SMTP)
spring.mail.host=smtp.gmail.com
spring.mail.port=587
spring.mail.username=your-email@gmail.com
spring.mail.password=your-app-password
spring.mail.properties.mail.smtp.auth=true
spring.mail.properties.mail.smtp.starttls.enable=true

# JPA/Hibernate
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true
```

---

## 🧪 Testing APIs

### Với Postman / curl:

```bash
# 1️⃣ Đăng nhập
curl -X POST http://localhost:8080/api/v1/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"patient1","password":"pass123"}'

# Response:
{
  "code": 200,
  "message": "Đăng nhập thành công",
  "data": {
    "token": "eyJhbGc...",
    "role": "PATIENT"
  }
}

# 2️⃣ Tìm bác sĩ (dùng token từ trên)
curl -X GET "http://localhost:8080/api/v1/doctors/search?specialty=1" \
  -H "Authorization: Bearer eyJhbGc..."

# 3️⃣ Đặt lịch
curl -X POST http://localhost:8080/api/v1/appointments \
  -H "Authorization: Bearer eyJhbGc..." \
  -H "Content-Type: application/json" \
  -d '{"scheduleId": 1, "reason": "Đau đầu"}'
```

---

## 🐛 Troubleshooting

### ❌ "Port 8080 đang sử dụng"
```bash
# Windows: Tìm process dùng port 8080
netstat -ano | findstr :8080

# Kill process (ghi PID từ kết quả trên)
taskkill /PID <PID> /F
```

### ❌ "MySQL connection failed"
- Kiểm tra MySQL service chạy: `mysql -u root -p`
- Check credentials trong `application.properties`
- Đảm bảo database `smartcare_db` đã được tạo

### ❌ "JWT token expired"
- Clear localStorage: `localStorage.clear()`
- Đăng nhập lại để lấy token mới

### ❌ "email service error"
- Gmail app password cần cấp - xem [Link hướng dẫn](https://support.google.com/accounts/answer/185833)
- Hoặc disable email gửi tạm thời (comment `emailService.send...`)

---

## 📦 Dependencies Chính

```xml
<!-- Backend -->
spring-boot-starter-web-mvc
spring-boot-starter-security
spring-boot-starter-data-jpa
spring-boot-starter-thymeleaf
spring-boot-starter-mail

jjwt (JWT tokens)
mysql-connector-java
lombok
```

---

## 🔍 Project Highlights

✨ **Đặc Điểm Nổi Bật:**
- ✅ **Microservice-ready**: REST APIs với JSON responses
- ✅ **Secure**: JWT tokens + Spring Security Role-based Access Control
- ✅ **Responsive**: Mobile-friendly HTML templates
- ✅ **Email Notifications**: Tự động gửi email xác nhận
- ✅ **Scalable**: JPA Repository pattern cho database queries
- ✅ **Error Handling**: Global exception handler
- ✅ **Logging**: Hibernate SQL logging (dev mode)

---

## 🎓 Learning Outcomes

Dự án này cover các kỹ năng:
- 🏗️ **Backend Architecture**: Controller → Service → Repository
- 🔐 **Security**: JWT authentication, Role-based authorization
- 💾 **Database**: JPA/Hibernate ORM, MySQL
- 🌐 **REST APIs**: RESTful design, JSON serialization
- 🎨 **Frontend**: HTML5, CSS3, JavaScript ES6+
- 📧 **Integration**: Email service, external APIs
- ⚙️ **DevOps**: Maven build, configuration management

---

## 📝 Next Steps

### Phase 2 (Advanced):
- [ ] Payment integration (Stripe/VNPay)
- [ ] Video consultation (WebRTC)
- [ ] Mobile app (React Native / Flutter)
- [ ] Analytics dashboard (Charts.js)
- [ ] Doctor availability prediction (ML)
- [ ] Message/Chat system (WebSocket)

### Optimization:
- [ ] Caching (Redis)
- [ ] Database indexing
- [ ] Load balancing (Nginx)
- [ ] Docker containerization
- [ ] CI/CD pipeline (GitHub Actions)

---

## 📚 Useful Resources

- **Spring Boot Docs**: https://spring.io/projects/spring-boot
- **JWT Tutorial**: https://jwt.io/introduction
- **JPA/Hibernate**: https://www.baeldung.com/the-persistence-layer-with-spring-data-jpa
- **REST Best Practices**: https://restfulapi.net/
- **MySQL Documentation**: https://dev.mysql.com/doc/

---

## 👨‍💻 Code Contributors

- **GitHub Copilot** - Project Setup & Implementation
- **Spring Framework Team** - Framework
- **MySQL Team** - Database

---

## 📄 License

© 2026 SmartCare. All Rights Reserved.

---

**Happy Coding! 🚀**

**Phiên bản**: 1.0.0  
**Cập nhật lần cuối**: 27/03/2026

---

## 📞 Support

Gặp vấn đề? Hãy:
1. Kiểm tra `SMARTCARE_DOCUMENTATION.md` để biết thêm chi tiết
2. Xem logs tomcat trong console
3. Check database schema: `SHOW TABLES;`
4. Gọi API với curl để debug

Chúc bạn phát triển thành công! ✨
