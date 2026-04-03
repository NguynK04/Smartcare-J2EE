/**
 * SmartCare Revenue Analytics Service + Real-Time Chat
 * Node.js/Express API thống kê doanh thu + Socket.io Chat
 */

const express = require('express');
const http = require('http');
const mysql = require('mysql2/promise');
const cors = require('cors');
const socketIo = require('socket.io');
require('dotenv').config();

const app = express();
const server = http.createServer(app);
const io = socketIo(server, {
  cors: {
    origin: "*",
    methods: ["GET", "POST"]
  }
});

const PORT = process.env.PORT || 3001;

// =====================================================
// MIDDLEWARE
// =====================================================
app.use(cors());
app.use(express.json());

// =====================================================
// DATABASE CONNECTION POOL
// =====================================================
const pool = mysql.createPool({
  host: process.env.DB_HOST || 'localhost',
  port: process.env.DB_PORT || 3306,
  user: process.env.DB_USER || 'root',
  password: process.env.DB_PASSWORD || '',
  database: process.env.DB_NAME || 'smartcare_db',
  waitForConnections: true,
  connectionLimit: 10,
  queueLimit: 0
});

// =====================================================
// SOCKET.IO CHAT HANDLING
// =====================================================

// Map để lưu socket ID của từng user (userId -> socketId)
const userSocketMap = {};

io.on('connection', (socket) => {
  console.log(`✅ User connected: ${socket.id}`);

  /**
   * Event: user-login
   * Khi user login, lưu mapping userId <-> socketId
   */
  socket.on('user-login', (userId) => {
    userSocketMap[userId] = socket.id;
    console.log(`📌 User ${userId} mapped to socket ${socket.id}`);
    io.emit('user-online', { userId: userId, socketId: socket.id });
  });

  /**
   * Event: send-message
   * Nhận tin nhắn từ client và gửi đến người nhận (real-time)
   * + Lưu vào database
   */
  socket.on('send-message', async (data) => {
    // data: { senderId, receiverId, messageContent }
    console.log(`💬 Message from ${data.senderId} to ${data.receiverId}: ${data.messageContent}`);

    try {
      // 1. Lưu tin nhắn vào database
      const connection = await pool.getConnection();
      const insertQuery = `
        INSERT INTO chat_messages (sender_id, receiver_id, message_content, created_at)
        VALUES (?, ?, ?, NOW())
      `;
      const [result] = await connection.query(insertQuery, [
        data.senderId,
        data.receiverId,
        data.messageContent
      ]);
      connection.release();

      const messageData = {
        id: result.insertId,
        senderId: data.senderId,
        senderName: data.senderName || 'Unknown',
        receiverId: data.receiverId,
        messageContent: data.messageContent,
        createdAt: new Date()
      };

      // 2. Gửi tin nhắn đến người nhận (nếu online)
      const receiverSocketId = userSocketMap[data.receiverId];
      if (receiverSocketId) {
        io.to(receiverSocketId).emit('receive-message', messageData);
      }

      // 3. Gửi lại cho người gửi (để confirm)
      socket.emit('message-sent', messageData);

    } catch (error) {
      console.error('❌ Lỗi lưu tin nhắn:', error);
      socket.emit('message-error', { error: error.message });
    }
  });

  /**
   * Event: user-disconnect
   */
  socket.on('disconnect', () => {
    // Tìm userId từ userSocketMap
    const userId = Object.keys(userSocketMap).find(key => userSocketMap[key] === socket.id);
    if (userId) {
      delete userSocketMap[userId];
      console.log(`❌ User ${userId} disconnected`);
      io.emit('user-offline', { userId: userId });
    }
  });
});

// =====================================================
// API ENDPOINTS
// =====================================================

/**
 * GET /api/revenue/monthly
 * Lấy doanh thu theo từng tháng trong năm
 * 
 * Logic nghiệp vụ:
 * - Đếm số lượng lịch khám có status = 'COMPLETED' trong mỗi tháng
 * - Nhân với 200.000 VNĐ (phí khám cố định)
 * - Trả về dữ liệu dạng: { month: 1, revenue: 0, count: 0 }, ...
 */
app.get('/api/revenue/monthly', async (req, res) => {
  try {
    const connection = await pool.getConnection();

    // SQL Query: Đếm số COMPLETED appointments theo tháng
    const query = `
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
    `;

    const [rows] = await connection.query(query);
    connection.release();

    // Chuẩn bị dữ liệu đầy đủ cho 12 tháng (tính cả tháng 0 doanh thu)
    const monthlyData = [];
    for (let month = 1; month <= 12; month++) {
      const found = rows.find(row => row.month === month);
      monthlyData.push({
        month: month,
        monthName: getMonthName(month),
        completedCount: found ? found.completed_count : 0,
        revenueVnd: found ? found.revenue_vnd : 0
      });
    }

    // Trả về API response
    res.status(200).json({
      code: 200,
      message: 'Lấy dữ liệu doanh thu thành công!',
      data: {
        year: new Date().getFullYear(),
        monthlyRevenue: monthlyData,
        totalRevenue: monthlyData.reduce((sum, item) => sum + item.revenueVnd, 0),
        totalAppointments: monthlyData.reduce((sum, item) => sum + item.completedCount, 0)
      }
    });

  } catch (error) {
    console.error('❌ Lỗi khi lấy doanh thu:', error);
    res.status(500).json({
      code: 500,
      message: 'Lỗi server: ' + error.message
    });
  }
});

/**
 * GET /api/revenue/summary
 * Lấy tóm tắt doanh thu (tổng, trung bình, max)
 */
app.get('/api/revenue/summary', async (req, res) => {
  try {
    const connection = await pool.getConnection();

    const query = `
      SELECT 
        COUNT(a.id) AS total_appointments,
        COUNT(a.id) * 200000 AS total_revenue,
        ROUND(COUNT(a.id) * 200000 / 12, 0) AS avg_monthly_revenue
      FROM appointments a
      WHERE a.status = 'COMPLETED'
        AND YEAR(a.created_at) = YEAR(CURDATE());
    `;

    const [rows] = await connection.query(query);
    connection.release();

    const summary = rows[0] || {
      total_appointments: 0,
      total_revenue: 0,
      avg_monthly_revenue: 0
    };

    res.status(200).json({
      code: 200,
      message: 'Lấy tóm tắt doanh thu thành công!',
      data: {
        year: new Date().getFullYear(),
        totalAppointments: summary.total_appointments,
        totalRevenue: summary.total_revenue,
        avgMonthlyRevenue: summary.avg_monthly_revenue,
        feePerAppointment: 200000
      }
    });

  } catch (error) {
    console.error('❌ Lỗi khi lấy tóm tắt doanh thu:', error);
    res.status(500).json({
      code: 500,
      message: 'Lỗi server: ' + error.message
    });
  }
});

/**
 * GET /api/revenue/doctors/:doctorId
 * Lấy doanh thu của một bác sĩ cụ thể
 */
app.get('/api/revenue/doctors/:doctorId', async (req, res) => {
  try {
    const { doctorId } = req.params;
    const connection = await pool.getConnection();

    const query = `
      SELECT 
        u.id,
        u.full_name,
        MONTH(s.work_date) AS month,
        COUNT(a.id) AS completed_count,
        COUNT(a.id) * 200000 AS revenue_vnd
      FROM appointments a
      INNER JOIN schedules s ON a.schedule_id = s.id
      INNER JOIN users u ON s.doctor_id = u.id
      WHERE a.status = 'COMPLETED'
        AND s.doctor_id = ?
        AND YEAR(s.work_date) = YEAR(CURDATE())
      GROUP BY MONTH(s.work_date), u.id, u.full_name
      ORDER BY month ASC;
    `;

    const [rows] = await connection.query(query, [doctorId]);
    connection.release();

    if (rows.length === 0) {
      return res.status(404).json({
        code: 404,
        message: 'Không tìm thấy dữ liệu doanh thu cho bác sĩ này'
      });
    }

    const doctorName = rows[0].full_name;
    const monthlyData = [];
    for (let month = 1; month <= 12; month++) {
      const found = rows.find(row => row.month === month);
      monthlyData.push({
        month: month,
        monthName: getMonthName(month),
        completedCount: found ? found.completed_count : 0,
        revenueVnd: found ? found.revenue_vnd : 0
      });
    }

    res.status(200).json({
      code: 200,
      message: 'Lấy doanh thu bác sĩ thành công!',
      data: {
        doctorId: doctorId,
        doctorName: doctorName,
        year: new Date().getFullYear(),
        monthlyRevenue: monthlyData,
        totalRevenue: monthlyData.reduce((sum, item) => sum + item.revenueVnd, 0),
        totalAppointments: monthlyData.reduce((sum, item) => sum + item.completedCount, 0)
      }
    });

  } catch (error) {
    console.error('❌ Lỗi khi lấy doanh thu bác sĩ:', error);
    res.status(500).json({
      code: 500,
      message: 'Lỗi server: ' + error.message
    });
  }
});

/**
 * Health Check Endpoint
 */
app.get('/health', (req, res) => {
  res.status(200).json({
    status: 'OK',
    message: 'Revenue Service is running',
    timestamp: new Date().toISOString()
  });
});

// =====================================================
// HELPER FUNCTIONS
// =====================================================

/**
 * Chuyển đổi số tháng thành tên tháng tiếng Việt
 */
function getMonthName(month) {
  const monthNames = [
    'Tháng 1', 'Tháng 2', 'Tháng 3', 'Tháng 4', 'Tháng 5', 'Tháng 6',
    'Tháng 7', 'Tháng 8', 'Tháng 9', 'Tháng 10', 'Tháng 11', 'Tháng 12'
  ];
  return monthNames[month - 1] || '';
}

// =====================================================
// ERROR HANDLING
// =====================================================

// 404 Not Found
app.use((req, res) => {
  res.status(404).json({
    code: 404,
    message: 'API endpoint không tồn tại'
  });
});

// =====================================================
// START SERVER
// =====================================================

server.listen(PORT, () => {
  console.log(`🚀 SmartCare Service đang chạy trên port ${PORT}`);
  console.log(`\n📊 Revenue API Endpoints:`);
  console.log(`   GET /api/revenue/monthly       - Doanh thu theo tháng`);
  console.log(`   GET /api/revenue/summary       - Tóm tắt doanh thu năm`);
  console.log(`   GET /api/revenue/doctors/:id   - Doanh thu của bác sĩ`);
  console.log(`   GET /health                    - Health check`);
  console.log(`\n💬 Socket.IO Chat Events:`);
  console.log(`   'user-login'                   - Đăng nhập để map userId <-> socketId`);
  console.log(`   'send-message'                 - Gửi tin nhắn real-time`);
  console.log(`   'receive-message'              - Nhận tin nhắn`);
  console.log(`\n🔗 WebSocket endpoint: ws://localhost:${PORT}/socket.io`);
});

module.exports = app;
