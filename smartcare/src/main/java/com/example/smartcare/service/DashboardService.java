package com.example.smartcare.service;

import com.example.smartcare.dto.AdminDashboardResponse;
import com.example.smartcare.enums.AppointmentStatus;
import com.example.smartcare.enums.Role;
import com.example.smartcare.repository.AppointmentRepository;
import com.example.smartcare.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class DashboardService {

    private final UserRepository userRepository;
    private final AppointmentRepository appointmentRepository;

    public AdminDashboardResponse getAdminStats() {
        // 1. Đếm số lượng User theo Role (Dùng Java Stream cho nhanh)
        long patients = userRepository.findAll().stream().filter(u -> u.getRole() == Role.PATIENT).count();
        long doctors = userRepository.findAll().stream().filter(u -> u.getRole() == Role.DOCTOR).count();

        // 2. Đếm số ca khám
        long totalAppt = appointmentRepository.count();
        long completedAppt = appointmentRepository.countByStatus(AppointmentStatus.COMPLETED);

        // 3. Xử lý dữ liệu thống kê theo khoa từ Query Object[] sang Map
        List<Object[]> statsRaw = appointmentRepository.countAppointmentsBySpecialty();
        Map<String, Long> specialtyStats = new HashMap<>();
        for (Object[] row : statsRaw) {
            specialtyStats.put((String) row[0], (Long) row[1]);
        }

        return AdminDashboardResponse.builder()
                .totalPatients(patients)
                .totalDoctors(doctors)
                .totalAppointments(totalAppt)
                .completedAppointments(completedAppt)
                .appointmentsBySpecialty(specialtyStats)
                .build();
    }
}