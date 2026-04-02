# SmartCare Revenue Analytics Service

Dịch vụ thống kê doanh thu cho hệ thống quản lý đặt lịch khám bệnh SmartCare, được xây dựng bằng Node.js/Express.

## 📋 Tính Năng

- ✅ **API thống kê doanh thu theo tháng** - Lấy doanh thu từng tháng trong năm
- ✅ **Biểu đồ doanh thu** - Hiển thị biểu đồ cột (Bar Chart) với Chart.js
- ✅ **Tóm tắt doanh thu** - Tổng doanh thu, trung bình, tổng lịch khám
- ✅ **Thống kê bác sĩ** - Xem doanh thu của từng bác sĩ
- ✅ **Responsive UI** - Giao diện đẹp, tương thích trên mobile

## 🚀 Cài Đặt

### Yêu Cầu
- **Node.js** >= 14.x
- **npm** hoặc **yarn**
- **MySQL Database** (database của Spring Boot SmartCare)

### Các Bước Cài Đặt

1. **Clone/Tải thư mục `revenue-service`** vào project SmartCare
   ```bash
   cd smartcare/revenue-service
   ```

2. **Cài đặt dependencies**
   ```bash
   npm install
   ```

3. **Cấu hình database** - Sửa file `.env`
   ```env
   # Database Configuration (kết nối tới MySQL của Spring Boot)
   DB_HOST=localhost
   DB_PORT=3306
   DB_NAME=smartcare_db
   DB_USER=root
   DB_PASSWORD=your_password

   # Server Configuration
   PORT=3001
   NODE_ENV=development
   ```

4. **Khởi chạy server**
   ```bash
   # Development mode (với hot reload)
   npm run dev

   # Production mode
   npm start
   ```

   Server sẽ chạy trên: `http://localhost:3001`

## 📊 API Endpoints

### 1. Lấy doanh thu theo tháng (Sử dụng trong biểu đồ)
```http
GET /api/revenue/monthly
```

**Response:**
```json
{
  "code": 200,
  "message": "Lấy dữ liệu doanh thu thành công!",
  "data": {
    "year": 2026,
    "monthlyRevenue": [
      {
        "month": 1,
        "monthName": "Tháng 1",
        "completedCount": 5,
        "revenueVnd": 1000000
      },
      ...
    ],
    "totalRevenue": 12000000,
    "totalAppointments": 60
  }
}
```

**Logic Nghiệp Vụ:**
- Đếm số lịch khám có `status = 'COMPLETED'` trong mỗi tháng
- Nhân với **200.000 VNĐ** (phí khám cố định)
- Công thức: `revenue = completed_count × 200,000`

### 2. Lấy tóm tắt doanh thu
```http
GET /api/revenue/summary
```

**Response:**
```json
{
  "code": 200,
  "message": "Lấy tóm tắt doanh thu thành công!",
  "data": {
    "year": 2026,
    "totalAppointments": 60,
    "totalRevenue": 12000000,
    "avgMonthlyRevenue": 1000000,
    "feePerAppointment": 200000
  }
}
```

### 3. Lấy doanh thu bác sĩ
```http
GET /api/revenue/doctors/:doctorId
```

**Response:**
```json
{
  "code": 200,
  "message": "Lấy doanh thu bác sĩ thành công!",
  "data": {
    "doctorId": 1,
    "doctorName": "Bác sĩ Nguyễn Văn A",
    "year": 2026,
    "monthlyRevenue": [...],
    "totalRevenue": 2000000,
    "totalAppointments": 10
  }
}
```

### 4. Health Check
```http
GET /health
```

## 📈 Frontend Dashboard

Truy cập dashboard thống kê tại: `http://localhost:3001`

**Tính Năng:**
- Hiển thị 4 summary cards: Tổng doanh thu, Tổng lịch khám, Doanh thu trung bình, Phí cố định
- Biểu đồ cột theo 12 tháng với 2 trục Y: Doanh thu (trái) và Số lịch khám (phải)
- Bảng chi tiết doanh thu từng tháng
- Auto-refresh dữ liệu mỗi 5 phút
- Responsive design (desktop, tablet, mobile)

## 🛠️ SQL Query Chi Tiết

### Query lấy doanh thu theo tháng:
```sql
SELECT 
  MONTH(s.work_date) AS month,
  COUNT(a.id) AS completed_count,
  COUNT(a.id) * 200000 AS revenue_vnd
FROM appointments a
INNER JOIN schedules s ON a.schedule_id = s.id
WHERE a.status = 'COMPLETED'
  AND YEAR(s.work_date) = YEAR(CURDATE())
GROUP BY MONTH(s.work_date)
ORDER BY month ASC;
```

**Giải Thích:**
- **MONTH(s.work_date)** - Lấy tháng từ ngày làm việc
- **COUNT(a.id)** - Đếm số appointments
- **`* 200000`** - Nhân với phí khám cố định
- **WHERE a.status = 'COMPLETED'** - Chỉ tính appointments hoàn thành
- **GROUP BY MONTH** - Nhóm theo tháng

## 📁 Cấu Trúc Thư Mục

```
revenue-service/
├── server.js                 # File chính (Express server, API endpoints)
├── package.json             # Dependencies
├── .env                      # Cấu hình database
├── .env.example             # Mẫu .env
├── public/
│   └── index.html           # Frontend dashboard
├── README.md                # Tài liệu này
└── .gitignore
```

## 🔌 Tích Hợp Với Spring Boot

Dịch vụ Node.js này kết nối tới cùng một database MySQL mà Spring Boot đang sử dụng.

**Cấu hình trong Spring Boot:**
1. Thêm endpoint API từ Node.js vào dashboard admin
2. Hoặc embed iframe dashboard:
   ```html
   <iframe src="http://localhost:3001" width="100%" height="600"></iframe>
   ```

## 🧪 Testing

### Test API bằng cURL:
```bash
# Lấy doanh thu theo tháng
curl http://localhost:3001/api/revenue/monthly

# Lấy tóm tắt doanh thu
curl http://localhost:3001/api/revenue/summary

# Lấy doanh thu bác sĩ ID 1
curl http://localhost:3001/api/revenue/doctors/1

# Health check
curl http://localhost:3001/health
```

## 🐛 Debugging

### Nếu gặp lỗi "Cannot find module":
```bash
npm install
npm install --save express mysql2/promise cors dotenv
```

### Nếu lỗi kết nối database:
- Kiểm tra cấu hình `.env` matching với MySQL setup
- Đảm bảo MySQL user có quyền truy cập database `smartcare_db`
- Kiểm tra database có bảng: `appointments`, `schedules`, `users`

### Nếu lỗi CORS:
- Frontend và API phải cùng domain hoặc nginx proxy
- Hoặc add header CORS trong public HTML

## 📝 Comments Trong Code

- **server.js**: Hầu hết các hàm có comment tiếng Việt giải thích logic
- **public/index.html**: Mã JavaScript có comment chi tiết

## 🚀 Production Deployment

```bash
# Cài đặt PM2 để chạy server 24/7
npm install -g pm2

# Start service
pm2 start server.js --name "smartcare-revenue"

# Auto restart khi máy reboot
pm2 startup
pm2 save
```

## 📞 Support

Nếu gặp vấn đề:
1. Kiểm tra logs: `npm run dev` sẽ in ra console
2. Đảm bảo database chạy và có dữ liệu
3. Kiểm tra port 3001 không bị chiếm dụng

---

**Version:** 1.0.0  
**Created:** 2026  
**License:** MIT  
**Author:** SmartCare Team
