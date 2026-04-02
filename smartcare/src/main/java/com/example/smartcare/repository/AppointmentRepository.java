package com.example.smartcare.repository;

import com.example.smartcare.entity.Appointment;
import com.example.smartcare.entity.User;
import com.example.smartcare.enums.AppointmentStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AppointmentRepository extends JpaRepository<Appointment, Long> {
    
    // Tìm lịch sử khám của Bệnh nhân
    List<Appointment> findByPatientOrderByCreatedAtDesc(User patient);
    
    // Tìm danh sách bệnh nhân đặt lịch của Bác sĩ
    List<Appointment> findByScheduleDoctorOrderByCreatedAtDesc(User doctor);

    // Đếm tổng số ca khám theo trạng thái (Ví dụ: COMPLETED)
    long countByStatus(AppointmentStatus status);

    // Query thần thánh: Thống kê số lượng ca khám cho mỗi Chuyên khoa
    @Query("SELECT a.schedule.doctor.specialty.name, COUNT(a) " +
           "FROM Appointment a " +
           "WHERE a.schedule.doctor.specialty IS NOT NULL " +
           "GROUP BY a.schedule.doctor.specialty.name")
    List<Object[]> countAppointmentsBySpecialty();

    boolean existsByPatientIdAndScheduleDoctorIdAndStatus(Long patientId, Long doctorId, AppointmentStatus status);
}