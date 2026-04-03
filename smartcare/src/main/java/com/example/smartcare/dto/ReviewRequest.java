package com.example.smartcare.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ReviewRequest {
    private Long appointmentId;  // ✅ Thêm field: ID ca khám
    private Integer rating;
    private String comment;
}