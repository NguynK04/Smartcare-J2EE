package com.example.smartcare.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class DoctorResponse {
    private Long doctorId;
    private String fullName;
    private String specialtyName;
}