package com.example.smartcare.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AppointmentRequest {
    
    @NotNull(message = "Vui lòng chọn mã lịch khám (scheduleId)")
    private Long scheduleId;

    // Lý do khám có thể để trống hoặc điền tùy ý
    private String reason; 
}