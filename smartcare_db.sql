-- --------------------------------------------------------
-- Máy chủ:                      127.0.0.1
-- Phiên bản máy chủ:            9.6.0 - MySQL Community Server - GPL
-- HĐH máy chủ:                  Win64
-- HeidiSQL Phiên bản:           12.15.0.7171
-- --------------------------------------------------------

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET NAMES utf8 */;
/*!50503 SET NAMES utf8mb4 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

-- Đang kết xuất đổ cấu trúc cho bảng smartcare_db.appointments
CREATE TABLE IF NOT EXISTS `appointments` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `created_at` datetime(6) NOT NULL,
  `reason` text COLLATE utf8mb4_general_ci,
  `status` enum('CANCELLED','COMPLETED','CONFIRMED','PENDING') COLLATE utf8mb4_general_ci NOT NULL,
  `patient_id` bigint NOT NULL,
  `schedule_id` bigint NOT NULL,
  PRIMARY KEY (`id`),
  KEY `FKopb2h9yhin1rb4dqote8bws6w` (`patient_id`),
  KEY `FK20g4fjnwy7g8i5yt9vc1kr923` (`schedule_id`),
  CONSTRAINT `FK20g4fjnwy7g8i5yt9vc1kr923` FOREIGN KEY (`schedule_id`) REFERENCES `schedules` (`id`),
  CONSTRAINT `FKopb2h9yhin1rb4dqote8bws6w` FOREIGN KEY (`patient_id`) REFERENCES `users` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=11 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- Đang kết xuất đổ dữ liệu cho bảng smartcare_db.appointments: ~6 rows (xấp xỉ)
INSERT INTO `appointments` (`id`, `created_at`, `reason`, `status`, `patient_id`, `schedule_id`) VALUES
	(1, '2026-03-27 14:21:30.552772', 'Dạo này code Đồ án nhiều nên tôi hay bị đau đầu chóng mặt.', 'COMPLETED', 2, 5),
	(6, '2026-03-27 23:03:17.140513', 'Test tính năng gửi Email báo cáo Đồ án', 'CONFIRMED', 2, 10),
	(7, '2026-03-28 03:23:48.478730', 'nhức đầu\n', 'CONFIRMED', 3, 11),
	(8, '2026-03-28 03:39:01.113821', 'đau bụng\n', 'CONFIRMED', 5, 6),
	(9, '2026-03-30 03:14:47.268300', 'con em bị sốt, đau đầu và tiêu chảy, cháu 3 tuổi, không dị ứng thuốc và sức khỏe trước đó ổn', 'COMPLETED', 8, 13),
	(10, '2026-03-30 05:47:46.937939', 'đau mắt', 'CANCELLED', 8, 14);

-- Đang kết xuất đổ cấu trúc cho bảng smartcare_db.chat_messages
CREATE TABLE IF NOT EXISTS `chat_messages` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `created_at` datetime(6) NOT NULL,
  `message_content` longtext COLLATE utf8mb4_general_ci,
  `receiver_id` bigint NOT NULL,
  `sender_id` bigint NOT NULL,
  PRIMARY KEY (`id`),
  KEY `idx_sender_receiver` (`sender_id`,`receiver_id`),
  KEY `idx_created_at` (`created_at`),
  KEY `FKand7mh9iu4kt3n1tn2w9i9of0` (`receiver_id`),
  CONSTRAINT `FKand7mh9iu4kt3n1tn2w9i9of0` FOREIGN KEY (`receiver_id`) REFERENCES `users` (`id`),
  CONSTRAINT `FKgiqeap8ays4lf684x7m0r2729` FOREIGN KEY (`sender_id`) REFERENCES `users` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- Đang kết xuất đổ dữ liệu cho bảng smartcare_db.chat_messages: ~4 rows (xấp xỉ)
INSERT INTO `chat_messages` (`id`, `created_at`, `message_content`, `receiver_id`, `sender_id`) VALUES
	(1, '2026-03-30 05:59:32.175407', 'xin chao', 1, 8),
	(2, '2026-03-30 06:01:00.938573', 'xin chao', 1, 8),
	(3, '2026-03-30 06:14:13.986713', 'xin chao', 1, 8),
	(4, '2026-03-30 06:22:06.238172', 'xin chao', 1, 8),
	(5, '2026-03-30 06:22:43.480389', 'xin chao', 1, 9);

-- Đang kết xuất đổ cấu trúc cho bảng smartcare_db.medical_records
CREATE TABLE IF NOT EXISTS `medical_records` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `created_at` datetime(6) DEFAULT NULL,
  `diagnosis` text COLLATE utf8mb4_general_ci NOT NULL,
  `notes` text COLLATE utf8mb4_general_ci,
  `prescription` text COLLATE utf8mb4_general_ci,
  `appointment_id` bigint NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UK2nyonrbplqq716buy7u4ghmt8` (`appointment_id`),
  CONSTRAINT `FKifeec8p5v06rt258odelw8s7j` FOREIGN KEY (`appointment_id`) REFERENCES `appointments` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- Đang kết xuất đổ dữ liệu cho bảng smartcare_db.medical_records: ~0 rows (xấp xỉ)
INSERT INTO `medical_records` (`id`, `created_at`, `diagnosis`, `notes`, `prescription`, `appointment_id`) VALUES
	(1, '2026-03-27 19:52:54.525254', 'Bệnh nhân bị rối loạn tiền đình do thức khuya code Đồ án Spring Boot quá nhiều.', 'Nhắc bệnh nhân đi ngủ sớm trước 12h đêm, bớt nhờ AI debug lại cho đỡ nhức đầu.', '1. Panadol Extra (Uống 2 viên/ngày)\n2. Bổ não Ginkgo Biloba', 1),
	(2, '2026-03-30 05:09:24.780232', 'abcd', 'không thức khuya', '[{"name":"panadol","quantity":10,"note":"1"}]', 9);

-- Đang kết xuất đổ cấu trúc cho bảng smartcare_db.reviews
CREATE TABLE IF NOT EXISTS `reviews` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `comment` text COLLATE utf8mb4_general_ci,
  `created_at` datetime(6) DEFAULT NULL,
  `rating` int NOT NULL,
  `doctor_id` bigint NOT NULL,
  `patient_id` bigint NOT NULL,
  PRIMARY KEY (`id`),
  KEY `FKmtd4ungrr68jpndc5gf67827v` (`doctor_id`),
  KEY `FKp6ff3lit060ehcuyc5artangi` (`patient_id`),
  CONSTRAINT `FKmtd4ungrr68jpndc5gf67827v` FOREIGN KEY (`doctor_id`) REFERENCES `users` (`id`),
  CONSTRAINT `FKp6ff3lit060ehcuyc5artangi` FOREIGN KEY (`patient_id`) REFERENCES `users` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- Đang kết xuất đổ dữ liệu cho bảng smartcare_db.reviews: ~0 rows (xấp xỉ)
INSERT INTO `reviews` (`id`, `comment`, `created_at`, `rating`, `doctor_id`, `patient_id`) VALUES
	(1, 'Bác sĩ khám chán quá!', '2026-03-27 22:03:29.915839', 1, 1, 2),
	(2, 'bác sĩ tận tâm và yêu trẻ con', '2026-03-30 23:40:07.005539', 5, 7, 8);

-- Đang kết xuất đổ cấu trúc cho bảng smartcare_db.schedules
CREATE TABLE IF NOT EXISTS `schedules` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `end_time` time NOT NULL,
  `is_booked` bit(1) NOT NULL,
  `start_time` time NOT NULL,
  `version` bigint DEFAULT NULL,
  `work_date` date NOT NULL,
  `doctor_id` bigint NOT NULL,
  PRIMARY KEY (`id`),
  KEY `FKhh7q3ryrcnpun7i6w37ckx71v` (`doctor_id`),
  CONSTRAINT `FKhh7q3ryrcnpun7i6w37ckx71v` FOREIGN KEY (`doctor_id`) REFERENCES `users` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=15 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- Đang kết xuất đổ dữ liệu cho bảng smartcare_db.schedules: ~9 rows (xấp xỉ)
INSERT INTO `schedules` (`id`, `end_time`, `is_booked`, `start_time`, `version`, `work_date`, `doctor_id`) VALUES
	(5, '11:30:00', b'1', '08:30:00', 2, '2026-05-20', 1),
	(6, '11:30:00', b'1', '08:30:00', 3, '2026-05-20', 1),
	(7, '09:30:00', b'0', '09:00:00', 2, '2026-05-25', 1),
	(8, '10:30:00', b'1', '10:00:00', 1, '2026-06-01', 1),
	(9, '10:30:00', b'1', '10:00:00', 1, '2026-06-01', 1),
	(10, '10:30:00', b'1', '10:00:00', 1, '2026-06-01', 1),
	(11, '10:30:00', b'1', '07:30:00', 1, '2026-03-29', 1),
	(12, '22:30:00', b'0', '20:30:00', 0, '2026-03-28', 6),
	(13, '11:12:00', b'1', '08:30:00', 1, '2026-03-30', 7),
	(14, '11:30:00', b'0', '08:30:00', 2, '2026-03-30', 6);

-- Đang kết xuất đổ cấu trúc cho bảng smartcare_db.specialties
CREATE TABLE IF NOT EXISTS `specialties` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `description` text COLLATE utf8mb4_general_ci,
  `name` varchar(255) COLLATE utf8mb4_general_ci NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UKbhb8s9o5hv30lkbidtod9cixc` (`name`)
) ENGINE=InnoDB AUTO_INCREMENT=10 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- Đang kết xuất đổ dữ liệu cho bảng smartcare_db.specialties: ~4 rows (xấp xỉ)
INSERT INTO `specialties` (`id`, `description`, `name`) VALUES
	(1, 'Chuyên khám và điều trị các bệnh lý liên quan đến tim mạch, huyết áp.', 'Khoa Tim Mạch'),
	(2, 'Chuyên khám và điều trị các bệnh lý liên quan đến tim mạch, huyết áp.', 'Khoa Răng Hàm Mặt'),
	(3, 'Chuyên khám và điều trị các bệnh lý liên quan đến tim mạch, huyết áp.', 'Nhãn Khoa'),
	(4, 'Chuyên khám và điều trị các bệnh lý liên quan đến tim mạch, huyết áp.', 'Nhi Khoa');

-- Đang kết xuất đổ cấu trúc cho bảng smartcare_db.users
CREATE TABLE IF NOT EXISTS `users` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `email` varchar(255) COLLATE utf8mb4_general_ci NOT NULL,
  `full_name` varchar(255) COLLATE utf8mb4_general_ci NOT NULL,
  `is_active` bit(1) NOT NULL,
  `password` varchar(255) COLLATE utf8mb4_general_ci NOT NULL,
  `role` enum('ADMIN','DOCTOR','PATIENT','STAFF') COLLATE utf8mb4_general_ci NOT NULL,
  `username` varchar(50) COLLATE utf8mb4_general_ci NOT NULL,
  `specialty_id` bigint DEFAULT NULL,
  `cv_file_path` varchar(500) COLLATE utf8mb4_general_ci DEFAULT NULL,
  `license_number` varchar(50) COLLATE utf8mb4_general_ci DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UK6dotkott2kjsp8vw4d0m25fb7` (`email`),
  UNIQUE KEY `UKr43af9ap4edm43mmtq01oddj6` (`username`),
  KEY `FKih74rpug03pac4o5mqt12j9cl` (`specialty_id`),
  CONSTRAINT `FKih74rpug03pac4o5mqt12j9cl` FOREIGN KEY (`specialty_id`) REFERENCES `specialties` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=11 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- Đang kết xuất đổ dữ liệu cho bảng smartcare_db.users: ~10 rows (xấp xỉ)
INSERT INTO `users` (`id`, `email`, `full_name`, `is_active`, `password`, `role`, `username`, `specialty_id`, `cv_file_path`, `license_number`) VALUES
	(1, 'khoa@gmail.com', 'Bác sĩ Khoa', b'1', '$2a$10$LqdjmeKsSFXPhWmVg3zGYOPBLOsEPBNHK1K/bIGvlgBfLW7kO5/.O', 'DOCTOR', 'khoadoan', 1, NULL, NULL),
	(2, 'nguyenkhoa010804@gmail.com', 'Bệnh Nhân A', b'1', '$2a$10$l7o21Ap0TTfd/8zjsWoMl.QG0zrW5Lb3dGdt.StBctCVMiYa59ZAS', 'PATIENT', 'benhnhanA', NULL, NULL, NULL),
	(3, 'bnb@gmail.com', 'Bệnh Nhân B', b'1', '$2a$10$l7o21Ap0TTfd/8zjsWoMl.QG0zrW5Lb3dGdt.StBctCVMiYa59ZAS', 'PATIENT', 'benhnhanB', NULL, NULL, NULL),
	(4, 'admin1@gmail.com', 'Quản trị viên Tối cao', b'1', '$2a$10$HE3sERUPdStujcVBP3246.lXVJrHSrNGTRhg8VEr/4Md9hk0JRTe2', 'ADMIN', 'admin1', NULL, NULL, NULL),
	(5, 'nguyenkhoa0914044556@gmail.com', 'Nguyễn Khoa', b'1', '$2a$10$eUjamdP.j40CQRABM2MqTemiOBRs.MV3t3cyDOYy2ZWzDJ/FQeSP.', 'PATIENT', 'khoa123', NULL, NULL, NULL),
	(6, 'nguyenkhoa96392@gmail.com', 'Khoa Nguyễn', b'1', '$2a$10$QOKH.AVtFSFhwMQMZUz9gekbZncZZt0bCQr5xQzZcjlC.HfX2b25K', 'DOCTOR', 'bskhoa', 3, NULL, NULL),
	(7, 'hamyduyen196585@gmail.com', 'Hà Mỹ Duyên', b'1', '$2a$10$PQPTZJeecalRtdF4HK32XeqaiE.902nbmNMnuW2klxIHsLkclOO/S', 'DOCTOR', 'hmd1', 4, NULL, NULL),
	(8, 'hoaugnguyen@gmail.com', 'Nguyễn Đỗ Hoàng ', b'1', '$2a$10$gY1UvqekCHqFEFT/0zKd5O5IaTi42/NNNYJWG4qz0nWqjgFIoPbg.', 'PATIENT', 'Phúc bệnh nhân', NULL, NULL, NULL),
	(9, 'neih@gmail.com', 'Phan Ngọc Hiển', b'1', '$2a$10$LDfITyYO79B/ysiRbYOlTOM3DGcTm/LRrS4OZwhVCmjbpKDlh5Ft2', 'STAFF', 'neih', NULL, NULL, NULL),
	(10, 'minh@123', 'Vũ Quang Minh', b'0', '$2a$10$qnpa62tV6ToIOps8YI1HROOE6Nh8JIL36RDvZSftakibQv2.snYUm', 'DOCTOR', 'minh1', NULL, 'uploads\\cv\\cv_1774828683527_NguyenKhoa_2280601517 (2).docx', '123456');

/*!40103 SET TIME_ZONE=IFNULL(@OLD_TIME_ZONE, 'system') */;
/*!40101 SET SQL_MODE=IFNULL(@OLD_SQL_MODE, '') */;
/*!40014 SET FOREIGN_KEY_CHECKS=IFNULL(@OLD_FOREIGN_KEY_CHECKS, 1) */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40111 SET SQL_NOTES=IFNULL(@OLD_SQL_NOTES, 1) */;
