package com.example.smartcare.service;

import com.example.smartcare.dto.ReviewRequest;
import com.example.smartcare.entity.Review;
import com.example.smartcare.entity.User;
import com.example.smartcare.enums.AppointmentStatus;
import com.example.smartcare.enums.Role;
import com.example.smartcare.repository.AppointmentRepository;
import com.example.smartcare.repository.ReviewRepository;
import com.example.smartcare.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ReviewService {

    private final ReviewRepository reviewRepository;
    private final UserRepository userRepository;
    private final AppointmentRepository appointmentRepository;

    /**
     * Tạo đánh giá qua Appointment ID (từ trang review của bệnh nhân)
     */
    public String createReviewByAppointment(Long appointmentId, ReviewRequest request) {
        // 1. Lấy người đang đăng nhập (bệnh nhân)
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User patient = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy user!"));

        if (patient.getRole() != Role.PATIENT) {
            throw new RuntimeException("Chỉ bệnh nhân mới được phép đánh giá!");
        }

        // 2. Tìm appointment
        var appointment = appointmentRepository.findById(appointmentId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy ca khám!"));

        // 3. Kiểm tra xem appointment có phải của bệnh nhân này không
        if (!appointment.getPatient().getId().equals(patient.getId())) {
            throw new RuntimeException("Lỗi bảo mật: Bạn không được phép đánh giá ca khám của người khác!");
        }

        // 4. Kiểm tra trạng thái ca khám phải COMPLETED
        if (appointment.getStatus() != AppointmentStatus.COMPLETED) {
            throw new RuntimeException("Lỗi logic: Chỉ có thể đánh giá ca khám đã hoàn thành!");
        }

        // 5. Lấy bác sĩ từ appointment
        User doctor = appointment.getSchedule().getDoctor();

        // 6. Kiểm tra SPAM: không được đánh giá 2 lần cùng ca khám
        boolean alreadyReviewed = reviewRepository.existsByPatientIdAndDoctorId(patient.getId(), doctor.getId());
        if (alreadyReviewed) {
            throw new RuntimeException("Lỗi spam: Bạn đã đánh giá bác sĩ này rồi. Không thể đánh giá nhiều lần!");
        }

        // 7. Kiểm tra rating hợp lệ
        if (request.getRating() < 1 || request.getRating() > 5) {
            throw new RuntimeException("Số sao đánh giá phải từ 1 đến 5!");
        }

        // 8. Lưu đánh giá
        Review review = Review.builder()
                .patient(patient)
                .doctor(doctor)
                .rating(request.getRating())
                .comment(request.getComment())
                .build();

        reviewRepository.save(review);
        return "Cảm ơn bạn đã đánh giá Bác sĩ " + doctor.getFullName() + "!";
    }

    public String createReview(Long doctorId, ReviewRequest request) {
        // 1. Lấy người đang đăng nhập
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User patient = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy user!"));

        if (patient.getRole() != Role.PATIENT) {
            throw new RuntimeException("Chỉ bệnh nhân mới được phép đánh giá!");
        }

        // 2. Tìm bác sĩ
        User doctor = userRepository.findById(doctorId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy bác sĩ này!"));

        // 3. VAN BẢO VỆ CHÍNH: Kiểm tra xem đã từng khám xong chưa?
        boolean hasCompletedAppointment = appointmentRepository
                .existsByPatientIdAndScheduleDoctorIdAndStatus(patient.getId(), doctorId, AppointmentStatus.COMPLETED);

        if (!hasCompletedAppointment) {
            throw new RuntimeException("Lỗi logic: Bạn chưa từng khám hoặc ca khám chưa hoàn thành, không được phép đánh giá bác sĩ này!");
        }

        // ==========================================
        // VAN CHỐNG SPAM 
        // ==========================================
        boolean alreadyReviewed = reviewRepository.existsByPatientIdAndDoctorId(patient.getId(), doctorId);
        if (alreadyReviewed) {
            throw new RuntimeException("Lỗi spam: Bạn đã đánh giá bác sĩ này rồi. Không thể đánh giá nhiều lần!");
        }
        // ==========================================

        // 4. Lưu đánh giá
        if (request.getRating() < 1 || request.getRating() > 5) {
            throw new RuntimeException("Số sao đánh giá phải từ 1 đến 5!");
        }

        Review review = Review.builder()
                .patient(patient)
                .doctor(doctor)
                .rating(request.getRating())
                .comment(request.getComment())
                .build();

        reviewRepository.save(review);
        return "Cảm ơn bạn đã đánh giá Bác sĩ " + doctor.getFullName() + "!";
    }

    public java.util.List<com.example.smartcare.dto.ReviewResponse> getDoctorReviews(Long doctorId) {
        return reviewRepository.findByDoctorIdOrderByCreatedAtDesc(doctorId).stream()
                .map(r -> com.example.smartcare.dto.ReviewResponse.builder()
                        .patientName(r.getPatient().getFullName())
                        .rating(r.getRating())
                        .comment(r.getComment())
                        .createdAt(r.getCreatedAt())
                        .build())
                .toList();
    }
}