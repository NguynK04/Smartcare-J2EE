package com.example.smartcare.controller;

import com.example.smartcare.dto.ApiResponse;
import com.example.smartcare.dto.ScheduleRequest;
import com.example.smartcare.dto.ScheduleResponse;
import com.example.smartcare.service.ScheduleService;
import jakarta.validation.Valid; // Bắt buộc phải có
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/schedules")
@RequiredArgsConstructor
public class ScheduleController {

    private final ScheduleService scheduleService;

    @PostMapping
    // Thêm @Valid ngay trước @RequestBody để kích hoạt người gác cổng
    public ResponseEntity<ApiResponse<ScheduleResponse>> createSchedule(@Valid @RequestBody ScheduleRequest request) {
        ScheduleResponse createdSchedule = scheduleService.createSchedule(request);
        
        ApiResponse<ScheduleResponse> response = ApiResponse.<ScheduleResponse>builder()
                .code(HttpStatus.CREATED.value())
                .message("Tạo lịch làm việc thành công")
                .data(createdSchedule)
                .build();
                
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/available")
    public ResponseEntity<ApiResponse<java.util.List<ScheduleResponse>>> getAvailableSchedules(
            @RequestParam(value = "doctorId", required = false) Long doctorId) {
        
        java.util.List<ScheduleResponse> data;
        if (doctorId != null) {
            data = scheduleService.getAvailableSchedulesByDoctor(doctorId);
        } else {
            data = scheduleService.getAvailableSchedules();
        }

        ApiResponse<java.util.List<ScheduleResponse>> response = ApiResponse.<java.util.List<ScheduleResponse>>builder()
                .code(org.springframework.http.HttpStatus.OK.value())
                .message("Lấy danh sách lịch khám trống thành công")
                .data(data)
                .build();

        return ResponseEntity.ok(response);
    }
}