package com.example.smartcare.service;

import lombok.RequiredArgsConstructor;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async; // Import thư viện Async
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EmailService {

    private final JavaMailSender mailSender;

    // THÊM NÓN @Async ĐỂ BẢO SPRING BOOT: "MÀY CỨ CHẠY NGẦM ĐI, ĐỪNG BẮT USER CHỜ!"
    @Async 
    public void sendAppointmentConfirmation(String toEmail, String patientName, String doctorName, String date, String time) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom("smartcare.hospital.noreply@gmail.com"); // Hiển thị người gửi
            message.setTo(toEmail);
            message.setSubject("Xác nhận đặt lịch khám thành công - SmartCare");
            message.setText("Xin chào " + patientName + ",\n\n" +
                    "Bạn đã đặt lịch khám thành công tại hệ thống SmartCare.\n" +
                    "Bác sĩ phụ trách: " + doctorName + "\n" +
                    "Ngày khám: " + date + "\n" +
                    "Khung giờ: " + time + "\n\n" +
                    "Vui lòng đến đúng giờ. Cảm ơn bạn đã tin tưởng SmartCare!");
            
            mailSender.send(message);
            System.out.println("Đã gửi email thành công đến: " + toEmail);
        } catch (Exception e) {
            System.err.println("Lỗi khi gửi email: " + e.getMessage());
        }
    }

    // Gửi email khi bác sĩ XÁC NHẬN lịch khám
    @Async
    public void sendAppointmentConfirmedEmail(String toEmail, String patientName, String doctorName, String date, String time) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom("smartcare.hospital.noreply@gmail.com");
            message.setTo(toEmail);
            message.setSubject("Lịch khám đã được xác nhận - SmartCare");
            message.setText("Xin chào " + patientName + ",\n\n" +
                    "Bác sĩ " + doctorName + " đã xác nhận lịch khám của bạn.\n\n" +
                    "Chi tiết lịch khám:\n" +
                    "- Bác sĩ: " + doctorName + "\n" +
                    "- Ngày khám: " + date + "\n" +
                    "- Khung giờ: " + time + "\n\n" +
                    "Vui lòng đến đúng giờ và mang theo thẻ bảo hiểm (nếu có).\n" +
                    "Cảm ơn bạn đã tin tưởng SmartCare!");
            
            mailSender.send(message);
            System.out.println("Đã gửi email xác nhận lịch khám đến: " + toEmail);
        } catch (Exception e) {
            System.err.println("Lỗi khi gửi email xác nhận: " + e.getMessage());
        }
    }

    // Gửi email khi bác sĩ HỦY lịch khám
    @Async
    public void sendAppointmentCancelledEmail(String toEmail, String patientName, String doctorName, String date, String time) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom("smartcare.hospital.noreply@gmail.com");
            message.setTo(toEmail);
            message.setSubject("Lịch khám bị hủy - SmartCare");
            message.setText("Xin chào " + patientName + ",\n\n" +
                    "Rất tiếc! Bác sĩ " + doctorName + " đã hủy lịch khám của bạn.\n\n" +
                    "Chi tiết lịch khám bị hủy:\n" +
                    "- Bác sĩ: " + doctorName + "\n" +
                    "- Ngày khám: " + date + "\n" +
                    "- Khung giờ: " + time + "\n\n" +
                    "Vui lòng liên hệ với bộ phận tiếp nhận để đặt lại lịch khám hoặc tìm một bác sĩ khác.\n" +
                    "Vấn đề gì thắc mắc, hãy liên hệ: support@smartcare.vn");
            
            mailSender.send(message);
            System.out.println("Đã gửi email hủy lịch khám đến: " + toEmail);
        } catch (Exception e) {
            System.err.println("Lỗi khi gửi email hủy lịch: " + e.getMessage());
        }
    }

    // Gửi email thông báo cho BÁC SĨ khi có bệnh nhân ĐẶT LỊCH
    @Async
    public void sendDoctorNotificationEmail(String doctorEmail, String doctorName, String patientName, String date, String time, String reason) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom("smartcare.hospital.noreply@gmail.com");
            message.setTo(doctorEmail);
            message.setSubject("Có bệnh nhân đặt lịch khám với bạn - SmartCare");
            message.setText("Xin chào Bác sĩ " + doctorName + ",\n\n" +
                    "Có bệnh nhân mới vừa đặt lịch khám với bạn.\n\n" +
                    "Chi tiết lịch khám:\n" +
                    "- Bệnh nhân: " + patientName + "\n" +
                    "- Ngày khám: " + date + "\n" +
                    "- Khung giờ: " + time + "\n" +
                    "- Lý do khám: " + reason + "\n\n" +
                    "Vui lòng đăng nhập vào hệ thống SmartCare để xác nhận hoặc từ chối lịch khám này.\n" +
                    "Cảm ơn bạn!");
            
            mailSender.send(message);
            System.out.println("Đã gửi email thông báo cho bác sĩ: " + doctorEmail);
        } catch (Exception e) {
            System.err.println("Lỗi khi gửi email thông báo bác sĩ: " + e.getMessage());
        }
    }

    // Gửi email thông báo BÁC SĨ khi tài khoản được DUYỆT
    @Async
    public void sendDoctorApprovalEmail(String doctorEmail, String doctorName) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom("smartcare.hospital.noreply@gmail.com");
            message.setTo(doctorEmail);
            message.setSubject("Tài khoản bác sĩ được phê duyệt - SmartCare");
            message.setText("Xin chào Bác sĩ " + doctorName + ",\n\n" +
                    "Chúc mừng! Tài khoản bác sĩ của bạn đã được Admin phê duyệt.\n\n" +
                    "Bây giờ bạn có thể:\n" +
                    "- Đăng nhập vào hệ thống SmartCare\n" +
                    "- Quản lý lịch làm việc của mình\n" +
                    "- Nhận và xác nhận lịch khám bệnh nhân\n" +
                    "- Tạo ghi chép y tế\n\n" +
                    "Nếu bạn có bất kỳ thắc mắc nào, vui lòng liên hệ bộ phận hỗ trợ: support@smartcare.vn\n" +
                    "Cảm ơn bạn đã là một phần của SmartCare!");
            
            mailSender.send(message);
            System.out.println("Đã gửi email phê duyệt cho bác sĩ: " + doctorEmail);
        } catch (Exception e) {
            System.err.println("Lỗi khi gửi email phê duyệt bác sĩ: " + e.getMessage());
        }
    }
}