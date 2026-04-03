package com.example.smartcare.repository;

import com.example.smartcare.entity.MedicalRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional; // Bổ sung dòng này là hết lỗi ngay!

@Repository
public interface MedicalRecordRepository extends JpaRepository<MedicalRecord, Long> {
    
    // Tìm bệnh án dựa vào ID của phiếu khám
    Optional<MedicalRecord> findByAppointmentId(Long appointmentId);
}