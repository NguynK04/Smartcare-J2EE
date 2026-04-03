package com.example.smartcare.service;

import com.example.smartcare.dto.ScheduleRequest;
import com.example.smartcare.dto.ScheduleResponse;
import com.example.smartcare.entity.Schedule;
import com.example.smartcare.entity.User;
import com.example.smartcare.enums.Role;
import com.example.smartcare.mapper.ScheduleMapper;
import com.example.smartcare.repository.ScheduleRepository;
import com.example.smartcare.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ScheduleService {

    private final ScheduleRepository scheduleRepository;
    private final UserRepository userRepository; // THÊM kho chứa User

    @Transactional
    public ScheduleResponse createSchedule(ScheduleRequest request) {
        
        if (request.getStartTime().isAfter(request.getEndTime()) || request.getStartTime().equals(request.getEndTime())) {
            throw new RuntimeException("Giờ kết thúc phải lớn hơn giờ bắt đầu!");
        }

        // 1. LẤY TÊN ĐĂNG NHẬP CỦA NGƯỜI ĐANG GỌI API (Từ thẻ JWT)
        String currentUsername = SecurityContextHolder.getContext().getAuthentication().getName();

        // 2. TÌM NGƯỜI ĐÓ TRONG DATABASE
        User currentDoctor = userRepository.findByUsername(currentUsername)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy thông tin người dùng!"));

        // 3. KIỂM TRA QUYỀN: Chỉ BÁC SĨ mới được tạo lịch
        if (currentDoctor.getRole() != Role.DOCTOR) {
            throw new RuntimeException("Lỗi phân quyền: Chỉ Bác sĩ mới được phép tạo lịch làm việc!");
        }

        // 4. LƯU LỊCH KÈM THEO THÔNG TIN BÁC SĨ
        Schedule schedule = Schedule.builder()
                .workDate(request.getWorkDate())
                .startTime(request.getStartTime())
                .endTime(request.getEndTime())
                .isBooked(false)
                .doctor(currentDoctor) // Gắn chặt Bác sĩ vào Lịch này
                .build();

        Schedule savedSchedule = scheduleRepository.save(schedule);

        return ScheduleMapper.toResponse(savedSchedule);
    }
    public java.util.List<ScheduleResponse> getAvailableSchedules() {
        // 1. Nhờ Repository gọi SQL lấy danh sách
        java.util.List<Schedule> availableSchedules = scheduleRepository.findByIsBookedFalseOrderByWorkDateAscStartTimeAsc();
        
        // 2. Biến đổi danh sách Entity (Schedule) thành danh sách DTO (ScheduleResponse) để trả về
        return availableSchedules.stream()
                .map(ScheduleMapper::toResponse)
                .toList();
    }

    // Lấy lịch khám trống của một bác sĩ cụ thể
    public java.util.List<ScheduleResponse> getAvailableSchedulesByDoctor(Long doctorId) {
        User doctor = userRepository.findById(doctorId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy bác sĩ với ID: " + doctorId));

        java.util.List<Schedule> availableSchedules = scheduleRepository
                .findByDoctorAndIsBookedFalseOrderByWorkDateAscStartTimeAsc(doctor);
        
        return availableSchedules.stream()
                .map(ScheduleMapper::toResponse)
                .toList();
    }
}