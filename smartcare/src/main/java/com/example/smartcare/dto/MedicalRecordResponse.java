package com.example.smartcare.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
public class MedicalRecordResponse {
    private Long recordId;
    private String doctorName;
    private String diagnosis;
    private String prescription;
    private String notes;
    private LocalDateTime createdAt;
}