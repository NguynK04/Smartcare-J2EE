package com.example.smartcare.repository;

import com.example.smartcare.entity.Review;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ReviewRepository extends JpaRepository<Review, Long> {
    
// Hàm check chống Spam: Bệnh nhân X đã đánh giá Bác sĩ Y bao giờ chưa?
    boolean existsByPatientIdAndDoctorId(Long patientId, Long doctorId);

    // Lấy danh sách review của bác sĩ, mới nhất xếp trước
    java.util.List<Review> findByDoctorIdOrderByCreatedAtDesc(Long doctorId);
}