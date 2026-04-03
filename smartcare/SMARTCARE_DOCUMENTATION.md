# 📋 SMARTCARE - Hệ Thống Quản Lý Khám Bệnh Trực Tuyến

## 🎯 Mô Tả Dự Án

SmartCare là một nền tảng khám bệnh trực tuyến toàn diện, cho phép:
- **Bệnh Nhân** tìm kiếm bác sĩ, đặt lịch khám, xem lịch sử và đánh giá
- **Bác Sĩ** quản lý lịch làm việc, xem bệnh nhân, ghi chép kết quả khám
- **Quản Trị Viên** quản lý hệ thống, duyệt bác sĩ mới, xem thống kê

---

## 🏗️ Cấu Trúc Dự Án

### Backend Structure
```
src/main/java/com/example/smartcare/
├── controller/
│   ├── ViewController.java         (Serve HTML views)
│   ├── AuthController.java         (Đăng nhập/Đăng ký)
│   ├── AppointmentController.java  (Quản lý lịch khám)
│   ├── ScheduleController.java     (Quản lý ca làm việc)
│   ├── ReviewController.java       (Đánh giá)
│   ├── MedicalRecordController.java (Ghi chép y tế)
│   ├── AdminController.java        (Dashboard Admin)
│   ├── SpecialtyController.java    (Chuyên khoa)
│   └── UserController.java         (Quản lý người dùng)
│
├── service/
│   ├── AuthService.java            (Xác thực & JWT)
│   ├── AppointmentService.java     (Logic đặt lịch)
│   ├── ScheduleService.java        (Logic lịch làm việc)
│   ├── ReviewService.java          (Logic đánh giá)
│   ├── MedicalRecordService.java   (Logic ghi chép)
│   ├── SpecialtyService.java       (Logic chuyên khoa)
│   ├── EmailService.java           (Gửi email thông báo)
│   ├── DashboardService.java       (Thống kê)
│   └── UserService.java            (Quản lý người dùng)
│
├── repository/
│   ├── UserRepository.java
│   ├── ScheduleRepository.java
│   ├── AppointmentRepository.java
│   ├── ReviewRepository.java
│   ├── MedicalRecordRepository.java
│   └── SpecialtyRepository.java
│
├── entity/
│   ├── User.java                   (Người dùng: Patient, Doctor, Admin)
│   ├── Schedule.java               (Ca làm việc của bác sĩ)
│   ├── Appointment.java            (Lịch khám bệnh nhân)
│   ├── Review.java                 (Đánh giá bác sĩ)
│   ├── Specialty.java              (Chuyên khoa)
│   └── MedicalRecord.java          (Ghi chép y tế)
│
├── dto/
│   ├── LoginRequest/Response
│   ├── RegisterRequest
│   ├── AppointmentRequest/Response
│   ├── ScheduleRequest/Response
│   ├── ReviewRequest/Response
│   ├── DoctorResponse
│   ├── AdminDashboardResponse
│   └── ...
│
├── enums/
│   ├── Role.java                   (ADMIN, DOCTOR, PATIENT)
│   └── AppointmentStatus.java      (PENDING, CONFIRMED, COMPLETED, CANCELLED)
│
├── security/
│   ├── SecurityConfig.java         (Cấu hình Spring Security)
│   ├── JwtService.java             (JWT token generation & validation)
│   └── ApplicationConfig.java      (Bean configuration)
│
└── exception/
    └── GlobalExceptionHandler.java (Xử lý lỗi)

src/main/resources/
├── templates/                      (HTML views)
│   ├── index.html                  (Trang chủ)
│   ├── login.html, register.html   (Xác thực)
│   ├── patient/
│   │   ├── dashboard.html
│   │   ├── search-doctors.html
│   │   ├── appointments.html
│   │   ├── profile.html
│   │   └── review.html
│   ├── doctor/
│   │   ├── dashboard.html
│   │   ├── schedules.html
│   │   ├── profile.html
│   │   ├── medical-record.html
│   │   └── patients.html
│   └── admin/
│       ├── dashboard.html
│       ├── doctors.html
│       ├── specialties.html
│       ├── stats.html
│       └── profile.html
│
├── static/
│   ├── css/
│   │   └── style.css               (CSS chung)
│   └── js/
│       └── main.js                 (Utility JS functions)
│
└── application.properties           (Cấu hình ứng dụng)
```

---

## 🔐 3 Vai Trò Chính & Chức Năng

### 👥 **1. Bệnh Nhân (PATIENT)**

#### Chức Năng Cơ Bản:
- ✅ Đăng ký tài khoản với email, họ tên, username, password
- ✅ Đăng nhập với JWT token
- ✅ Quản lý hồ sơ cá nhân (họ tên, email, SĐT, địa chỉ)

#### Chức Năng Cốt Lõi:
- ✅ **Tìm kiếm bác sĩ** theo:
  - Chuyên khoa (Khoa Nội, Tim Mạch, Nhi Khoa, v.v.)
  - Tên bác sĩ
  - Xem thông tin: chuyên khoa, kinh nghiệm, đánh giá
- ✅ **Xem lịch trống** của bác sĩ theo ngày/giờ
- ✅ **Đặt lịch khám** (Booking):
  - Chọn bác sĩ → chọn ca khám → nhập lý do khám → đặt
  - Nhận xác nhận qua email

#### Chức Năng Cao Cấp:
- ✅ **Nhận email/SMS** xác nhận đặt lịch & nhắc nhở trước giờ khám
- ✅ **Xem lịch sử khám bệnh**:
  - Danh sách tất cả lần khám (cũ & mới)
  - Chi tiết: bác sĩ, ngày/giờ, lý do, trạng thái
- ✅ **Tải xuống kết quả khám/đơn thuốc** (PDF - chưa implement)
- ✅ **Đánh giá & nhận xét** sau khám:
  - Rating 1-5 sao
  - Bình luận chi tiết
- ✅ **Hủy lịch khám** trước giờ (nếu trạng thái là PENDING)

### 👨‍⚕️ **2. Bác Sĩ (DOCTOR)**

#### Chức Năng Cơ Bản:
- ✅ Đăng ký tài khoản với thông tin chuyên môn
- ✅ Quản lý thông tin cá nhân:
  - Họ tên, SĐT, địa chỉ phòng khám
  - Chuyên khoa, kinh nghiệm, bằng cấp
  - Tiểu sử chuyên môn

#### Chức Năng Cốt Lõi:
- ✅ **Quản lý lịch làm việc**:
  - Khai báo ca khám: chọn ngày, giờ bắt đầu, giờ kết thúc
  - Xem danh sách lịch của mình
  - Xóa lịch khám nếu chưa có ai đặt

#### Chức Năng Cao Cấp:
- ✅ **Xem danh sách bệnh nhân đặt lịch**:
  - Sắp xếp theo ngày/giờ
  - Hiển thị: tên bệnh nhân, ngày/giờ, lý do khám
  - Lọc theo trạng thái (PENDING, CONFIRMED, COMPLETED)
- ✅ **Cập nhật trạng thái ca khám**:
  - PENDING → CONFIRMED (xác nhận)
  - CONFIRMED → COMPLETED (hoàn thành)
  - CANCELLED (hủy nếu cần)
- ✅ **Ghi chú kết quả khám**:
  - Nhập chẩn đoán bệnh (bắt buộc)
  - Kê đơn thuốc
  - Lời dặn cho bệnh nhân
  - Lưu vào hệ thống

### 🔐 **3. Quản Trị Viên (ADMIN)**

#### Chức Năng Cơ Bản:
- ✅ Quản lý danh mục:
  - Thêm/sửa/xóa chuyên khoa
- ✅ Quản lý tài khoản:
  - Xem danh sách người dùng
  - Khóa/mở khóa tài khoản
  - Đặt lại mật khẩu

#### Chức Năng Cao Cấp:
- ✅ **Duyệt hồ sơ bác sĩ mới**:
  - Xem danh sách bác sĩ chờ duyệt
  - Kiểm tra: tên, email, chuyên khoa, giấy phép
  - Duyệt → kích hoạt tài khoản
  - Từ chối → xóa tài khoản
- ✅ **Dashboard thống kê**:
  - Tổng số bác sĩ, bệnh nhân, ca khám
  - Biểu đồ doanh thu theo tháng (nếu có phí khám)
  - Số ca khám theo chuyên khoa
  - Tỷ lệ hủy lịch
- ✅ **Báo cáo chi tiết**:
  - Lọc theo khoảng thời gian
  - Xuất dữ liệu (CSV/PDF - chưa implement)

---

## 📡 API Endpoints

### Auth Endpoints
```
POST   /api/v1/auth/register        # Đăng ký
POST   /api/v1/auth/login           # Đăng nhập
```

### Patient Endpoints
```
GET    /api/v1/doctors/search       # Tìm bác sĩ
GET    /api/v1/doctors/{id}/schedules # Xem lịch trống
POST   /api/v1/appointments        # Đặt lịch
GET    /api/v1/appointments/my-history # Xem lịch sử
PUT    /api/v1/appointments/{id}/cancel # Hủy lịch
POST   /api/v1/reviews             # Đánh giá
```

### Doctor Endpoints
```
GET    /api/v1/schedules/my        # Xem lịch của mình
POST   /api/v1/schedules           # Tạo lịch mới
DELETE /api/v1/schedules/{id}      # Xóa lịch
GET    /api/v1/appointments/doctor-requests # Xem bệnh nhân
PUT    /api/v1/appointments/{id}/status # Cập nhật trạng thái
POST   /api/v1/medical-records     # Ghi chép kết quả
```

### Admin Endpoints
```
GET    /api/v1/admin/dashboard     # Thống kê tổng hợp
GET    /api/v1/admin/doctors/pending # Bác sĩ chờ duyệt
PUT    /api/v1/admin/doctors/{id}/approve # Duyệt bác sĩ
GET    /api/v1/admin/stats         # Chi tiết thống kê
POST   /api/v1/specialties         # Thêm chuyên khoa
GET    /api/v1/specialties         # Xem chuyên khoa
```

---

## 🔒 Xác Thực & Bảo Mật

### Quy Trình Đăng Nhập
1. User gửi `username` + `password`
2. Server xác minh password (BCrypt)
3. Nếu đúng: Phát hành **JWT Token**
4. Client lưu token trong `localStorage`
5. Mỗi request gửi kèm: `Authorization: Bearer <token>`

### JWT Token Structure
```
Header: {
  "alg": "HS256",
  "typ": "JWT"
}

Payload: {
  "sub": "username",
  "role": "PATIENT",
  "iat": 1234567890,
  "exp": 1234654290  # Hết hạn sau 24 giờ
}
```

### Phân Quyền (Role-based Access)
- **PATIENT**: Chỉ access resources của chính mình
- **DOCTOR**: Chỉ quản lý lịch/bệnh nhân của chính mình
- **ADMIN**: Toàn quyền quản lý hệ thống

---

## 📧 Email Service

### Tích Hợp Gmail SMTP
```properties
spring.mail.host=smtp.gmail.com
spring.mail.username=your-email@gmail.com
spring.mail.password=your-app-password
```

### Các Email Gửi Tự Động:
1. **Xác nhận đặt lịch** → Gửi cho bệnh nhân
2. **Nhắc nhở trước khám** → Gửi 24h trước (cần job scheduler)
3. **Kết quả khám** → Gửi sau khi bác sĩ ghi chép (chưa implement)

---

## 🗄️ Database Schema

### Users Table
```sql
CREATE TABLE users (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  username VARCHAR(50) UNIQUE NOT NULL,
  password VARCHAR(255) NOT NULL,
  full_name VARCHAR(255) NOT NULL,
  email VARCHAR(255) UNIQUE NOT NULL,
  phone VARCHAR(20),
  address VARCHAR(500),
  role ENUM('ADMIN', 'DOCTOR', 'PATIENT') NOT NULL,
  specialty_id BIGINT,  -- FK (chỉ dùng cho Doctor)
  is_active BOOLEAN DEFAULT true
);
```

### Schedules Table (Ca Làm Việc)
```sql
CREATE TABLE schedules (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  doctor_id BIGINT NOT NULL,
  work_date DATE NOT NULL,
  start_time TIME NOT NULL,
  end_time TIME NOT NULL,
  is_booked BOOLEAN DEFAULT false,
  FOREIGN KEY (doctor_id) REFERENCES users(id)
);
```

### Appointments Table (Lịch Khám)
```sql
CREATE TABLE appointments (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  patient_id BIGINT NOT NULL,
  schedule_id BIGINT NOT NULL,
  reason TEXT,
  status ENUM('PENDING', 'CONFIRMED', 'COMPLETED', 'CANCELLED'),
  created_at TIMESTAMP,
  FOREIGN KEY (patient_id) REFERENCES users(id),
  FOREIGN KEY (schedule_id) REFERENCES schedules(id)
);
```

---

## 🚀 Hướng Dẫn Chạy

### 1. Chuẩn Bị Database
```sql
CREATE DATABASE smartcare_db;
USE smartcare_db;
# (Hibernate sẽ tự tạo tables với ddl-auto=update)
```

### 2. Cấu Hình application.properties
```properties
# Thay đổi MySQL credentials nếu cần
spring.datasource.url=jdbc:mysql://localhost:3306/smartcare_db
spring.datasource.username=root
spring.datasource.password=123456

# Cấu hình Email
spring.mail.username=your-email@gmail.com
spring.mail.password=your-app-password
```

### 3. Build & Run
```bash
# Build project
mvn clean install

# Chạy ứng dụng
mvn spring-boot:run

# Hoặc run file JAR
java -jar target/smartcare-0.0.1-SNAPSHOT.jar
```

### 4. Truy Cập
```
Trang chủ: http://localhost:8080/
API Base: http://localhost:8080/api/v1/
```

---

## 📝 Test Accounts

### Test User Credentials
| Role    | Username | Password | Email                  |
|---------|----------|----------|------------------------|
| Patient | patient1 | pass123  | patient1@smartcare.com |
| Doctor  | doctor1  | pass123  | doctor1@smartcare.com  |
| Admin   | admin1   | pass123  | admin@smartcare.com    |

> **Ghi chú**: Tạo tài khoản test bằng API `/auth/register` hoặc insert trực tiếp vào database.

---

## 🔧 Công Nghệ Sử Dụng

### Backend
- **Spring Boot 4.0.5** - Framework chính
- **Spring Data JPA** - ORM & Database access
- **Spring Security** - Xác thực & phân quyền
- **JWT (jjwt)** - Token management
- **MySQL** - Database
- **Lombok** - Reduce boilerplate
- **Spring Mail** - Email service

### Frontend
- **HTML5 / CSS3 / Vanilla JavaScript** - UI
- **Thymeleaf** - Template engine
- **Bootstrap concepts** - Responsive design

---

## ✅ Checklist Chức Năng

### Bệnh Nhân
- [x] Đăng ký / Đăng nhập
- [x] Tìm kiếm bác sĩ
- [x] Xem lịch trống
- [x] Đặt lịch khám
- [x] Xem lịch sử khám
- [x] Hủy lịch khám
- [x] Đánh giá bác sĩ
- [ ] Tải xuống kết quả khám (PDF)
- [x] Nhận email xác nhận

### Bác Sĩ
- [x] Đăng ký / Đăng nhập
- [x] Quản lý lịch làm việc
- [x] Xem danh sách bệnh nhân
- [x] Cập nhật trạng thái ca khám
- [x] Ghi chép kết quả khám
- [ ] Kê đơn thuốc (integration)
- [x] Quản lý hồ sơ

### Admin
- [x] Duyệt bác sĩ mới
- [x] Quản lý chuyên khoa
- [x] Dashboard thống kê
- [x] Quản lý người dùng
- [ ] Xuất báo cáo (CSV/PDF)
- [ ] Quản lý thanh toán

---

## 🐛 Known Issues & TODO

### Cần Hoàn Thành:
1. **Payment Integration** - Tích hợp thanh toán (Stripe, VNPay)
2. **PDF Export** - Tải xuống kết quả khám
3. **Video Consultation** - Khám bệnh video trực tuyến
4. **Prescription Management** - Quản lý đơn thuốc chi tiết
5. **Notification** - Push notification / SMS
6. **Admin Analytics** - Biểu đồ chi tiết hơn

### Optional Features:
- Google/Facebook OAuth2 login
- Appointment rescheduling
- Doctor ratings algorithm
- AI symptom checker
- Insurance integration

---

## 📞 Support & Contact

- **Email**: support@smartcare.vn
- **Hotline**: 1800-XXXX
- **GitHub**: [Link to repository]

---

## 📄 License

© 2026 SmartCare. All rights reserved.

---

**Được tạo bởi: GitHub Copilot**  
**Ngày cập nhật: 27/03/2026**
