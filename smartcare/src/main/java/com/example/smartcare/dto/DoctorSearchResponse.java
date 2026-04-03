package com.example.smartcare.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class DoctorSearchResponse {
    private Long id;
    private String fullName;
    private String specialtyName;
}