package com.example.smartcare.dto;

import com.example.smartcare.enums.AppointmentStatus;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdateAppointmentStatusRequest {
    
    @NotNull(message = "Trạng thái không được để trống")
    private AppointmentStatus status;

}