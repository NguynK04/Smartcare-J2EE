package com.example.smartcare.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import java.util.Map;

@Getter
@Setter
@Builder
public class AdminDashboardResponse {
    private long totalPatients;
    private long totalDoctors;
    private long totalAppointments;
    private long completedAppointments;
    private Map<String, Long> appointmentsBySpecialty; // Thống kê theo chuyên khoa
}