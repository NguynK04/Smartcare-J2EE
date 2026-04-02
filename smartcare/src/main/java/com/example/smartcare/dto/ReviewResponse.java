package com.example.smartcare.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import java.time.LocalDateTime;

@Getter
@Setter
@Builder
public class ReviewResponse {
    private String patientName;
    private Integer rating;
    private String comment;
    private LocalDateTime createdAt;
}