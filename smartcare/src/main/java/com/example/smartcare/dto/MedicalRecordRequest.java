package com.example.smartcare.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MedicalRecordRequest {
    
    @NotNull(message = "ID ca khám không được để trống!")
    private Long appointmentId;
    
    @NotBlank(message = "Vui lòng nhập chẩn đoán bệnh!")
    private String diagnosis;
    
    // Đơn thuốc dưới dạng JSON string: [{"name": "Thuốc A", "quantity": 2, "note": "2 lần/ngày"}, ...]
    private String prescription;
    
    private String notes;
}