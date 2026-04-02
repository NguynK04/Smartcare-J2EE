package com.example.smartcare.controller;

import com.example.smartcare.dto.ApiResponse;
import com.example.smartcare.dto.ReviewRequest;
import com.example.smartcare.service.ReviewService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class ReviewController {

    private final ReviewService reviewService;

    /**
     * API: POST /api/v1/reviews
     * Bệnh nhân đánh giá bác sĩ qua appointment ID
     */
    @PostMapping("/reviews")
    public ResponseEntity<ApiResponse<String>> createReviewByAppointment(
            @RequestBody ReviewRequest request) {

        String message = reviewService.createReviewByAppointment(request.getAppointmentId(), request);

        return ResponseEntity.status(201).body(ApiResponse.<String>builder()
                .code(201)
                .message(message)
                .build());
    }

    /**
     * API: POST /api/v1/doctors/{doctorId}/reviews
     * (Legacy endpoint - vẫn giữ để backward compatibility)
     */
    @PostMapping("/doctors/{doctorId}/reviews")
    public ResponseEntity<ApiResponse<String>> rateDoctor(
            @PathVariable("doctorId") Long doctorId,
            @RequestBody ReviewRequest request) {

        String message = reviewService.createReview(doctorId, request);

        return ResponseEntity.ok(ApiResponse.<String>builder()
                .code(200)
                .message(message)
                .build());
    }

    // API: GET /api/v1/doctors/{doctorId}/reviews
    @GetMapping("/doctors/{doctorId}/reviews")
    public ResponseEntity<ApiResponse<java.util.List<com.example.smartcare.dto.ReviewResponse>>> getReviews(@PathVariable("doctorId") Long doctorId) {
        return ResponseEntity.ok(ApiResponse.<java.util.List<com.example.smartcare.dto.ReviewResponse>>builder()
                .code(200)
                .message("Lấy danh sách đánh giá thành công!")
                .data(reviewService.getDoctorReviews(doctorId))
                .build());
    }
}