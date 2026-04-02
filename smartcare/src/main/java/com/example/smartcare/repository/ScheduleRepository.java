package com.example.smartcare.repository;

import com.example.smartcare.entity.Schedule;
import com.example.smartcare.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface ScheduleRepository extends JpaRepository<Schedule, Long> {
    // Spring Data JPA đã lo hết các hàm save(), findById(), findAll()...
    List<Schedule> findByIsBookedFalseOrderByWorkDateAscStartTimeAsc();
    
    // Lấy lịch khám trống của bác sĩ cụ thể
    List<Schedule> findByDoctorAndIsBookedFalseOrderByWorkDateAscStartTimeAsc(User doctor);
}